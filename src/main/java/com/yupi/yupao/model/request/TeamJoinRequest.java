package com.yupi.yupao.model.request;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍
 * @TableName team
 */
@TableName(value ="team")
@Data
public class TeamJoinRequest implements Serializable {
    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}