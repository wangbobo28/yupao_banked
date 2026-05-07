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

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param httpServletRequest
     * @return
     */
    User userLogin(String userAccount,String userPassword, HttpServletRequest httpServletRequest);


    /**
     * 获取安全用户
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 根据用户标签来查询用户
     * @param tagList
     * @return
     */
    List<User> searchUserByTags(List<String> tagList);

    /**
     * 根据姓名搜索用户
     * @param name
     * @return
     */
    User searchUserByName(String name);

    /**
     * 获取当前用户登录信息
     */
    User getLoginUser(HttpServletRequest httpRequest);

    /**
     * 更新当前用户信息
     */
    int updateUser(User user,User LoginUser);


    /**
     * 是否是管理员
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 是否是管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

}
