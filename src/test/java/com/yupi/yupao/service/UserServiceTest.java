package com.yupi.yupao.service;
import java.util.Arrays;
import java.util.List;

//import com.yupao.usercenter.model.User;
//import com.yupao.usercenter.service.UserService;
import com.yupi.yupao.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userservice;

//    @Resource
//    private User user;
//
//    @Test
//    public void testAddUser(){
//        User user = new User();
//        user.setId(0L);
//        user.setUsername("dogyupi1");
//        user.setUserAccount("123");
//        user.setAvatarUrl("https://thirdwx.qlogo.cn/mmopen/vi_32/VpxAqyL1icz4lgzuVPuQMib5vcLbUNIcSickBQmU2IYibE3ZLaFDVZmx36ibgx218NlcLVdlnJ03zPsZRc8vPlarlew/132");
//        user.setGender(0);
//        user.setUserPassword("123");
//        user.setPhone("123");
//        user.setEmail("456");
//        user.setStatus(0);
//        user.setCreateTime(new Date());
//        user.setUpdateTime(new Date());
//        user.setIsDelete(0);
//        boolean result = userservice.save(user);
//        System.out.println(user.getId());
////        Assertions.assertTure(result);
//    }
    @Test
    public void searchUserByTags() {
        List<String> tagList = Arrays.asList("java", "python");
        List<User> list = userservice.searchUserByTags(tagList);
//        Assert.assertNotNull(list);
        System.out.println(list);
    }
    @Test
    public void searchUserByName(){
        User user1 = userservice.searchUserByName("wangli");
        System.out.println(user1);
    }

    @Test
    public void testSearchUserByTags(){
        List<String> strings = Arrays.asList("java", "python");
        List<User> userList = userservice.searchUserByTags(strings);
//        Assert.assertNotnull(userList);
        System.out.println("////////////////////////////////////////////////////////");
        System.out.println(userList);
    }
}