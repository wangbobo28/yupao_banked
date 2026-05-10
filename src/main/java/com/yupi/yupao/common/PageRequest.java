package com.yupi.yupao.common;

import lombok.Data;

import java.io.Serializable;


@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -5860707094194210842L;

    /**
     * 页面大小
     */
    private int pageSize = 10;
    /**
     * 页码
     */
    private int pageNum = 1;
}
