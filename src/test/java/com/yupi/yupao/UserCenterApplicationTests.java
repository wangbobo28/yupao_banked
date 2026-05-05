package com.yupi.yupao;

//import com.yupao.usercenter.service.UserService;
import com.yupi.yupao.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserCenterApplicationTests {

    @Resource
    private UserService userService;
    @Test
    void contextLoads() {

    }

    @Test
    void register(){
        String userAccount = "yupi3";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long l = userService.userRegister(userAccount, userPassword, checkPassword);
        System.out.println(l);
    }

}
