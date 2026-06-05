package com.joyfishs.dawa.course.domain.bo;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.joyfishs.dawa.course.entity.Courseware;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CoursewareBo", description = "课件")
public class CoursewareBo {

    private Long id;

    @ApiModelProperty("课程id,不是资源库类必须要设置")
    private Long courseId;

    @ApiModelProperty(value = "名称", required = true)
    private String name;

    @ApiModelProperty("学时（学习之后获得的）")
    private BigDecimal learnHours;

    @ApiModelProperty("学分（学习之后获得的）")
    private BigDecimal learnScore;

    @ApiModelProperty("文件Url,如果是文档类型课件，必须设置")
    private String fileUrl;

    @ApiModelProperty(value = "课件类型  1 = 视频  2=文档", required = true)
    @NotNull(message = "课件类型不能为空")
    private Integer type;

    @ApiModelProperty(value = "是否资源库", required = true)
    @NotNull(message = "是否资源库设置不能为空")
    private boolean resources;

    public boolean isVideo(){
        return this.type.equals(Courseware.VIDEO);
    }

    public boolean isDoc(){
        return this.type.equals(Courseware.DOCUMENT);
    }

    @ApiModelProperty("文件大小")
    private String sizeStr;
}
