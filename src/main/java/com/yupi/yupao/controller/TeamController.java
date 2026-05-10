package com.yupi.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.ResultUtils;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.model.Team;
import com.yupi.yupao.model.User;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.model.request.TeamAddRequest;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("team")
@Slf4j
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    /**
     * 新增队伍
     *
     */
    @PostMapping("/addTeam")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);
    }
    /**
     * 删除队伍
     *
     */
    @PostMapping("/deleteTeam")
    public BaseResponse<Boolean> deleteTeam(@RequestBody Team team){
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        System.out.println(team.getId());
        System.out.println(team);
        boolean result = teamService.removeById(team);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统内部异常");
        }
        return ResultUtils.success(true);
    }

    /**
     * 修改队伍
     *
     */
    @PostMapping("/updateTeam")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team){
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean save = teamService.updateById(team);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统内部异常");
        }
        return ResultUtils.success(true);
    }
    /**
     * 根据id查询队伍
     *
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeam(long id){
        if (id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统内部异常");
        }
        return ResultUtils.success(team);
    }
    /**
     * 查询所有队伍
     *
     */
    @GetMapping("/list")
    public BaseResponse<List<Team>> getTeams(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team,teamQuery);
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>(team);
        List<Team> teamList = teamService.list(teamQueryWrapper);
        return ResultUtils.success(teamList);
    }
    /**
     * 分页查询所有队伍
     *
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamByPage(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page,teamQueryWrapper);
        return ResultUtils.success(resultPage);
    }
}
