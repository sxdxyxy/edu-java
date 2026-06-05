package com.joyfishs.dawa.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工程项目实体类
 */
@Data
@TableName("xm_engineering_project")
public class EngineeringProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目简称
     */
    private String shortName;

    /**
     * 项目编码
     */
    private String code;

    /**
     * 建设地点
     */
    private String buildAddress;

    /**
     * 状态：1=筹备中，2=进行中，3=已完成
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除：0=未删除，1=已删除
     */
    private Integer isDelete;

    /**
     * 删除原因
     */
    private String deleteReason;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    private Long deleteBy;
}
