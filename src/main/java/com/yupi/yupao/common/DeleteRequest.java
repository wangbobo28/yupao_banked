package com.yupi.yupao.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 9041735390661863957L;

    private Long id;
}
