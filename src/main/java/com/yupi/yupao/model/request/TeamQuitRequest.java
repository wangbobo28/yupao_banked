package com.yupi.yupao.model.request;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户退出请求体
 */
@Data
public class TeamQuitRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -5277503316967813836L;
    private Long teamId;


}
