package com.qin.dcesp.service;

import com.qin.dcesp.utils.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 进行Socket通信
 * 作为服务器端
 * 与ESP8266建立连接
 * */
@Service
public class SocketService implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(SocketService.class);

    //服务器端口
    private static final int servicePort = 10005;

    //线程池
    private ExecutorService executorService;

    //ServiceSocket
    private ServerSocket serverSocket;

    //存储多个ScoketService
    public Map<String,Socket> socketMap = new ConcurrentHashMap<>();

    //存储封装的Esp8266Service信息
    public Map<String,Esp8266Service> esp8266ServiceMap = new ConcurrentHashMap<>();
    //存储消息
    public Map<String,String> messageMap = new ConcurrentHashMap<>();

    public void startSocketService() {
        try {
            logger.info("Socket Service is starting.");
            //设置socket端口
            serverSocket = new ServerSocket(servicePort);
            //创建线程池
            executorService = Executors.newFixedThreadPool(50);
            Runnable mainRun = () -> {
                try {
                    Socket accept = null;//存储连接对象
                    while((accept = serverSocket.accept()) != null){
                        //等待连接
                        logger.info("获取连接成功!");
                        Socket socket = accept;
                        String socketName = socket.getInetAddress() + ":" + socket.getPort();
                        socketMap.put(socketName,socket);//存储当前连接的连接对象.
                        //进行Esp8266Service的封装
                        Esp8266Service esp8266Service = new Esp8266Service();
                        esp8266Service.setSocket(socket);
                        esp8266Service.setStatus(ESP8266WAITTING);
                        esp8266ServiceMap.put(socketName,esp8266Service);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            executorService.execute(mainRun);
        }catch (Exception e) {
            logger.error("创建线程池出错!==========" + e.getMessage());
        }
    }
    public String replaceMessage(String message){
        //处理数据,清除空格,水平制表符,换行,回车
        String res = "";
        if(message != null){
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(message);
            res = m.replaceAll("");
        }
        return res;
    }
}
