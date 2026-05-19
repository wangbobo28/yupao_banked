package com.yupi.yupao.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import com.yupao.usercenter.model.User;
//import com.yupao.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yupao.mapper.UserMapper;
import com.yupi.yupao.model.User;
import com.yupi.yupao.model.enums.TeamStatusEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userservice;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

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

    @Test
    public void testUpdateUser(){
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", "wangbo");
        List<User> users = userMapper.selectList(userQueryWrapper);
        System.out.println(users);
//        User user = new User();
//        user.setId(21L);
//        user.setUsername("wangli");
//        user.setUserAccount("wangli123");
//        int i = userservice.updateUser(user);
//        System.out.println("修改成功的数据条数"+i);
    }
    @Test
    public void insertUsers(){
        long max = 1000000;
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            User user = new User();
//            user.setId(1L);
            user.setUsername("假鱼皮");
            user.setUserAccount("123456789");
            user.setAvatarUrl("https://pic2.zhimg.com/v2-86b356a32e20cb5f91c3568614df54a3_1440w.jpg");
            user.setGender(1);
            user.setUserPassword("123456");
            user.setPhone("13800138000");
            user.setEmail("wangbo@example.com");
            user.setTags("[\"Java\",\"Spring\",\"MySQL\"]");
            user.setStatus(0);
            user.setUserRole(0);  // 0-普通用户，1-管理员
//            user.setCreateTime(new Date());
//            user.setUpdateTime(new Date());
            user.setIsDelete(0);
            users.add(user);
        }
        boolean b = userservice.saveBatch(users, 50000);

    }

    @Test
    void testRedis(){
        System.out.println(TeamStatusEnum.getEnumByValue(0));
    }


}