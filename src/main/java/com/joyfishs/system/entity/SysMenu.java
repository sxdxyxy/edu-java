package com.joyfishs.system.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.joyfishs.system.config.validation.Update;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 菜单表
 **/
@ApiModel(value = "系统菜单")
@Data
public class SysMenu extends BaseEntity {

    /**
     * 菜单ID
     **/
    @TableId(type = IdType.AUTO)
    @NotNull(message = "菜单ID不能为空", groups = Update.class)
    private Long id;

    @ApiModelProperty("菜单名称")
    @NotEmpty(message = "菜单名称不能为空")
    private String name;

    @ApiModelProperty("菜单编码")
    @NotEmpty(message = "菜单编码不能为空")
    private String code;

    @ApiModelProperty("菜单图标")
    public String icon;

    @ApiModelProperty("上级菜单ID")
    @NotNull(message = "上级菜单ID不能为空")
    private Long pid;

    /**
     * 父ids
     */
    private String pids;

    @NotNull(message = "不能为空")
    @ApiModelProperty(value = "菜单类型,0目录 1菜单 2按钮 3-服务级(待定)")
    private Integer type;

    @ApiModelProperty("路由地址")
    private String router;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 权限标识
     **/
    private String permission;

    @ApiModelProperty("是否显示,1-是 0-否 ")
    @NotNull(message = "是否显示不能为空")
    private Integer visible;

    @ApiModelProperty("顺序号")
    private Integer intCode;

    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();

}
