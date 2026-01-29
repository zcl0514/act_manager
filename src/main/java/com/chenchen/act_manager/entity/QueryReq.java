package com.chenchen.act_manager.entity;

import lombok.Data;

/**
 * @author zouchanglin
 * @since 2026/1/28
 */
@Data
public class QueryReq {

    private Integer type;

    private Integer page = 0;

    private Integer size = 10;

    private String sortBy = "id";

    private String direction = "desc";
}
