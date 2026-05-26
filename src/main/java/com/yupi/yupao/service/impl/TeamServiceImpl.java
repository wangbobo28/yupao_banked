package com.yupi.yupao.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupao.common.DeleteRequest;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.mapper.TeamMapper;
import com.yupi.yupao.model.Team;
import com.yupi.yupao.model.User;
import com.yupi.yupao.model.UserTeam;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.model.enums.TeamStatusEnum;
import com.yupi.yupao.model.request.TeamJoinRequest;
import com.yupi.yupao.model.request.TeamQuitRequest;
import com.yupi.yupao.model.request.TeamUpdateRequest;
import com.yupi.yupao.model.vo.TeamUserVO;
import com.yupi.yupao.model.vo.UserVO;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.service.UserService;
import com.yupi.yupao.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
* @author LENOVO
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2026-05-10 11:42:18
*/
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {


    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private TeamMapper teamMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addTeam(Team team, User loginUser) {
        final long userId = loginUser.getId();
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不符合要求");
        }
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不符合要求");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
        if (enumByValue == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(enumByValue) && (StringUtils.isBlank(password) || password.length() > 32)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不符合要求");
        }
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"过期时间不符合要求");
        }
        QueryWrapper<Team> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("userId",userId);
        long count = this.count(objectQueryWrapper);
        if (count >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍数量不能过多");
        }
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setCreateTime(new Date());
        userTeam.setTeamId(team.getId());
        boolean result1 = userTeamService.save(userTeam);
        if (!result1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        return teamId;
    }
    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin,Integer statusRequest) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            // 查询最大人数相等的
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            // 根据创建人来查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            // 根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
//            queryWrapper.eq("status", statusEnum.getValue());
            queryWrapper.eq("status",statusRequest);
        }
        // 不展示已过期的队伍
        // expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public List<TeamUserVO> listMyTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            // 查询最大人数相等的
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            // 根据创建人来查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            // 根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        }
        // 不展示已过期的队伍
        // expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    /**
     * 修改退伍信息
     */
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request){
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(teamUpdateRequest.getId());
        if (ObjectUtils.isEmpty(oldTeam)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean admin = userService.isAdmin(loginUser);
        if (oldTeam.getUserId() != loginUser.getId() || !admin){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (teamUpdateRequest.getStatus() == 2){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须要设置密码");
            }
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,team);
        return this.updateById(team);
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser) {
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = loginUser.getId();


        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (teamStatusEnum.equals(TeamStatusEnum.PRIVATE)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if(TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if (StringUtils.isNotBlank(password) && !team.getPassword().equals(password)){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"密码错误");
            }
        }
        QueryWrapper<UserTeam> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("userId",userId);
        long count = userTeamService.count(objectQueryWrapper);
        if (count > 5){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"最多加入五个队伍");
        }
        objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("userId",userId);
        objectQueryWrapper.eq("teamId",teamId);
        long hasUserJoin = userTeamService.count(objectQueryWrapper);
        if (hasUserJoin > 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户已加入队伍");
        }
        objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("teamId",teamId);
        long count1 = userTeamService.count(objectQueryWrapper);
        if (count1 >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Long userId = loginUser.getId();
        if (teamId < 0 || teamId == null || userId == null || userId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserTeam> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("userId",userId);
        objectQueryWrapper.eq("teamId",teamId);
        long count1 = userTeamService.count(objectQueryWrapper);
        if (count1 <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户未加入队伍");
        }
        long teamMemories = this.countTeamUserByTeamId(teamId);
        if (teamMemories == 1){
            //解散队伍
            boolean b = this.removeById(teamId);
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("teamId",teamId);
            return userTeamService.removeById(userTeamQueryWrapper);
        }else {
            Team team = this.getById(teamQuitRequest.getTeamId());
            if (team.getUserId() == loginUser.getId()){
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamlist = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamlist) || userTeamlist.size() < 2){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam userTeam = userTeamlist.get(1);
                Long nextTeamLeader = userTeam.getUserId();
                //更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeader);
                boolean result = this.updateById(updateTeam);
                if (!result){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                }
            }
            //退出队伍
            return userTeamService.remove(objectQueryWrapper);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Long id, User loginUser) {
        Team team = this.getById(id);
        Long teamId = team.getId();
        if (team.getUserId() != loginUser.getId()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"没有权限");
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        boolean remove = userTeamService.remove(userTeamQueryWrapper);
        if (!remove){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除关联队伍信息失败");
        }
        return this.removeById(teamId);
    }

    @Override
    public List<Team> selectByUserId(long userId) {
        List<Team> teams = teamMapper.selectTeamByUserId(userId);
        return teams;
    }

    /**
     * 查询队伍人数
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId){
        QueryWrapper<UserTeam> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(objectQueryWrapper);
    }
}
