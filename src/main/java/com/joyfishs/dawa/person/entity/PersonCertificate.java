package com.joyfishs.dawa.person.entity;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyfishs.system.annotation.Excel;
import com.joyfishs.system.entity.BaseEntity;
import com.joyfishs.utils.StringUtils;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author ykfnb
 */

@Data
@Accessors(chain = true)
@TableName(value ="xm_person_certificate")
@ApiModel(value = "PersonCertificate", description = "学员证书")
public class PersonCertificate extends BaseEntity {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    /* 人员ID */
    @NotNull(message = "人员ID不能为空")
    private Long personId;

    @Excel(name = "人员名称")
    @TableField(exist = false)
    private String personName;

    /** 名称 */
    @NotNull(message = "证书名称不能为空")
    @Excel(name = "证书名称")
    private String name;

    /** 注册日期 */
    @NotNull(message = "注册日期不能为空")
    @Excel(name = "注册日期", dateFormat = DatePattern.NORM_DATE_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date registerDate;

    /** 有效日期始 */
    @NotNull(message = "有效日期始不能为空")
    @Excel(name = "有效日期始", dateFormat = DatePattern.NORM_DATE_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date validityStartDate;

    /** 有效日期止 */
    @NotNull(message = "有效日期止不能为空")
    @Excel(name = "有效日期止", dateFormat = DatePattern.NORM_DATE_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date validityEndDate;

    /** 证书分类 字典0001 */
    @NotNull(message = "证书分类不能为空")
    @Excel(name = "证书分类", dictType = "0001")
    private Integer type;

    /* 照片 */
    @NotEmpty(message = "证书图片不能为空")
    private String file;

    /** 证书照片 */
    @TableField(exist = false)
    @Excel(name = "证书图片")
    private String fileUrl;
    public void setFileUrl(String fileUrl){
        this.fileUrl = "";
        if(StringUtils.isEmpty(this.file)) {
            return;
        }

        JSONArray fileJsonArray = JSONArray.parseArray(this.file);
        for (int i = 0; i < fileJsonArray.size(); i++) {
            this.fileUrl = this.fileUrl + fileJsonArray.getJSONObject(i).getString("url") + ",";
        }
        if(this.fileUrl.length() > 0) {
            this.fileUrl = this.fileUrl.substring(0, this.fileUrl.length()-1);
        }
    }

    /** 证书状态 1:正常 2:过期 字典0010 */
    @Excel(name = "证书状态", dictType = "0010")
    private Integer state;
}
