package com.yupi.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupao.common.DeleteRequest;
import com.yupi.yupao.model.Team;
import com.yupi.yupao.model.User;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.model.request.TeamJoinRequest;
import com.yupi.yupao.model.request.TeamQuitRequest;
import com.yupi.yupao.model.request.TeamUpdateRequest;
import com.yupi.yupao.model.vo.TeamUserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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

    /**
     * 查询队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin,Integer statusRequest);

    /**
     * 查询队伍
     */
    List<TeamUserVO> listMyTeams(TeamQuery teamQuery, boolean isAdmin);


    /**
     * 修改队伍信息
     * @param teamUpdateRequest
     * @param request
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    /**
     * 退出队伍
     */
     boolean quitTeam(TeamQuitRequest teamQuitRequest, User user);

    /**
     * 删除（解散队伍）
     * @param deleteRequest
     * @param request
     * @return
     */
    boolean deleteTeam(Long id, User loginUser);

    /**
     * 通过用户id查队伍
     */
    List<Team> selectByUserId(long userId);
}
