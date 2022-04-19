package com.qin.dcesp;

import com.qin.dcesp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DcespApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        System.out.println(userService.updatePassword(1, "321"));
        System.out.println(userService.selectUserById(1));
    }

}
