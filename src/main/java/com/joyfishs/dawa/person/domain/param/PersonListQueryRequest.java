package com.joyfishs.dawa.person.domain.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author ykfnb
 */
@Data
@ApiModel(value = "PersonListQueryRequest", description = "人员查询请求")
public class PersonListQueryRequest {
    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号码")
    @NotEmpty(message = "请输入手机号码")
    private String phone;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("类型 多个类型用,分隔 字典0005")
    private String type;

    @ApiModelProperty("是否管理员 2:否 1:是")
    private Integer isAdmin;

    @ApiModelProperty("组织类型 1:单位部门 2:项目工程")
    private Integer orgType;

    @ApiModelProperty("注册机构")
    private Long orgId;

    @ApiModelProperty("状态 0:离职 1:在职")
    private Integer state;
}
