package com.qin.dcesp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qin.dcesp.entity.GraphData;
import com.qin.dcesp.service.SocketService;
import com.qin.dcesp.utils.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableTransactionManagement
public class DcespApplication implements ApplicationRunner , CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(DcespApplication.class);

    @Autowired
    private SocketService socketService;

    public static void initBaseData(){
        //初始化时加载文件,读取实验电路的连接,加载到一个List和两个Map中.
        String fileName = "baseData.json";
        StringBuilder jsonString = new StringBuilder();
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        try {
            InputStream inputStream = classPathResource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            int c = -1;
            while ((c = inputStreamReader.read()) != -1) {
                jsonString.append((char) c);
            }
            inputStreamReader.close();
            inputStream.close();
        }catch (FileNotFoundException e){
            logger.error("找不到该文件!" + e.getMessage());
        } catch (IOException e) {
            logger.error("读取文件出错!" + e.getMessage());
        }
        JSONObject jsonObject = JSON.parseObject(jsonString.toString());
        JSONArray powerLinked = jsonObject.getJSONArray("powerLinked");
        for (int i = 0; i < powerLinked.size(); i++) {
            GraphData graphData = new GraphData();
            JSONObject data = powerLinked.getJSONObject(i);
            graphData.setGraphId(-1);
            graphData.setCdmId(-1);
            graphData.setFrom(data.getString("form"));
            graphData.setTo(data.getString("to"));
            graphData.setFromPort(data.getString("formPort"));
            graphData.setToPort(data.getString("toPort"));
            powerLinkedList.add(graphData);
        }
        JSONObject checkLined = jsonObject.getJSONObject("checkLinked");
        for (String key : checkLined.keySet()) {
            JSONArray jsonArray = checkLined.getJSONArray(key);
            List<GraphData> checkLinked = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject checkObj = jsonArray.getJSONObject(i);
                GraphData graphData = new GraphData();
                graphData.setGraphId(-1);
                graphData.setCdmId(-1);
                graphData.setFrom(checkObj.getString("form"));
                graphData.setFromPort(checkObj.getString("formPort"));
                graphData.setTo(checkObj.getString("to"));
                graphData.setToPort(checkObj.getString("toPort"));
                checkLinked.add(graphData);
            }
            checkLinkedListMap.put(key,checkLinked);
        }
        JSONObject controlLined = jsonObject.getJSONObject("controlLinked");
        for(String key : controlLined.keySet()){
            JSONArray jsonArray = controlLined.getJSONArray(key);
            List<GraphData> controlLinked = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject controlObj = jsonArray.getJSONObject(i);
                GraphData graphData = new GraphData();
                graphData.setGraphId(-1);
                graphData.setCdmId(-1);
                graphData.setFrom(controlObj.getString("form"));
                graphData.setFromPort(controlObj.getString("formPort"));
                graphData.setTo(controlObj.getString("to"));
                graphData.setToPort(controlObj.getString("toPort"));
                controlLinked.add(graphData);
            }
            controlLinkedListMap.put(key,controlLinked);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DcespApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initBaseData();
        socketService.startSocketService();
    }
}
