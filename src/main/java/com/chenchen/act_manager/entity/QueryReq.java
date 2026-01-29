package com.chenchen.act_manager.entity;

import lombok.Data;

/**
 * @author zouchanglin
 * @since 2026/1/28
 */
@Data
public class QueryReq {

    private Integer type;

    private Integer owner;

    private String keyword;

}
