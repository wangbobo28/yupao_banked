package com.yupi.yupao.mapper;

import com.yupi.yupao.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author LENOVO
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2025-09-07 21:22:33
* @Entity com.yupi.usercenter.model.User
*/
@Mapper
public interface TagMapper extends BaseMapper<User> {
    // 根据用户名查询一条数据
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByName(String username);
}
