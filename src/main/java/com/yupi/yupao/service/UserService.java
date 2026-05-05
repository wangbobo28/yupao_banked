package com.yupi.yupao.service;

import com.yupi.yupao.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author LENOVO
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-09-07 21:22:33
*/
public interface UserService extends IService<User> {

    /**
     *用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    User userLogin(String userAccount,String userPassword, HttpServletRequest httpServletRequest);


    User getSafetyUser(User originUser);

    /**
     * 根据用户标签来查询用户
     * @param tagList
     * @return
     */
    List<User> searchUserByTags(List<String> tagList);

    User searchUserByName(String name);
}
