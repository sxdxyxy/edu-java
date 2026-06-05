package com.joyfishs.dawa.notice.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 通知公告人员关联表
 */

@Data
@Accessors(chain = true)
@TableName(value ="xm_notice_person")
public class NoticePerson {
    /**  id */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 公告id */
    private Long noticeId;

    /** 接收人员id */
    private Long personId;

    /** 用户id */
    private Long userId;

    /** 是否已读 1是 0否 **/
    private Integer isRead;

    /** 阅览时间 **/
    @JsonFormat(pattern=DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private Date readTime;

    /* 组织名称 */
    @TableField(exist = false)
    private String orgName;

    /* 人员名称 */
    @TableField(exist = false)
    private String personName;

    /** 用户头像 **/
    @TableField(exist = false)
    private String avatar;
}
