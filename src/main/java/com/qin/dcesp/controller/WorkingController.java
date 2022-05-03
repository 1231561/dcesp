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
import com.qin.dcesp.utils.HostHolder;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/working")
public class WorkingController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(WorkingController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CircuitdiagramService workingService;

    @Autowired
    SocketService socketService;

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
        //将节点数据存储

        //进行路径分析,获取电源到接地端和检测端的路径
        Circuitdiagram circuitdiagram = new Circuitdiagram();
        circuitdiagram.setUserId(user.getId());
        Graph graph = new Graph(graphData);
        //获取路径
        Map<String, List<List<String>>> roads = graph.getRoad();
        logger.info("路径 : " + roads.toString());
        //解析路径上的继电器位置
        //首先找需要上电的芯片
        List<List<String>> powerToGround = roads.get("powerToGround");//电源到接地的路径
        List<List<String>> powerToMeasure = roads.get("powerToMeasure");//电源到检测端的路径
        List<List<String>> afterFilterPowerToMeasureData = new ArrayList<>();//过滤后的数据
        //过滤检测端数据,当先出现接地后出现检测端时,该数据直接忽略.
        boolean isGround = false;
        boolean isGroundBeforMeasur = false;
        for(List<String> data : powerToMeasure){
            for(String node : data){
                if(!isGround && node.contains("电平检测")){
                    break;
                }
                if(!isGround && node.contains("接地")){
                    isGround = true;
                }
                if(isGround && node.contains("电平检测")){
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
        //解析完成,开始封装数据
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
        //获取需要检测电平位置的芯片的端口信息
        List<GraphData> nodeToCheckData = packegData.get("nodeToCheck");
        List<GraphData> filterCheckData = new ArrayList<>();
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
        //打包控制端信息

        logger.info("打包的数据: " + packegData.toString());
        //封装完成,准备调用单片机处理
        if(socketService.getEsp8266ServiceMap() == null || socketService.getEsp8266ServiceMap().size() == 0){
            logger.info("===========当前没有可用客户机!请联系管理员!=============");
            resultMap.put("code","error");
            resultMap.put("msg","无客户机!");
            return JSON.toJSONString(resultMap);
        }
        //查询所有已连接的单片机状态,查看是否有非忙碌状态的
        Map<String, Esp8266Service> map = socketService.getEsp8266ServiceMap();
        Esp8266Service client = null;
        for (String key : map.keySet()){
            if(ESP8266WAITTING.equals(map.get(key).getStatus()) && !map.get(key).getSocket().isClosed()){
                client = map.get(key);
            }
        }
        if(client != null){
            //当有非忙碌状态的单片机时,将其置为忙碌状态,并且取得与它的连接,开始与其通信
            logger.info("存在非忙碌的客户机,进行连接");
            client.setStatus(ESP8266BUSY);
            Socket socket = client.getSocket();
            if(socket.isConnected()){
                logger.info("取得与客户端:" + socket.getInetAddress() + ":" + socket.getPort() + " 的连接,开始通信");
                //连接状态时,进行消息发送
                try{
                    InputStream inputStream = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();
                    String message = "连接成功,这是服务器发送的消息";
                    client.senData(message);
                    out.flush();
                    byte[] buffer = new byte[1024];
                    int index = -1;
                    logger.info("发送了一条数据");
                    while((index = inputStream.read(buffer)) != -1){
                        //保持连接,进行数据收发
                        String getMsg = new String(buffer,0,index, StandardCharsets.UTF_8);
                        System.out.println("客户端的消息: " + getMsg);
                        if(getMsg.contains(ESP8266FINISH)){
                            logger.info("通信完成,客户端已发送结束连接请求 : " + getMsg);
                            break;
                        }
                        logger.info("获取一次消息完成");
                        System.out.println("请发送消息: ");
                    }
                    client.setStatus(ESP8266WAITTING);
                }catch (IOException e){
                    client.setStatus(ESP8266WAITTING);
                    System.out.println("获取流失败!" + e.getMessage());
                }
            }
        }else{
            logger.info("============所有客户机都处于忙碌状态!请等待=================");
        }
        //if(client != null){
        //    //当有非忙碌状态的单片机时,将其置为忙碌状态,并且取得与它的连接,开始与其通信
        //    client.setStatus(ESP8266BUSY);
        //    client.senData("与客户端进行连接!!!!!!!-------------TEST消息");
        //    //将继电器位置发给单片机,由单片机完成电路连接
        //    /*准备调用单片机客户端接口,调用Service,在Service中去处理接口调用*/
        //
        //
        //    /*调用完毕*/
        //    client.setStatus(ESP8266WAITTING);
        //}


        //通信完成后,更新连接状态为非忙碌,并且释放流资源.
        /*封装回传数据*/



        return JSON.toJSONString(resultMap);
    }
}
