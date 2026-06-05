package com.joyfishs.dawa.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 课程课件
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "课件")
@TableName("xm_courseware")
public class Courseware extends BaseEntity {
    //视频
    public static final Integer VIDEO = 1;
    //文档
    public static final Integer DOCUMENT = 3;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 课件名称
     */
    private String name;

    /**
     * 课件大小
     */
    private String sizeStr;
    /**
     * 课件时长
     */
    private Long duration;

    /**
     * 学时（学习之后获得的）
     */
    private BigDecimal learnHours;

    /**
     * 学分（学习之后获得的）
     */
    private BigDecimal learnScore;

    /**
     * 文件路径
     */
    private String videoPath;

    /**
     * 腾讯云vod文件id
     */
    private String fileId;

    /**
     * 自适应码流文件清单
     */
    private String m3u8;
    /**
     * 封面图路径
     */
    private String coverPath;

    /**
     * 三方课件ID
     */
    private Long thirdPartyId;

    /**
     * 课件类别  1 = 视频  2- 文档
     */
    private Integer type;

    /**
     * 是否资源库  1-是  0-否
     */
    private Integer ifResources;

    /**
     * 是否转码完成 true=完成  false=在转码中
     */
    private Boolean transcode;

    /* 浏览量 */
    private Integer viewCount;

    /* 下载次数 */
    private Integer downCount;

    /* 文件名后缀 */
    private String fileExtension;

    public boolean isVideo() {
        return this.type.equals(VIDEO);
    }

    public boolean isDoc() {
        return this.type.equals(DOCUMENT);
    }
}
