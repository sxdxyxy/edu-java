package com.joyfishs.dawa.project.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="xm_project_relate")
@ApiModel(value = "ProjectRelate", description = "项目和受训单位,课程列表,人员三者之间的关系")
public class ProjectRelate extends BaseEntity {
    /** id */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型 1-受训单位 2-课程列表  3-人员 */
    private Integer type;

    /** 项目id */
    private Long projectId;

    /** 受训单位ID */
    /** 课程id */
    /** 人员id */
    private String relateIds;

    @TableField(exist = false)
    private String ids;

    /** 排序字段 */
    private Integer sort;

    /** 用于接收前端数据 */
    @TableField(exist = false)
    private List<ProjectRelate> list;

    public ProjectRelate(Long id, Integer type, Long projectId, String ids, Integer sort) {
        this.id = id;
        this.type = type;
        this.projectId = projectId;
        this.relateIds = ids;
        this.sort = sort;
    }
    public ProjectRelate(Long projectId, String ids) {
        this.type = 3;
        this.projectId = projectId;
        this.relateIds = ids;
    }
}
