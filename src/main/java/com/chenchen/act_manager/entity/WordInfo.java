package com.chenchen.act_manager.entity;

import lombok.Data;

/**
 * @author zouchanglin
 * @since 2026/1/19
 */
@Data
public class WordInfo {

    private Integer id;

    private String role;

    private Integer redFlowerCount;

    /**
     * @see com.chenchen.act_manager.enums.ActManagerType
     */
    private Integer type;

    private String extra;

    private Integer owner;
}
