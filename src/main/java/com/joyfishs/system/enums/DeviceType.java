package com.joyfishs.system.enums;

import com.joyfishs.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型
 * 针对一套 用户体系
 *
 */
@Getter
@AllArgsConstructor
public enum DeviceType {

    /**
     * pc端
     */
    WEB("web"),

    /**
     * app端
     */
    APP("app"),

    /**
     * 小程序端
     */
    MINI_PROGRAM("mini_program");

    private final String device;

    public static DeviceType valueOfType(String type) {
        for (DeviceType device : DeviceType.values()) {
            if (StringUtils.equals(device.getDevice(), type)) {
                return device;
            }
        }
        return null;
    }
}
