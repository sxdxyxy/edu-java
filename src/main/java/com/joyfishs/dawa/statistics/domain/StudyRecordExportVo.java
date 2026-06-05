package com.joyfishs.dawa.statistics.domain;

import java.util.ArrayList;
import java.util.List;

import com.deepoove.poi.data.NumberingRenderData;
import com.joyfishs.dawa.project.domain.vo.ProjectCourseList;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "StudyRecordVo", description = "学习记录")
public class StudyRecordExportVo {
    @ApiModelProperty("证书编号")
    private String number;

    @ApiModelProperty("文档标题")
    private String title;

    @ApiModelProperty("人员姓名")
    private String personName;

    @ApiModelProperty("用户性别")
    private String sex;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("身份证号码")
    private String idCardNo;

    @ApiModelProperty("出生日期")
    private String birthday;

    @ApiModelProperty("施工单位")
    private String orgName;

    @ApiModelProperty("工程名称")
    private String projectName;

    @ApiModelProperty("课程开始时间")
    private String startDate;

    @ApiModelProperty("课程结束时间")
    private String endDate;

    @ApiModelProperty("岗位")
    private String jobsName;

    @ApiModelProperty("头像")
    private String facePhotoUrl;

    @ApiModelProperty("工种")
    private String workTypeName;

    @ApiModelProperty("总学时要求")
    private Integer totalLearnHours;

    @ApiModelProperty("项目所有课程")
    private List<ProjectCourseList> courseList = new ArrayList<>();

    @ApiModelProperty("项目所有课程名称")
    private NumberingRenderData courseNameList;
}
