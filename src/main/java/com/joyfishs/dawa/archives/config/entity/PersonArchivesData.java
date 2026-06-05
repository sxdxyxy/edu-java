package com.joyfishs.dawa.archives.config.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.joyfishs.system.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 人员档案数据 (Person Archives Data) Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("person_archives_data")
@ApiModel(value = "PersonArchivesData", description = "人员档案数据")
public class PersonArchivesData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键 ID")
    private Long id;

    /**
     * 关联配置 ID
     */
    @ApiModelProperty("关联配置 ID")
    private Long configId;

    /**
     * 人员 ID
     */
    @ApiModelProperty("人员 ID")
    private Long personId;

    /**
     * 用户 ID
     */
    @ApiModelProperty("用户 ID")
    private Long userId;

    /**
     * 工种类型
     */
    @ApiModelProperty("工种类型")
    private Integer workType;

    /**
     * 机构 ID
     */
    @ApiModelProperty("机构 ID")
    private Long orgId;

    /**
     * 项目 ID
     */
    @ApiModelProperty("项目 ID")
    private Long projectId;

    /**
     * 班组名称
     */
    @ApiModelProperty("班组名称")
    private String teamName;

    /**
     * 档案数据内容 (JSON)
     */
    @ApiModelProperty("档案数据内容 (JSON)")
    private String archiveData;

    /**
     * 生成的文档 URL
     */
    @ApiModelProperty("生成的文档 URL")
    private String generatedDocUrl;

    /**
     * 档案编号
     */
    @ApiModelProperty("档案编号")
    private String archiveNumber;

    /**
     * 状态 (draft/pending/approved/archived)
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 创建时间（兼容原字段）
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间（兼容原字段）
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updatedAt;

    // === 扩展字段 (transient) ===
    @TableField(exist = false)
    @JsonProperty("personName")
    private String personName;

    @TableField(exist = false)
    @JsonProperty("safetyColor")
    private String safetyColor;

    @TableField(exist = false)
    @JsonProperty("safetyStatus")
    private String safetyStatus;

    @TableField(exist = false)
    @JsonProperty("diag")
    private String diag;

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getDiag() {
        return diag;
    }

    public void setDiag(String diag) {
        this.diag = diag;
    }
}