package com.chenchen.act_manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zouchanglin
 * @since 2026/1/19
 */
@Getter
@AllArgsConstructor
public enum TitleEnum {
    owner(""),role("规则"),redFlowerCount("小红花"),extra("额外");

    private final String title;

}
