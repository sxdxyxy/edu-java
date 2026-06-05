package com.joyfishs.dawa.archives.config.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 人员档案配置 (Person Archives Config) Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("person_archives_config")
@ApiModel(value = "PersonArchivesConfig", description = "人员档案配置")
public class PersonArchivesConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键 ID")
    private Long id;

    /**
     * 工种类型 (1=普工，2=电工等)
     */
    @ApiModelProperty("工种类型")
    private Integer workType;

    /**
     * 档案配置名称
     */
    @ApiModelProperty("档案配置名称")
    private String configName;

    /**
     * 配置编码
     */
    @ApiModelProperty("配置编码")
    private String configCode;

    /**
     * 所属单位/机构 ID
     */
    @ApiModelProperty("机构 ID")
    private Long orgId;

    /**
     * 所属项目 ID
     */
    @ApiModelProperty("关联项目 ID")
    private Long projectId;

    /**
     * 所属工程项目 ID（统一管理下属项目部的一人一档配置）
     */
    @ApiModelProperty("关联工程项目 ID")
    private Long engineeringId;

    /**
     * 模板字段配置 (JSON)
     */
    @ApiModelProperty("模板字段配置 (JSON)")
    private String templateFields;

    /**
     * 文档模板 URL
     */
    @ApiModelProperty("文档模板 URL")
    private String documentTemplateUrl;

    /**
     * 文档模板名称
     */
    @ApiModelProperty("文档模板名称")
    private String documentTemplateName;

    /**
     * 排序顺序
     */
    @ApiModelProperty("排序顺序")
    private Integer sortOrder;

    /**
     * 是否默认方案
     */
    @ApiModelProperty("是否默认方案")
    private Boolean isDefault;

    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用")
    private Boolean isActive;

    /**
     * 档案项目配置 (JSON) - 包含该方案下所有档案项及其排序
     */
    @ApiModelProperty("档案项目配置 (JSON)")
    private String archiveItems;

    // === 以下是前端显示用扩展字段，不存入数据库 ===
    /**
     * 该配置下档案项数量
     */
    private transient Integer itemCount;

    /**
     * 更新人员用户名
     */
    private transient String updateUserName;
}
