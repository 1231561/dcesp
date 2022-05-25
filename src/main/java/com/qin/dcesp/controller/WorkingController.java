package com.qin.dcesp.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qin.dcesp.entity.Circuitdiagram;
import com.qin.dcesp.entity.Graph;
import com.qin.dcesp.entity.GraphData;
import com.qin.dcesp.entity.User;
import com.qin.dcesp.entity.messageclass.GraphDataFromFront;
import com.qin.dcesp.entity.messageclass.NodeDataFromFront;
import com.qin.dcesp.service.CircuitdiagramService;
import com.qin.dcesp.service.Esp8266Service;
import com.qin.dcesp.service.SocketService;
import com.qin.dcesp.utils.CommunityConstant;
import com.qin.dcesp.utils.GraphDataStringPorcessUtil;
import com.qin.dcesp.utils.HostHolder;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@RequestMapping("/working")
public class WorkingController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(WorkingController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CircuitdiagramService circuitdiagramService;

    @Autowired
    SocketService socketService;

    volatile public AtomicInteger timeoutCount = new AtomicInteger(10);
    volatile public AtomicBoolean timeoutCountStart = new AtomicBoolean(false);

    /**
     * 电路请求处理
     * */
    @PostMapping("/sumitCircuitdiagram")
    @ResponseBody
    public String handleCiruitdiagram(
            @RequestParam(value = "dlt",required = false) String dlt,
            @RequestParam(value = "nodes",required = false) String nodes
    ){
        /*电路图数据格式:
           [
            {form:"连接的源节点,比如74LS20,1",formProt:"连接的源节点的端口",
            to:"本节点,也就是被连到的节点",toPort: "被连到节点与源节点连接的端口"
            },
            {}....
           ]
        * */
        List<GraphDataFromFront> graphDataFromFront = JSONObject.parseArray(dlt,GraphDataFromFront.class);
        List<NodeDataFromFront> nodeDataFromFront = JSONObject.parseArray(nodes,NodeDataFromFront.class);
        logger.info(graphDataFromFront.toString());
        logger.info(nodeDataFromFront.toString());
        logger.info("======================================");
        Map<String,Object> resultMap = new HashMap<>();
        if(dlt.isEmpty()){
            resultMap.put("code","error");
            resultMap.put("msg","请先绘制电路图!");
            return JSON.toJSONString(resultMap);
        }
        User user = hostHolder.getUser();
        resultMap.put("code","success");
        resultMap.put("msg","请求成功");
        //进行图数据封装,存入数据库
        List<GraphData> graphData = new ArrayList<>();//从前端获取的图信息
        for(GraphDataFromFront dat : graphDataFromFront){
            GraphData data = new GraphData();
            data.setFrom(dat.getFrom());
            data.setTo(dat.getTo());
            data.setFromPort(dat.getFromPort());
            data.setToPort(dat.getToPort());
            graphData.add(data);
        }
        logger.info("前端获取的图信息: " + graphData);
        //将节点数据存储,存graphData,但是感觉没必要,不存了

        //进行路径分析,获取电源到接地端和检测端的路径
        Circuitdiagram circuitdiagram = new Circuitdiagram();
        circuitdiagram.setUserId(user.getId());
        Graph graph = new Graph(graphData);
        //获取路径
        Map<String, List<List<String>>> roads = graph.getRoad();
        logger.info("路径 : " + roads.toString());
        //解析路径上的继电器位置
        //==============================================================================
        //首先找需要上电的芯片
        List<List<String>> powerToGround = roads.get("powerToGround");//电源到接地的路径
        List<List<String>> powerToMeasure = roads.get("powerToMeasure");//电源到检测端的路径
        List<List<String>> highPowerTo = roads.get("highPowerTo");//高电平-地
        List<List<String>> lowerPowerTo = roads.get("lowerPowerTo");//低电平-地
        List<List<String>> afterFilterPowerToMeasureData = new ArrayList<>();//过滤后的数据
        //过滤检测端数据,当先出现接地后出现检测端时,该数据直接忽略.
        boolean isGround = false;
        boolean isGroundBeforMeasur = false;
        for(List<String> data : powerToMeasure){
            for(String node : data){
                if(!isGround && (node.contains("电平检测") || node.contains("示波器"))){
                    break;
                }
                if(!isGround && node.contains("接地")){
                    isGround = true;
                }
                if(isGround && (node.contains("电平检测") || node.contains("示波器"))){
                    isGroundBeforMeasur = true;
                }
            }
            if(!isGroundBeforMeasur){
                afterFilterPowerToMeasureData.add(data);
            }
        }
        List<String> needPower = new ArrayList<>();
        for(List<String> road : powerToGround){
            for(String node : road){
                if(node.contains("74LS")){
                    needPower.add(node);
                }
            }
        }
        List<String> needMeasur = new ArrayList<>();
        for(List<String> road : afterFilterPowerToMeasureData){
            for(int i = 0;i < road.size(); i++){
                if(road.get(i).contains("电平检测")){
                    needMeasur.add(road.get(i - 1));
                }
            }
        }
        List<String> needHighControl = new ArrayList<>();
        for(List<String> road : highPowerTo){
            for (String s : road) {
                if (s.contains("74LS")) {
                    needHighControl.add(s);
                }
            }
        }
        List<String> needLowerControl = new ArrayList<>();
        for(List<String> road : lowerPowerTo){
            for(String s : road){
                if(s.contains("74LS")){
                    needLowerControl.add(s);
                }
            }
        }
        //=======================================================================
        //解析完成,开始封装数据
        //=======================================================================
        //包装需要上电的芯片信息.
        Map<String,List<GraphData>> packegData = new HashMap<>();//打包发给单片机的数据
        for(String node : needPower){
            for(GraphData g : powerLinkedList){
                if(node.contains(g.getTo())){
                    List<GraphData> list;
                    if(!packegData.containsKey("powerToNode")){
                        list = new ArrayList<>();
                    }else{
                        list = packegData.get("powerToNode");
                    }
                    list.add(g);
                    packegData.put("powerToNode",list);
                }
            }
        }
        //打包需要检测电平位置的芯片信息
        for(String node : needMeasur){
            for(String key : checkLinkedListMap.keySet()){
                if(key.contains(node.split(",")[0])){
                    for(GraphData g : checkLinkedListMap.get(key)){
                        if(node.contains(g.getFrom())){
                            List<GraphData> list;
                            if(!packegData.containsKey("nodeToCheck")){
                                list = new ArrayList<>();
                            }else{
                                list = packegData.get("nodeToCheck");
                            }
                            list.add(g);
                            packegData.put("nodeToCheck", list);
                        }
                    }
                }
            }
        }
        //打包电平控制端的数据
        for(String node : needHighControl){
            for(String key : controlLinkedListMap.keySet()){
                if(key.contains(node.split(",")[0])){
                    for(GraphData g : controlLinkedListMap.get(key)){
                        if(node.contains(g.getTo())){
                            List<GraphData> list;
                            if(!packegData.containsKey("highPowerTo")){
                                list = new ArrayList<>();
                            }else{
                                list = packegData.get("highPowerTo");
                            }
                            list.add(g);
                            packegData.put("highPowerTo",list);
                        }
                    }
                }
            }
        }
        for(String node : needLowerControl){
            for(String key : controlLinkedListMap.keySet()){
                if(key.contains(node.split(",")[0])){
                    for(GraphData g : controlLinkedListMap.get(key)){
                        if(node.contains(g.getTo())){
                            List<GraphData> list;
                            if(!packegData.containsKey("lowerPowerTo")){
                                list = new ArrayList<>();
                            }else{
                                list = packegData.get("lowerPowerTo");
                            }
                            list.add(g);
                            packegData.put("lowerPowerTo",list);
                        }
                    }
                }
            }
        }
        //======================================================================
        //包装端口信息
        //======================================================================
        //获取需要检测电平位置的芯片的端口信息
        List<GraphData> nodeToCheckData = packegData.get("nodeToCheck");
        List<GraphData> filterCheckData = new ArrayList<>();
        if(nodeToCheckData != null && nodeToCheckData.size() > 0){
            for(GraphData data : graphData){
                for(GraphData checkNode : nodeToCheckData){
                    String dataForm = data.getFrom();
                    String dataFormPort = data.getFromPort();
                    String dataTo = data.getTo();
                    String dataToPort = data.getToPort();
                    String checkNodeForm = checkNode.getFrom();
                    String checkNodeFormPort = checkNode.getFromPort().split("_")[1];
                    if(dataForm.contains(checkNodeForm)){
                        if(dataFormPort.equals(checkNodeFormPort)){
                            filterCheckData.add(checkNode);
                        }
                    }else if(dataTo.contains(checkNodeForm)){
                        if(dataToPort.equals(checkNodeFormPort)){
                            filterCheckData.add(checkNode);
                        }
                    }
                }
            }
            packegData.put("nodeToCheck",filterCheckData);
        }
        //打包控制端信息
        List<GraphData> highPowerToData = packegData.get("highPowerTo");
        List<GraphData> lowerPowerToData = packegData.get("lowerPowerTo");
        List<GraphData> packageHighPowerTo = new ArrayList<>();
        List<GraphData> packageLowerPowerTo = new ArrayList<>();
        for(GraphData data : graphData){
            if(highPowerToData != null && highPowerToData.size() > 0){
                for(GraphData highNode : highPowerToData){
                    String dataForm = data.getFrom();
                    String dataFormPort = data.getFromPort();
                    String dataTo = data.getTo();
                    String dataToPort = data.getToPort();
                    String highTo = highNode.getTo();
                    String highToPort = highNode.getToPort().split("_")[1];
                    if(dataForm.contains("高电平")){
                        if(highTo.contains(dataTo.split(",")[0])){
                            if(dataToPort.equals(highToPort)){
                                packageHighPowerTo.add(highNode);
                            }
                        }
                    }else if(dataTo.contains("高电平")){
                        if(highTo.contains(dataForm.split(",")[0])){
                            if(dataFormPort.equals(highToPort)){
                                packageHighPowerTo.add(highNode);
                            }
                        }
                    }
                }
            }
            if(lowerPowerToData != null && lowerPowerToData.size() > 0){
                for(GraphData lowerNode : lowerPowerToData){
                    String dataForm = data.getFrom();
                    String dataFormPort = data.getFromPort();
                    String dataTo = data.getTo();
                    String dataToPort = data.getToPort();
                    String lowerTo = lowerNode.getTo();
                    String lowerToPort = lowerNode.getToPort().split("_")[1];
                    if(dataForm.contains("低电平")){
                        if(lowerTo.contains(dataTo.split(",")[0])){
                            if(dataToPort.equals(lowerToPort)){
                                packageLowerPowerTo.add(lowerNode);
                            }
                        }
                    }else if (dataTo.contains("低电平")){
                        if(lowerTo.contains(dataForm.split(",")[0])){
                            if(dataFormPort.equals(lowerToPort)){
                                packageLowerPowerTo.add(lowerNode);
                            }
                        }
                    }
                }
            }
        }
        if(!packageLowerPowerTo.isEmpty()){
            packegData.put("lowerPowerTo",packageLowerPowerTo);
        }
        if(!packageHighPowerTo.isEmpty()){
            packegData.put("highPowerTo",packageHighPowerTo);
        }

        //=======================================================================

        //处理数据,打包单片机端方便处理的格式==========================================
        /* 拟定数据结构: Map<String,List<String>>
         * 原先的格式:
         * {
         *      lowerPowerTo : [GraphData(cdmId=-1, graphId=-1, from=control, fromPort=control_port5, to=74LS20, toPort=74LS20_port12)],
         *      powerToNode : [GraphData(cdmId=-1, graphId=-1, from=power, fromPort=power_port2, to=74LS20, toPort=74LS20_port14)],
         *      highPowerTo : [GraphData(cdmId=-1, graphId=-1, from=control, fromPort=control_port4, to=74LS20, toPort=74LS20_port13)],
         *      nodeToCheck : [GraphData(cdmId=-1, graphId=-1, from=74LS20, fromPort=74LS20_port8, to=check, toPort=check_port1)]
         * }
         * 拟定格式:
         * 注意点: 当前为单实验模式,也就是单次实验只能使用1个芯片.
         *  {
         *      lowerPowerTo : ["c5",...],//表示开启八路继电器的哪路,并且是输出低电平
         *      powerToNode : ["p2",...],//表示开启继电器的哪一路,注,此处是使用了3-8译码器的,可能可以再封装一次
         *      highPowerTo : ["c4",...],//同lowerPowerTo
         *      nodeToCheck : ["c1",...] //表示那一端检查端需要进行检查.
         *  }
         * */
        Map<String,Set<String>> sendData = new HashMap<>();//最终要发送的数据
        sendData.put("lowerPowerTo",new HashSet<>());
        sendData.put("powerToNode",new HashSet<>());
        sendData.put("highPowerTo",new HashSet<>());
        sendData.put("nodeToCheck",new HashSet<>());
        for(String key : packegData.keySet()){
            switch (key) {
                case "lowerPowerTo" -> {
                    List<GraphData> lpt = packegData.get("lowerPowerTo");
                    Set<String> lptdata = sendData.get("lowerPowerTo");
                    for (GraphData data : lpt) {
                        String port = GraphDataStringPorcessUtil.getPort(data.getFromPort());
                        lptdata.add("C" + port);
                    }
                    sendData.put("lowerPowerTo", lptdata);
                }
                case "powerToNode" -> {
                    List<GraphData> ptn = packegData.get("powerToNode");
                    Set<String> ptndata = sendData.get("powerToNode");
                    for (GraphData data : ptn) {
                        String port = GraphDataStringPorcessUtil.getPort(data.getFromPort());
                        ptndata.add("P" + port);
                    }
                    sendData.put("powerToNode", ptndata);
                }
                case "highPowerTo" -> {
                    List<GraphData> hpt = packegData.get("highPowerTo");
                    Set<String> hptdata = sendData.get("highPowerTo");
                    for (GraphData data : hpt) {
                        String port = GraphDataStringPorcessUtil.getPort(data.getFromPort());
                        hptdata.add("C" + port);
                    }
                    sendData.put("highPowerTo", hptdata);
                }
                case "nodeToCheck" -> {
                    List<GraphData> ntc = packegData.get("nodeToCheck");
                    Set<String> ntcdata = sendData.get("nodeToCheck");
                    for (GraphData data : ntc) {
                        String port = GraphDataStringPorcessUtil.getPort(data.getToPort());
                        ntcdata.add("C" + port);
                    }
                    sendData.put("nodeToCheck", ntcdata);
                }
                default ->{
                    //do nothing
                }
            }
        }
        //=======================================================================
        logger.info("打包的数据: " + packegData);
        logger.info("二次封装的数据: "  + JSON.toJSONString(sendData));
        //封装完成,准备调用单片机处理
        if(socketService.getEsp8266ServiceMap() == null || socketService.getEsp8266ServiceMap().size() == 0){
            logger.info("===========1当前没有可用客户机!请联系管理员!=============");
            resultMap.put("code","error");
            resultMap.put("msg","无客户机!");
            return JSON.toJSONString(resultMap);
        }
        //查询所有已连接的单片机状态,查看是否有非忙碌状态的
        Map<String, Esp8266Service> map = socketService.getEsp8266ServiceMap();
        Map<String,Socket> socketMap = socketService.getSocketMap();
        Esp8266Service client = null;
        List<String> delSocketNameList = new ArrayList<>();
        for (String key : map.keySet()){
            if(ESP8266WAITTING.equals(map.get(key).getStatus()) && !map.get(key).getSocket().isClosed()) {
                client = map.get(key);
                //数据发送前,确保客户端在线,发送一个HB心跳包,只要不出异常,就能往下走
                try{
                    //socket.sendUrgentData("HB".getBytes());//发送校验位,如果连接实际上是断开的话会导致异常
                    //DataInputStream in = new DataInputStream(socket.getInputStream());

                    Socket socket = client.getSocket();
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.write("HB".getBytes());//发送检测数据,如果出现异常则说明有问题
                    out.flush();
                    logger.info("本连接不存在问题");
                }catch (IOException e) {
                    //如果存在异常,就把这个socket从map里删了.
                    logger.error("该链接: " + key + "存在异常,可能已经断开,它将被删除");
                    delSocketNameList.add(key);
                }
            }
        }
        for(String delName : delSocketNameList){
            map.remove(delName);
            socketMap.remove(delName);
            logger.info("删除1条连接");
        }
        try {
            Thread.sleep(5000);
            Thread.sleep(1000);
            Thread.sleep(1000);
            logger.info("延时完成");
        }catch (InterruptedException e){
            logger.error("延时出错");
        }

        //删除之后如果没东西了,就说明不存在客户端,直接返回
        if(map.isEmpty()){
            logger.info("===========2当前没有可用客户机!请联系管理员!=============");
            resultMap.put("code","error");
            resultMap.put("msg","无客户机!");
            return JSON.toJSONString(resultMap);
        }else{
            for(String key : map.keySet()){
                client = map.get(key);
                break;
            }
        }
        Set<String> set = new HashSet<>();
        Set<String> finalReData = Collections.synchronizedSet(set);
        if(client != null){
            AtomicReference<StringBuffer> getMessage = new AtomicReference<>(new StringBuffer());
            AtomicReference<String> getM = new AtomicReference<>("");
            //当有非忙碌状态的单片机时,将其置为忙碌状态,并且取得与它的连接,开始与其通信
            logger.info("存在非忙碌的客户机,进行连接");
            client.setStatus(ESP8266BUSY);
            Socket socket = client.getSocket();
            if(socket.isConnected()){
                logger.info("取得与客户端:" + socket.getInetAddress() + ":" + socket.getPort() + " 的连接,开始通信");
                //连接状态时,进行消息发送
                try{
                    socket.setSoTimeout(3000);
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    //发送数据
                    while(in.available() != 0){
                        in.skip(in.available());
                        logger.info("忽略前一次剩下的数据");
                    }
                    out.writeUTF(JSON.toJSONString(sendData));
                    out.flush();
                    logger.info("发送数据完成,发送数据为: " + sendData);
                    Callable<AtomicReference<String>> task = ()->{
                        while(true) {
                            logger.info("等待接收数据");

                            /*阻塞区域---------------------------*/
                            //保持连接,进行数据收发
                            int available = 0;
                            while (available == 0) {
                                available = in.available();
                            }
                            timeoutCount.set(10);
                            logger.info("数据长度: " + available);
                            byte[] buffer = new byte[available];
                            in.read(buffer);//阻塞
                            /*阻塞区域---------------------------*/

                            getMessage.set(new StringBuffer());
                            for (int i = 0; i < buffer.length; i++) {
                                if (i != buffer.length - 1) {
                                    if (buffer[i] < 0) {
                                        getMessage.get().append((buffer[i] + 256)).append(",");
                                    } else if (buffer[i] == 48) {
                                        getMessage.get().append(0).append(",");
                                    } else if (buffer[i] >= 0) {
                                        getMessage.get().append(buffer[i]).append(",");
                                    }
                                } else {
                                    if (buffer[i] < 0) {
                                        getMessage.get().append((buffer[i] + 256));
                                    } else if (buffer[i] == 48) {
                                        getMessage.get().append(0);
                                    } else if (buffer[i] >= 0) {
                                        getMessage.get().append(buffer[i]);
                                    }
                                }
                            }
                            getM.set(new String(buffer));
                            if(getM.get().contains(ESP8266FINISH)){
                                logger.info("客户端发送结束连接请求 : " + getM.get());
                                break;
                            }else{
                                logger.info("本次返回的结果 : " + getMessage.get());
                                finalReData.add(getMessage.get().toString());
                            }
                            logger.info("获取消息完成");
                        }
                        return getM;
                    };
                    FutureTask<AtomicReference<String>> futureTask = new FutureTask<>(task);
                    logger.info("启动任务");
                    new Thread(futureTask).start();
                    futureTask.get(120, TimeUnit.SECONDS);
                }catch (IOException e){
                    logger.info("获取流失败!" + e.getMessage());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    //超时
                    logger.info("接收数据超时!" + e.getMessage());
                    resultMap.put("error","接收数据超时!");
                    return JSON.toJSONString(resultMap);
                } finally {
                    client.setStatus(ESP8266WAITTING);
                }
            }
        }else{
            logger.info("===========3当前没有可用客户机!请联系管理员!=============");
            resultMap.put("code","error");
            resultMap.put("msg","无客户机!");
            return JSON.toJSONString(resultMap);
        }
        //通信完成后,更新连接状态为非忙碌,并且释放流资源.
        /*封装回传数据*/
        //finalReData中包含了回传的数据,为adc:[300个取样点](ADC精度为12位,一次传输8位.构建波形数据),check:100个GPIOC端口的取样点(防抖)
        String[] needReData = new String[700];
        for (String reData : finalReData) {
            String[] tempdata = reData.split(",");
            if(tempdata.length == 700){
                System.arraycopy(tempdata, 0, needReData, 0, 700);
                break;
            }
        }
        if(needReData[0] == null){
            logger.info("===========未接受到正确数据=============");
            resultMap.put("code","error");
            resultMap.put("msg","未接受到数据!,请再次提交!");
            return JSON.toJSONString(resultMap);
        }
        //首先处理ADC数据,根据是否有示波器,判断是否要封装adc数据数组回去.
        boolean hasSBQ = false;//默认没有
        String sbqName = "";
        for(GraphDataFromFront n : graphDataFromFront){
            if(n.getFrom().contains("示波器") || n.getTo().contains("示波器")){
                hasSBQ = true;
                sbqName = n.getFrom().contains("示波器") ? n.getFrom() : n.getTo();
            }
        }
        if(hasSBQ){
            double[] resultAdc = new double[300];
            int resultAdcIndex = 0;
            //对回传ADC数据进行处理,每两位数据为一个取样点,前一个数据为高八位,后一个数据为低八位.
            //得到的数据是12位的,也就是区分4096个,故每一个采样点电压值为 : 3.3 * 12位的值 / 4096;
            for(int i = 0;i < 300;){
                int high = Integer.parseInt(needReData[i++]);
                int lower = Integer.parseInt(needReData[i++]);
                high = high < 0 ? high + 256 : high;
                lower = lower < 0 ? lower + 256 : lower;
                int pointer = (high << 8) | lower;
                resultAdc[resultAdcIndex++] = (double) pointer / 4096 * 3.3;
            }
            resultMap.put("sbqName",sbqName);
            resultMap.put("adcData",resultAdc);
        }
        //处理电平检测数据并封装
        if(null != sendData.get("nodeToCheck") && !sendData.get("nodeToCheck").isEmpty()){
            logger.info("needData : " + Arrays.toString(needReData));
            resultMap.put("nodeToCheck",sendData.get("nodeToCheck"));
            int[] resultGpiocDataArr = new int[100];
            for(int i = 600;i < needReData.length;i++){
                resultGpiocDataArr[i - 600] = Integer.parseInt(needReData[i]);
            }
            resultMap.put("gpiocData",resultGpiocDataArr);
        }
        logger.info("-----------------数据打包完成,返回给前端------------------");
        return JSON.toJSONString(resultMap);
    }
}
