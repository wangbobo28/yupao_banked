package com.yupi.yupao.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.mapper.UserMapper;
import com.yupi.yupao.model.User;
import com.yupi.yupao.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 缓存预热任务
 */

@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private UserService userService;


    private List<Long> mainUserList = Arrays.asList(1L);

    @Scheduled(cron = "0 59 15 * * ?")
    public void doCacheRecommendUser(){
        for (Long userId : mainUserList){
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            Page<User> userPage = userService.page(new Page<>(1,20),userQueryWrapper);
            String redisKey = String.format("yupao:user:recommend:%s",userId);
            try {
                redisTemplate.opsForValue().set(redisKey,userPage,10, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("redis is set key error");
            }
        }

    }
}
