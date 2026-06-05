package com.joyfishs.dawa.course.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author xiaodai
 * @create 2021/12/21 20:44
 */

@Data
@Accessors(chain = true)
public class CoursewareVideo {

    /*附件ID*/
    private String id;

    /*课件名称*/
    private String name;

    /*课件路径*/
    private String path;

    /*封面图片*/
    private String coverImage;

    /*附件大小*/
    private Long size;
}
