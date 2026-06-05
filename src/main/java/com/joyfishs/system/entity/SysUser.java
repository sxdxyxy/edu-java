package com.joyfishs.system.entity;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joyfishs.system.config.validation.Update;
import com.joyfishs.utils.StringUtils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdcardUtil;
import lombok.Data;

@Data
public class SysUser extends BaseEntity {

    public static final String DEFAULT_PASSWORD = "Aqpx123456";

    /** 用户ID **/
    @TableId(type = IdType.AUTO)
    @NotNull(message = "用户ID不能为空", groups = {Update.class})
    private Long id;

    /** 姓名 */
    @NotEmpty(message = "姓名不能为空")
    private String name;

    /** 用户名称 **/
    @NotEmpty(message = "用户名称不能为空")
    private String userName;

    /** 用户昵称 **/
    private String nickName;

    /** 用户邮箱 **/
    private String email;

    /** 手机号码 **/
    private String phone;

    /** 用户性别 0-未知 1-男 2-女 **/
    private int sex;

    /** 用户头像 **/
    private String avatar;

    /** 用户密码 **/
    @JsonIgnore
    private String password;

    /** 用户SALT **/
    @JsonIgnore
    private String salt;

    /** 用户状态 0-停用 1-正常 2-注册 **/
    private Integer status;

    /** 最后登录IP **/
    private String loginIp;

    /** 最后登录时间 **/
    @JsonFormat(pattern= DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime loginDate;

    /** 登录次数 **/
    private int loginCount;

    /** 身份证号码 **/
    private String idCardNo;

    /** 年龄 */
    @TableField(exist = false)
    private Integer age;
    public int getAge() {
        if (StringUtils.isEmpty(this.idCardNo) || !IdcardUtil.isValidCard(this.idCardNo)) {
            return 0;
        }
        // 通过身份证号计算年龄
        return this.age = IdcardUtil.getAgeByIdCard(this.getIdCardNo());
    }

}
