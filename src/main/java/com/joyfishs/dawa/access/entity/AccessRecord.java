package com.joyfishs.dawa.access.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 准入记录实体类
 *
 * @author safe-edu
 * @since 2026-03-29
 */
@Data
@TableName(value = "access_records", autoResultMap = true)
public class AccessRecord {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 关联项目 ID
     */
    private Long projectId;

    /**
     * 关联项目部 ID（组织机构）
     */
    private Long orgId;

    /**
     * 进场时间
     */
    private Date accessTime;

    /**
     * 出场时间
     */
    private Date exitTime;

    /**
     * 准入类型：normal/temporary/denied
     */
    private String accessType;

    /**
     * 进场时安全码颜色
     */
    private String safetyCodeColor;

    /**
     * 闸机编号
     */
    private String gateId;

    /**
     * 抓拍照片 URL
     */
    private String cameraSnapshot;

    /**
     * 位置信息
     */
    private String location;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    private Date createdAt;

    // ===== 扩展字段（从 sys_user 关联查询）=====
    /**
     * 用户姓名
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户手机号
     */
    @TableField(exist = false)
    private String phone;

    /**
     * 身份证号
     */
    @TableField(exist = false)
    private String idCardNo;

    /**
     * 项目名称
     */
    @TableField(exist = false)
    private String projectName;
}
