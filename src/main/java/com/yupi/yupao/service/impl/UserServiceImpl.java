package com.yupi.yupao.service.impl;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.contant.UserConstant;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.model.User;
import com.yupi.yupao.service.UserService;
import com.yupi.yupao.mapper.TagMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import static com.yupi.yupao.contant.UserConstant.ADMIN_ROLE;
import static com.yupi.yupao.contant.UserConstant.USER_LOGIN_STATE;

/**
* @author LENOVO
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-09-07 21:22:33
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<TagMapper, User>
    implements UserService {

    /**
     * 盐值
     * 混淆密码
     */

    private static String SALT = "yupi";

    /**
     * 用户登录态键
     */
    private static String USER_LOGIN_STATE = "userLoginState";
    @Autowired
    private TagMapper userMapper;
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1，校验
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return -1;
        }
        if(userAccount.length() < 4){
            return -1;
        }
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            return -1;
        }
        //账户不能包含特殊字符
        String validPattern = "^[a-zA-Z0-9_]{3,12}+$";
//        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        boolean matcher = userAccount.matches(validPattern);
        if(!matcher) {
            return -1;
        }
        if(!userPassword.equals(checkPassword)){
            return -1;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            return -1;
        }
        //2，加密
        final String SALT = "wangbo";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setStatus(0);
        user.setIsDelete(0);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }


    @Override
    public User userLogin(String userAccount, String userPassword,HttpServletRequest request) {
        //1，校验
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        if(userAccount.length() < 4){
            return null;
        }
        if(userPassword.length() < 6){
            return null;
        }
        //账户不能包含特殊字符
        String validPattern = "^[a-zA-Z0-9_]{3,13}+$";
//        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        boolean matcher = userAccount.matches(validPattern);
        if(!matcher) {
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        queryWrapper.eq("user_password",userPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if(user == null) {
//            log.info("user login failed,userCount cant match userPassword");
            return null;
        }
        //用户脱敏
//        User safetyUser = new User();
//        safetyUser.setId(user.getId());
//        safetyUser.setUsername(user.getUsername());
//        safetyUser.setUserAccount(user.getUserAccount());
//        safetyUser.setAvatarUrl(user.getAvatarUrl());
//        safetyUser.setGender(user.getGender());
//        safetyUser.setPhone(user.getPhone());
//        safetyUser.setEmail(user.getEmail());
//        safetyUser.setStatus(user.getStatus());
//        safetyUser.setCreateTime(new Date());
//        safetyUser.setUpdateTime(new Date());
        //存储用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
        return user;
    }


    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setStatus(originUser.getStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     *
     * 根据标签查询用户
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            String tags = user.getTags();
            if (StringUtils.isBlank(tags)){
                return false;
            }
            List<String> tempNameList = gson.fromJson(tags, new TypeToken<List<String>>(){}.getType());
            Set<String> tempNameSet = new HashSet<>(tempNameList);
//            gson.toJson(tempNameSet); 反序列化
            for(String tagName : tagNameList){
                if(!tempNameSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     *
     * 根据姓名查询用户
     * @param username
     * @return
     */
    @Override
    public User searchUserByName(String username) {
        return userMapper.selectByName(username);
    }

    /**
     * 获取用户登录信息
     * @param httpRequest
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest httpRequest) {
        if (httpRequest == null){
            return null;
        }
        Object attribute = httpRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if (attribute == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User)attribute;
    }

    /**
     * 更新用户信息
     * @param user
     * @param loginUser
     * @return
     */
    @Override
    public int updateUser(User user, User loginUser) {
        Long userId = user.getId();
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return userMapper.updateById(user);
    }

    /**
     * 判断当前用户是否为管理员
     * @param user
     * @return
     */
    public boolean isAdmin(User user){
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }
    /**
     * 判断用户是否是管理员
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request) {
        //仅管理员查询
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}