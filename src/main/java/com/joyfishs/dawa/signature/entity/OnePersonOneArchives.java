package com.joyfishs.dawa.signature.entity;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangkaifeng
 */
@Data
@TableName(value = "xm_one_person_one_archives")
@ApiModel(value = "OnePersonOneArchives", description = "一人一档")
public class OnePersonOneArchives {
    /**
     * 目录模板
     */
    public static final String CATALOGUE_TEMPLATE = "/signature/catalogue_template.docx";
    /**
     * 学习记录模板
     */
    public static final String LEARNING_RECORD = "/signature/learningRecord.docx";
    public final static String DOCX = "docx";
    public final static String EXCEL ="xlsx";
    public final static String ZIP ="zip";

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("档案编号")
    private String number;

    @ApiModelProperty("组织机构id")
    @NotNull
    private Long orgId;

    @ApiModelProperty("人员id")
    private Long personId;

    @ApiModelProperty("工种，1=普工，2=电工，3=电焊工，4=钢筋工，5=混凝土，6=架子工")
    @NotNull
    private Integer workType;

    @ApiModelProperty("人员姓名")
    private String personName;

    @ApiModelProperty("进场时间")
    private LocalDateTime entryTime;

    @ApiModelProperty("工程名称")
    private String projectName;

    @ApiModelProperty("班组名称")
    private String teamName;

    @ApiModelProperty("一人一档信息目录文件url")
    private String catalogueUrl;

    @ApiModelProperty("一人一档信息目录文件key")
    private String catalogueName;

    @ApiModelProperty("生成的压缩文件url")
    private String fileUrl;

    @ApiModelProperty("生成的压缩文件key")
    private String fileName;

    @ApiModelProperty("生成压缩文件任务id")
    private String jobId;

    @ApiModelProperty("生成压缩文件是否完成")
    private Boolean jobComplete;

    @ApiModelProperty("业务发生日期")
    private java.util.Date businessTransactionDate;

    private LocalDateTime createTime;
}
