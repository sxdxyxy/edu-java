package com.joyfishs.dawa.violation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 违章即扣分 - JSON Body 请求 DTO
 * <p>
 * 与 violationAndDeduct (query 参数) 配套,支持前端 axios 直接传 JSON。
 * </p>
 *
 * @author safe-edu
 * @since 2026-06-06
 */
@Data
@ApiModel(value = "ViolationAndDeductRequest", description = "违章即扣分请求体")
public class ViolationAndDeductRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "人员ID（xm_person.id）", required = true)
    private Long personId;

    @ApiModelProperty(value = "项目ID", required = true)
    private Long projectId;

    @ApiModelProperty(value = "违章类型代码（如 NO_HELMET）", required = true)
    private String violationCode;

    @ApiModelProperty("违章描述（可选）")
    private String description;

    @ApiModelProperty("证据照片URL JSON数组字符串（可选,生产建议1-3张必填）")
    private String evidencePhotos;

    @ApiModelProperty("违章地点（可选）")
    private String location;
}
