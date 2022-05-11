package com.qin.dcesp.jobs;

import com.qin.dcesp.service.Esp8266Service;
import com.qin.dcesp.service.SocketService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class SocketConnectionCheckJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(SocketConnectionCheckJob.class);

    @Autowired
    SocketService socketService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //检查已经存在的ESP8266-Socket连接,当存在Socket断开或者失效的时候,将该Socket剔除Map.
        Map<String, Socket> socketMap = socketService.getSocketMap();
        Map<String, Esp8266Service> esp8266ServiceMap = socketService.getEsp8266ServiceMap();
        List<String> disableSocketList = new ArrayList<>();
        for(String socketName : socketMap.keySet()){
            Socket socket = socketMap.get(socketName);
            if(socket.isClosed()){
                //如果当前socket寄了
                logger.info("连接: " + socket + " 寄了,它将会被剔除");
                disableSocketList.add(socketName);
            }
        }
        if(!disableSocketList.isEmpty()){
            //如果是存在寄了的socket
            for(String disableSocketName : disableSocketList){
                socketMap.remove(disableSocketName);
                esp8266ServiceMap.remove(disableSocketName);
            }
        }
    }
}
