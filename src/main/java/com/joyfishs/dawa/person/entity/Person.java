package com.joyfishs.dawa.person.entity;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyfishs.dawa.person.enums.WorkType;
import com.joyfishs.dawa.utils.Dictionary;
import com.joyfishs.system.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@TableName(value = "xm_person")
@ApiModel(value = "Person", description = "学员基本信息")
public class Person extends BaseEntity {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("姓名")
    @NotEmpty(message = "姓名不能为空")
    private String name;

    @ApiModelProperty("用户名")
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @ApiModelProperty("用户性别 0-未知 1-男 2-女")
    private Integer sex;

    @ApiModelProperty("身份证号码")
    private String idCardNo;

    @ApiModelProperty("出生日期")
    @TableField(exist = false)
    private String birthday;

    @ApiModelProperty("身份证照片正面")
    private String idPhotoFace;

    @ApiModelProperty("身份证照片反面")
    private String idPhotoBack;

    @ApiModelProperty("血型")
    private String bloodType;

    @ApiModelProperty("民族")
    private String nation;

    @ApiModelProperty("手机号码")
    @NotEmpty(message = "手机号码不能为空")
    private String phone;

    @ApiModelProperty("户籍地址")
    private String residenceAddress;

    @ApiModelProperty("家庭住址")
    private String homeAddress;

    @ApiModelProperty("家庭联系人")
    private String emergencyContact;

    @ApiModelProperty("家庭联系人电话")
    private String emergencyContactPhone;

    @ApiModelProperty("婚姻状态, 字典0002")
    private String jobs;

    @ApiModelProperty("婚姻状态名")
    @TableField(exist = false)
    private String jobsName;

    @ApiModelProperty("工种，1=普工，2=电工，3=电焊工，4=钢筋工，5=混凝土，6=架子工")
    private Integer workType;

    @ApiModelProperty("工种名")
    @TableField(exist = false)
    private String workTypeName;

    @ApiModelProperty("学历 字典0004")
    private Integer degreeId;

    @ApiModelProperty("年度计划学时")
    private BigDecimal planClassHour;

    @ApiModelProperty("部门信息")
    @NotNull(message = "部门信息不能为空")
    private Long orgId;

    @ApiModelProperty("是否管理员 2:否 1:是")
    private Integer isAdmin;

    @ApiModelProperty("状态 0:离职 1:在职")
    private Integer state;

    @ApiModelProperty("注册日期")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date registerDate;

    @ApiModelProperty("人脸头像，用于人脸识别")
    private String facePhotoUrl;

    @ApiModelProperty("头像地址，用于社群标识")
    private String avatar;

    @ApiModelProperty("是否人脸认证通过，true=通过，false=未通过")
    private Boolean verified;

    @ApiModelProperty("年龄")
    @TableField(exist = false)
    private Integer age;
    @ApiModelProperty("证书列表")
    @TableField(exist = false)
    private List<PersonCertificate> certificateList = new ArrayList<>();
    @ApiModelProperty("组织名称")
    @TableField(exist = false)
    private String orgName;
    @ApiModelProperty("项目组名称")
    @TableField(exist = false)
    private String projectTeam;
    @ApiModelProperty("组织类型 1:单位部门 2:项目工程")
    @TableField(exist = false)
    private Integer orgType;
    @ApiModelProperty("是否注册审核 1是 0否")
    @TableField(exist = false)
    private Integer isRegisterVerify = 0;

    @ApiModelProperty(value = "小程序二维码Url")
    private String qrCodeUrl;

    @ApiModelProperty(value = "小程序二维码内容")
    private String qrCode;

    public int getAge() {
        if (this.idCardNo == null) {
            return 0;
        }
        // 通过身份证号计算年龄
        return this.age = IdcardUtil.getAgeByIdCard(this.getIdCardNo());
    }

    public String getWorkTypeName() {
        if (ObjectUtil.isNull(this.workType)) {
            return null;
        }
        return WorkType.value(this.workType).getDesc();
    }

    public String getSexStr() {
        if (ObjUtil.isNull(this.getSex())) {
            return "未设置";
        }
        switch (this.getSex()) {
            case 1:
                return "男";
            case 2:
                return "女";
        }
        return "未设置";
    }

    public String getJobsName() {
        return getDictionaryNames("0002", this.jobs);
    }

    public String getBirthday() {
        if (idCardNo == null) {
            return null;
        }
        return DateUtil.formatDate(IdcardUtil.getBirthDate(idCardNo));
    }

    public String getDictionaryNames(String code, String values) {
        if (values == null || "".equals(values)) {
            return values;
        }
        String[] split = values.split(",");
        StringBuffer sb = new StringBuffer();
        for (String s : split) {
            String name = Dictionary.getDictionaryItem(code, s).getName();
            if (name != null) {
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(name);
            }
        }
        return sb.toString();
    }
}
