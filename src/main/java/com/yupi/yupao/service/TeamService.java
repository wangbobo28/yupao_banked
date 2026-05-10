package com.yupi.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupao.model.Team;
import com.yupi.yupao.model.User;
import com.yupi.yupao.model.request.TeamAddRequest;

/**
* @author LENOVO
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2026-05-10 11:42:18
*/
public interface TeamService extends IService<Team> {
    /**
     * 添加队伍
     */
    long addTeam(Team team, User loginUser);

}
