package com.joyfishs.dawa.mobile.domain;

import lombok.Data;

/**
 * 首页封装返回数据
 * @Author xiaodai
 * @create 2022-02-15 10:51
 */

@Data
public class MobileHome {

    /**
     *
     *  需要带个类型
     *  (项目）课程进度 - 正在学习的课程
     *  -名称 、ID 、学习进度百分比
     *
     *  首页包含的内容 系统 - 推荐的选修课
     *  -课程名称、ID
     *
     *  学习提醒
     *  -项目名称、项目ID 要求：学员添加的 需要学习的课程（关联过的）
     *
     *  新闻公告
     *  -新闻标题、是否必读、阅读量、发布人、新闻存储的图片
     *
     */


    /* 返回数据类型 1-课程进度、学习提醒（项目ID） 2-新闻资讯ID */
//    private String type;
//
//    /* 课程进度 */
//    private CourseSchedule courseSchedule;
//
//    /* 学习提醒 */
//    private List<LearningRemind> remindList;
//
//    /* 新闻资讯 */
//    private NewsBulletin newsBulletin;


}
