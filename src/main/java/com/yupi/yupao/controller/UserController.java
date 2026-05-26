package com.yupi.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.ResultUtils;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.model.User;
import com.yupi.yupao.model.request.UserLoginRequest;
import com.yupi.yupao.model.request.UserRegisterRequest;
import com.yupi.yupao.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
//        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return result == -1 ? ResultUtils.error(1, "注册失败", "已经存在用户") : ResultUtils.success(result);
    }

    /**
     *
     * @param
     * @param
     * @return
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        final String SALT = "wangbo";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        System.out.println(encryptPassword);
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userAccount == null || userPassword == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, encryptPassword, request);
        return ResultUtils.success(user);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
//        if (!isAdmin(request)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
//        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(userList);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
//        if (!isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH);
//        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        System.out.println(id);
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }
    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = null;
        if(request.getSession(false) != null){
            userObj = request.getSession(false).getAttribute("userLoginState");
        }
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
//        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(user);
    }

    /**
     * 修改用户信息
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        //检验参数是否为空
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 查询所有用户信息
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> selectUsers(@RequestParam(value = "pageSize",defaultValue = "10") long pageSize,
                                                @RequestParam(value = "pageNum",defaultValue = "1")long pageNum,
                                                HttpServletRequest request){
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        User loginUser = userService.getLoginUser(request);
        //如果有缓存 直接读缓存
        String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null){
            return ResultUtils.success(userPage);
        }
        // 1. 参数校验（避免恶意请求）
        if (pageSize <= 0) {
            pageSize = 10;
        }
        if (pageSize > 100) {  // 限制最大100条，防止性能问题
            pageSize = 100;
        }
        if (pageNum <= 0) {
            pageNum = 1;
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum,pageSize),userQueryWrapper);
        try {
            valueOperations.set(redisKey,userPage,10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis is set key error");
        }
        return ResultUtils.success(userPage);
    }

}
