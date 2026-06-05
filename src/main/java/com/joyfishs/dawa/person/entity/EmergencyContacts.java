package com.joyfishs.dawa.person.entity;

import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 紧急联系人
 *
 * @author ykfnb
 */

@Data
@Accessors(chain = true)
@TableName(value = "xm_person_emergency_contacts")
public class EmergencyContacts {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属人员id
     */
    private Long personId;

    @ApiModelProperty("姓名")
    @NotEmpty(message = "姓名不能为空")
    private String name;

    @ApiModelProperty("关系")
    @NotEmpty(message = "关系不能为空")
    private String relation;

    @ApiModelProperty("职业")
    private String job;

    @ApiModelProperty("电话")
    @NotEmpty(message = "电话不能为空")
    private String phone;

    @ApiModelProperty("血型")
    private String bloodType;

    @ApiModelProperty("地址")
    private String address;
}
