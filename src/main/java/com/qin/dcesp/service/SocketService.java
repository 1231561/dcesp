package com.qin.dcesp.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
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
public class SocketService {
    //private static final Logger logger = LoggerFactory.getLogger(SocketService.class);

    //服务器端口
    private static final int servicePort = 10005;

    //线程池
    private ExecutorService executorService;

    //ServiceSocket
    private ServerSocket serverSocket;

    //存储多个ScoketService
    public Map<String,Socket> socketMap = new ConcurrentHashMap<>();

    //存储消息
    public Map<String,String> messageMap = new ConcurrentHashMap<>();

    public SocketService() {
        try {
            //logger.info("Socket Service is starting.");
            //设置socket端口
            serverSocket = new ServerSocket(servicePort);
            //创建线程池
            executorService = Executors.newFixedThreadPool(50);
            Runnable mainRun = new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket accept = null;//存储连接对象
                        while((accept = serverSocket.accept()) != null){
                            //等待连接
                            Socket socket = accept;
                            socketMap.put(socket.getInetAddress() + ":" + socket.getPort(),socket);//存储当前连接的连接对象.
                            Runnable run = () -> {
                                //每个线程中要做的
                                //logger.info("新连接: " + socket.getInetAddress() + " : " + socket.getPort());
                                try {
                                    BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());//获取输入流
                                    //OutputStream outputStream = socket.getOutputStream();//获取输出流
                                    byte[] buffer = new byte[1024];//缓冲区
                                    int index = -1;
                                    String getMessage = "";
                                    StringBuffer sb = new StringBuffer();
                                    try{
                                        while((index = inputStream.read(buffer)) != -1){
                                            sb.append(new String(buffer,0,index,"gbk"));
                                            //sb就是单片机发送给服务器的消息
                                            getMessage = replaceMessage(sb.toString());
                                            System.out.println("客户端发送消息: " + getMessage);
                                            //存储对应的客户端发送的消息
                                            //messageMap.put(socket.getInetAddress() + ":" + socket.getPort(),getMessage);
                                        }
                                    }catch (SocketException e){
                                        //如果出现异常,那就是客户端断开连接,进行资源释放
                                        System.out.println("客户端断开连接!进行资源释放!");
                                        socketMap.remove(socket.getInetAddress() + ":" + socket.getPort());
                                        socket.close();//关闭连接
                                        inputStream.close();//关闭流
                                    }
                                    //关闭连接后,线程结束
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            };
                            executorService.execute(run);//放入线程池
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            executorService.execute(mainRun);
        }catch (Exception e) {
            //logger.error("创建线程池出错!==========" + e.getMessage());
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
