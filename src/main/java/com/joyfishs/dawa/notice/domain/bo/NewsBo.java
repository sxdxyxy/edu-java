package com.joyfishs.dawa.notice.domain.bo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "新闻资讯")
public class NewsBo {
    @ApiModelProperty("id")
    private Long id;

    @NotEmpty(message = "标题不能为空")
    @ApiModelProperty(value = "标题",required = true)
    private String title;

    @ApiModelProperty("封面图Url")
    private String cover;

    @NotNull(message = "发布范围不能为空")
    @ApiModelProperty(value = "发布范围id",required = true)
    private Long rangeId;

    @NotNull(message = "是否置顶必须设置")
    @ApiModelProperty(value = "是否置顶 1是 0否",required = true)
    private Integer isTop;

    @NotNull(message = "是否即时发布必须设置")
    @ApiModelProperty(value = "是否即时发布 1是 0否",required = true)
    private Integer isNowPublish;

    @ApiModelProperty("发布时间，如果是即时发布就不需要设置")
    private LocalDateTime publishTime;

    @NotEmpty(message = "内容不能为空")
    @ApiModelProperty(value = "内容，富文本编辑",required = true)
    private String content;

    @ApiModelProperty("附件url")
    private String file;

}
