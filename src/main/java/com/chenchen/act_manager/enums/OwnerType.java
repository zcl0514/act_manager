package com.chenchen.act_manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zouchanglin
 * @since 2026/1/19
 */
@Getter
@AllArgsConstructor
public enum OwnerType {

    CHENCHEN(1, "宸宸"),

    DAD_MUM(2, "爸爸妈妈");

    private final int code;

    private final String show;

    public static String getByCode(int code) {
        for (OwnerType type : OwnerType.values()) {
            if (type.getCode() == code) {
                return type.show;
            }
        }
        return null;
    }

}
