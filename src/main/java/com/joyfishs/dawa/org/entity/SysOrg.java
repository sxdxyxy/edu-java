package com.joyfishs.dawa.org.entity;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.config.validation.Update;
import com.joyfishs.system.entity.BaseEntity;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统组织机构表
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "组织机构")
@Data
@TableName("sys_org")
public class SysOrg extends BaseEntity {

    @ApiModelProperty(value = "组织ID")
    @TableId(type = IdType.AUTO)
    @NotNull(message = "组织ID不能为空", groups = Update.class)
    private Long id;

    @ApiModelProperty(value = "组织名称")
    @NotEmpty(message = "组织名称不能为空")
    private String name;

    @ApiModelProperty(value = "组织简称")
    private String shortName;

    @ApiModelProperty(value = "组织编码")
    @NotEmpty(message = "组织编码不能为空")
    private String code;

    @ApiModelProperty(value = "组织类型 1:单位部门 2:项目工程 ")
    @NotNull(message = "组织类型不能为空")
    private Integer orgType = 1;

    @ApiModelProperty(value = "类型 orgType=1时 1:单位 2:部门； orgType=2时 1:项目 2:部门 ")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @ApiModelProperty(value = "上级组织ID ")
    @NotNull(message = "上级组织ID不能为空")
    private Long pid;
    /**
     * 父ids
     */
    private String pids;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "企业信息")
    private String companyInfo;

    @ApiModelProperty(value = "行政区划")
    private Integer division;

    @ApiModelProperty(value = "社会信用代码")
    private String socialCreditCode;

    @ApiModelProperty(value = "邀请码")
    private String invitationCode;

    @ApiModelProperty(value = "机构小程序码Url，scene包含机构邀请码")
    private String wxaCodeUrl;

    @ApiModelProperty(value = "所属工程项目ID（项目部专用）")
    private Long engineeringId;

    public List<Long> getPidList() {
        return CollStreamUtil.toList(StrSplitter.split(StrUtil.removeAll(this.getPids(), '[', ']'), ',', 0, true, true), item -> Long.valueOf(item));
    }
}
