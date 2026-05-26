package com.yupi.yupao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.yupao.model.Team;
import com.yupi.yupao.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
* @author LENOVO
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2026-05-10 11:42:18
* @Entity generator.domain.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

        @Select("SELECT t.* FROM team t  \n" +
                "            INNER JOIN user_team ut ON t.id = ut.teamId\n" +
                "            WHERE ut.userId = #{userId} AND t.isDelete = 0\n" +
                "            ORDER BY t.createTime DESC")
    List<Team> selectTeamByUserId(@Param("userId") Long userId);

}




