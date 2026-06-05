package com.joyfishs.dawa.notice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyfishs.system.entity.BaseEntity;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 通知公告
 */

@Data
@Accessors(chain = true)
@TableName(value ="xm_notice")
public class Notice extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 公告标题 */
    private String title;

    /** 发布范围 */
    private Long rangeId;

    /** 是否置顶 1是 0否 **/
    private Integer isTop;

    /** 是否计算学时 1是 0否 **/
    private Integer isCalculate;

    /** 学时 */
    private BigDecimal classHour;

    /** 是否即时发布 1是 0否 **/
    private Integer isNowPublish;

    /** 发布时间 **/
    @JsonFormat(pattern=DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime publishTime;

    /** 发布类型 1:新闻资讯 2:通知公告 3:必读文件 **/
    private Integer type;

    /** 内容 富文本编辑 */
    private String content;

    /** 阅读量 */
    private Integer viewCount;

    /** 是否必读  1-是  0-否*/
    private Integer isMustRead;

    /** 封面图地址 */
    private String cover;

    /** 附件地址 */
    private String file;

    /** 发布状态 0:未发布 1:已发布 2:撤销 */
    private Integer status = 0;

    /** 发布人所在单位 */
    private Long createOrgId;
    }
