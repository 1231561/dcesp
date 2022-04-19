package com.qin.dcesp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DcespApplication {

    public static void main(String[] args) {
        SpringApplication.run(DcespApplication.class, args);
    }

}
