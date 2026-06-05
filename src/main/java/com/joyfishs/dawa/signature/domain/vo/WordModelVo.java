package com.joyfishs.dawa.signature.domain.vo;

import com.deepoove.poi.data.PictureRenderData;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 轮播图
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "WordModelVo", description = "Word模板模型数据")
public class WordModelVo {

    @ApiModelProperty("档案编号")
    private String number;

    @ApiModelProperty("组织机构")
    private String orgName;

    @ApiModelProperty("工程名称")
    private String projectName;

    @ApiModelProperty("工种")
    private String workTypeName;

    @ApiModelProperty("人员姓名")
    private String personName;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("民族")
    private String nation;

    @ApiModelProperty("学历-文化程度")
    private String degree;

    @ApiModelProperty("用户性别")
    private String sex;

    @ApiModelProperty("身份证号码")
    private String idCardNo;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("家庭住址")
    private String homeAddress;

    @ApiModelProperty("家庭联系人")
    private String emergencyContact;

    @ApiModelProperty("家庭联系人电话")
    private String emergencyContactPhone;

    @ApiModelProperty("进场时间")
    private String entryTime;

    @ApiModelProperty("班组名称")
    private String teamName;

    @ApiModelProperty("签名时间")
    private String signDate;

    @ApiModelProperty("签名图片")
    private PictureRenderData signImage;
}
