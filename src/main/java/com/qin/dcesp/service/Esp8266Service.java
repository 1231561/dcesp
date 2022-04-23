package com.qin.dcesp.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 与ESP8266进行通讯的主要类
 * */
public class Esp8266Service {

    private static final Logger logger = LoggerFactory.getLogger(Esp8266Service.class);

    private String status;

    private Socket socket;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    //获取当前连接的ESP8266状态
    public String getEsp8266Status(){
        return status;
    }

    public Socket getSocket() {
        return socket;
    }

    //发送数据
    public boolean senData(String data){
        if(socket == null){
            logger.error("发送数据失败!当前没有连接!");
            return false;
        }
        try{
            OutputStream outputStream = socket.getOutputStream();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        outputStream.write(data.getBytes());
                        logger.info("ESP8266Service发送数据: " + data);
                        outputStream.flush();
                    } catch (IOException e) {
                        logger.error("ESP8266发送数据失败!!! : " + e.getMessage());
                    }
                }
            },1000);
        } catch (IOException e) {
            logger.error("ESP8266Service获取输出流失败!:"+e.getMessage());
            return false;
        }
        return true;
    }

    //接收数据


}
