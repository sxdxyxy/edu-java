package com.joyfishs.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: APP版本
 * @date: 2021-08-27 10:32
 */
@Data
@Accessors(chain = true)
@TableName("sys_app_version")
public class SysAppVersion extends BaseEntity {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 版本名称
     */
    private String title;

    /**
     * 版本描述
     */
    private String description;

    /**
     * 版本号
     */
    private String versionNum;

    /**
     * app类型 1:安卓 2:ios
     */
    private Integer appType;

    /**
     * app地址
     */
    private String url;
}
