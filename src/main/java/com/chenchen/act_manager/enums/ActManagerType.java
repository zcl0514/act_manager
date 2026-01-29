package com.chenchen.act_manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zouchanglin
 * @since 2026/1/19
 */
@Getter
@AllArgsConstructor
public enum ActManagerType {

    AWARD(1, "奖励"),

    DEDUCT(2, "扣除"),

    GREAT_AWARD(3, "重大奖励"),

    GREAT_DEDUCT(4, "重大扣除");

    private final int code;

    private final String show;

    public static String getByCode(int code) {
        for (ActManagerType type : ActManagerType.values()) {
            if (type.getCode() == code) {
                return type.show;
            }
        }
        return null;
    }

}
