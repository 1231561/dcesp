package com.qin.dcesp.service;

import com.qin.dcesp.utils.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 进行Socket通信
 * 作为服务器端
 * 与ESP8266建立连接
 * */
@Component
public class SocketService implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(SocketService.class);

    //服务器端口
    private static final int SERVICE_PORT = 10005;

    //线程池
    private ExecutorService executorService;

    //ServiceSocket
    private ServerSocket serverSocket;

    //存储多个ScoketService
    private Map<String,Socket> socketMap = new ConcurrentHashMap<>();

    //存储封装的Esp8266Service信息
    private Map<String,Esp8266Service> esp8266ServiceMap = new ConcurrentHashMap<>();
    //存储消息
    private Map<String,String> messageMap = new ConcurrentHashMap<>();

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Map<String, Socket> getSocketMap() {
        return socketMap;
    }

    public void setSocketMap(Map<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }

    public Map<String, Esp8266Service> getEsp8266ServiceMap() {
        return esp8266ServiceMap;
    }

    public void setEsp8266ServiceMap(Map<String, Esp8266Service> esp8266ServiceMap) {
        this.esp8266ServiceMap = esp8266ServiceMap;
    }

    public Map<String, String> getMessageMap() {
        return messageMap;
    }

    public void setMessageMap(Map<String, String> messageMap) {
        this.messageMap = messageMap;
    }

    public void startSocketService() {
        try {
            logger.info("Socket Service is starting.");
            //设置socket端口
            serverSocket = new ServerSocket(SERVICE_PORT);
            //创建线程池
            executorService = Executors.newFixedThreadPool(50);
            for(int i = 0;i < 10;i++){
                //10条线程
                Runnable mainRun = () -> {
                    try {
                        Socket accept = null;//存储连接对象
                        logger.info("等待连接......");
                        accept = serverSocket.accept();
                        //等待连接
                        logger.info("获取连接成功!");
                        Socket socket = accept;
                        String socketName = socket.getInetAddress() + ":" + socket.getPort();
                        socketMap.put(socketName,socket);//存储当前连接的连接对象.
                        //进行Esp8266Service的封装
                        logger.info("开始封装ESP8266Sevice");
                        Esp8266Service esp8266Service = new Esp8266Service();
                        esp8266Service.setSocket(socket);
                        esp8266Service.setStatus(ESP8266WAITTING);
                        esp8266ServiceMap.put(socketName,esp8266Service);
                        logger.info("ESP8266Service封装完毕!退出线程!" + Thread.currentThread().getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                executorService.execute(mainRun);
            }
        }catch (Exception e) {
            logger.error("创建线程池出错!==========" + e.getMessage());
        }
    }
}
