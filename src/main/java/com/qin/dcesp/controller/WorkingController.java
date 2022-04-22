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
import com.qin.dcesp.service.SocketService;
import com.qin.dcesp.utils.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/working")
public class WorkingController {

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
        List<GraphData> graphData = new ArrayList<>();
        for(GraphDataFromFront dat : graphDataFromFront){
            GraphData data = new GraphData();
            data.setFrom(dat.getFrom());
            data.setTo(dat.getTo());
            data.setFromPort(dat.getFromPort());
            data.setToPort(dat.getToPort());
            graphData.add(data);
        }
        //将节点数据存储

        //进行路径分析,获取电源到接地端和检测端的路径
        Circuitdiagram circuitdiagram = new Circuitdiagram();
        circuitdiagram.setUserId(user.getId());
        Graph graph = new Graph(graphData);
        //获取路径
        Map<String, List<List<String>>> roads = graph.getRoad();
        logger.info(roads.toString());
        //解析路径上的继电器位置

        //将继电器位置发给单片机,由单片机完成电路连接
        /*准备调用单片机客户端接口,调用Service,在Service中去处理接口调用*/


        /*调用完毕*/
        /*封装回传数据*/



        return JSON.toJSONString(resultMap);
    }
}
