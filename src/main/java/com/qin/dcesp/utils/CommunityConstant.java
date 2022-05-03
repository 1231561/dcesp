package com.qin.dcesp.utils;

import com.qin.dcesp.entity.GraphData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CommunityConstant {

    //激活成功状态
    int ACTIVATION_SUCCESS = 0;
    //重复激活
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;
    //默认凭证的过期时长
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    //记住我的过期时长
    int REMEBER_SECONDS = 3600 * 12 * 12;
    //Esp8266状态:待机
    String ESP8266WAITTING = "watting";
    //Esp8266状态:忙碌
    String ESP8266BUSY = "busy";
    //完成通讯提示指令
    String ESP8266FINISH = "callfinish";
    //实验电路的powerLinked部分
    List<GraphData> powerLinkedList = new ArrayList<>();
    //实验电路的checkLinkedMap部分
    Map<String,List<GraphData>> checkLinkedListMap = new HashMap<>();
    //实验电路的controlLinkedMap部分
    Map<String,List<GraphData>> controlLinkedListMap = new HashMap<>();
}
