package com.qin.dcesp.jobs;

import com.qin.dcesp.service.Esp8266Service;
import com.qin.dcesp.service.SocketService;
import com.qin.dcesp.utils.CommunityConstant;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class ListeningSocketJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(ListeningSocketJob.class);

    @Autowired
    private SocketService socketService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //监听启动的线程池.观察其中的任务是否保持在一定数量,如果少于这个数量,就向里面加任务
        ExecutorService runningThreadPool = socketService.getExecutorService();
        int runningTaskCount = ((ThreadPoolExecutor) runningThreadPool).getActiveCount();
        int corePoolSize = ((ThreadPoolExecutor) runningThreadPool).getCorePoolSize();
        ServerSocket serverSocket = socketService.getServerSocket();
        Map<String, Socket> socketMap = socketService.getSocketMap();
        Map<String, Esp8266Service> esp8266ServiceMap = socketService.getEsp8266ServiceMap();
        if(runningTaskCount < corePoolSize){
            //如果当前任务少于核心线程数,添加任务到线程池中
            for(int i = runningTaskCount;i < corePoolSize;i++){
                runningThreadPool.execute(()->{
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
                });
            }
        }
    }
}
