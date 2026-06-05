package com.joyfishs.system.entity.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PersonVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  学员id */
    private Long id;

    /** 部门信息 */
    private Long orgId;

    private Long userId;

    private Long personId;

    /** 姓名 */
    private String name;

    /* 用户名 */
    private String userName;

    /** 用户性别 0-未知 1-男 2-女 **/
    private Integer sex;

    /** 是否管理员 2:否 1:是 */
    private Integer isAdmin;

    /* 单位信息 */
    private String unit;

    /* 部门信息 */
    private String dept;

    @ApiModelProperty("社区头像")
    private String avatar;

    @ApiModelProperty("人脸头像，用于人脸识别")
    private String facePhotoUrl;

    /** 年度学时 */
    private BigDecimal yearClassHour;

    /** 累计学时 */
    private BigDecimal sumClassHour;
}
