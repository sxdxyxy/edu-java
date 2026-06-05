package com.joyfishs.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ykfnb
 */
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 8330414287694336424L;

    /** 创建者 **/
    @TableField("create_by")
    private Long createBy;

    /** 创建时间 **/
    @JsonFormat(pattern= DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    @TableField("create_time")
    private Date createTime;

    /** 更新者 **/
    @TableField("update_by")
    private Long updateBy;

    /** 更新时间 **/
    @JsonFormat(pattern=DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    @TableField("update_time")
    private Date updateTime;

    /** 是否删除 1-是,0-否 **/
    @ApiModelProperty(hidden = true)
    @TableField("is_delete")
    private Integer isDelete;

    /** 删除者 **/
    @ApiModelProperty(hidden = true)
    @TableField("delete_by")
    private Long deleteBy;

    /** 删除时间 **/
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern=DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    @TableField("delete_time")
    private Date deleteTime;

    /** 删除理由 **/
    @ApiModelProperty(hidden = true)
    @TableField("delete_reason")
    private String deleteReason;

    /** 备注 **/
    @ApiModelProperty(hidden = true)
    @TableField("remark")
    private String remark;

}
