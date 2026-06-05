package com.joyfishs.dawa.person.domain.result;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author ykfnb
 */

@Data
@Accessors(chain = true)
@ApiModel(value = "PersonChangeLogResult", description = "学员信息变动结果集")
public class PersonChangeLogResult implements Serializable {

    /** 部门信息 */
    private Long orgId;

    /* 变动类型*/
    private String changeType;

    /* 部门名称 */
    @TableField(exist = false)
    private String orgName;

    /* 创建日期 */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    Date createTime;

}
