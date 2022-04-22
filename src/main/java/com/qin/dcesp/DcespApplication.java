package com.qin.dcesp;

import com.qin.dcesp.service.SocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DcespApplication implements ApplicationRunner {

    @Autowired
    private SocketService socketService;

    public static void main(String[] args) {
        SpringApplication.run(DcespApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        socketService.startSocketService();
    }
}
