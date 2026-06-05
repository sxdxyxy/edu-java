package com.joyfishs.dawa.course.service;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.course.entity.CourseQuestion;
import com.joyfishs.dawa.course.entity.Courseware;
import com.joyfishs.dawa.course.entity.CoursewareRequiredQuestion;
import com.joyfishs.dawa.course.mapper.CoursewareRequiredQuestionMapper;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author xiaodai
 * @create 2021/12/17 17:26
 */

@Slf4j
@Service
public class RequiredQuestionService extends ServiceImpl<CoursewareRequiredQuestionMapper, CoursewareRequiredQuestion> {

    @Autowired
    private CourseCoursewareService courseCoursewareService;
    @Autowired
    private CourseQuestionService courseQuestionService;

    /**
     *课件已选中的题目ID
     * @return
     */
    public List<Long> findRequiredQuestion(Long coursewareId){
       //通过课件获取已选中的题目
        List<Long> questionIdList = baseMapper.findQuestionIdAllByCoursewareId(coursewareId);

        return questionIdList;
    }

    /**
     * 必选题-新增或编辑
     * @param ids 传入的题目多个Id
     * @param coursewareId  课件ID
     * 每次进来都新增一次，时长后数据量会变得非常庞大，每次编辑必选题的时候，不再进行新增
     * 把这个课件所关联的所有题目ID查出来，然后与传进来的题目ID进行对比，筛出和新增时不一样的，然后删除不一样的数据
     */
    public void saveOrUpdateRequired(String ids, Long coursewareId) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        if (StringUtils.isNull(coursewareId)) {
            throw new CustomException("课件ID不能为空！");
        }

        Courseware courseware = courseCoursewareService.getById(coursewareId);
        if (courseware == null || StringUtils.isNull(courseware.getId())) {
            throw new CustomException("未找到课件");
        }

        //传入进来的题目ID   ----  字符串拆分后转Long
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(toList());

        //通过课件ID获取未删除的所有必选题 的 题目ID
        List<Long> questionIdList = baseMapper.findQuestionIdAllByCoursewareId(coursewareId);

        // 得到 已存在的必选题，和 传入的必选题进行对比,找出用户取消的题目ID然后删除
        if (questionIdList.size() != 0) {

            // 用户传入的ID和库里面比差集
            List<Long> userDifferent = idList.stream().filter(item -> !questionIdList.contains(item)).collect(toList());

            //找出已存的必选题 ，和新传入的必选题做比较，取出差集
            List<Long> different = questionIdList.stream().filter(item -> !idList.contains(item)).collect(toList());

            //用户比出来的差集是要新增的，暂不管  但库里和用户比出来的必定要删除的，如果两个差集均为 空  说明用户和库里选中的是一样的不必处理
            if (userDifferent.size() == 0 && different.size() == 0) {
                return;
            }

            //如果新传入的和原有的必选题有不一样的，删除用户已取消的必选题
            if (different.size() != 0) {
                baseMapper.delCancelRequiredQuestion(coursewareId, StringUtils.join(different, ","));
            }
        }

        //新增或修改必选题
        for (Long questionId : idList){
            //去关联表中通过题目ID和课件ID 去查询，是否已经存在，存在编辑，不存在就新增
            CoursewareRequiredQuestion coursewareRequiredQuestion = baseMapper.findRequiredQuestion(questionId,coursewareId);

            boolean bool = true;
            CoursewareRequiredQuestion coursewareRequiredQuestion1 = new CoursewareRequiredQuestion();
            if (coursewareRequiredQuestion == null || StringUtils.isNull(coursewareRequiredQuestion.getId())) { //新增
                //往关联表中添加关联题目和课件的关联关系
                coursewareRequiredQuestion1.setQuestionId(questionId);
                coursewareRequiredQuestion1.setCoursewareId(coursewareId);
                coursewareRequiredQuestion1.setCreateTime(new Date());
                coursewareRequiredQuestion1.setCreateBy(SecurityUtil.getUserId());
                coursewareRequiredQuestion1.setIsDelete(YesOrNoState.NO.getState());
            } else {//编辑
                bool = false;
                coursewareRequiredQuestion.setUpdateTime(new Date());
                coursewareRequiredQuestion.setUpdateBy(SecurityUtil.getUserId());
            }

            if (bool) save(coursewareRequiredQuestion1);
            else updateById(coursewareRequiredQuestion);
        }
    }


    /**
     * 课件下的必选题
     * @param coursewareId 课件ID
     * @return 课件下的必选题
     */
    public List<CourseQuestion> testing(Long coursewareId) {
       //获取该课件下的必选题
        List<Long> questionId = baseMapper.findQuestionIdAllByCoursewareId(coursewareId);

        if (CollUtil.isEmpty(questionId)) {
            return ListUtil.empty();
        }

        List<CourseQuestion> questionList = courseQuestionService.getByIdIN(StringUtils.join(questionId, ","));
        return questionList;
    }

    /**
     * 从课件必选题中抽取题目
     * @param coursewareId
     * @return
     */
    public List<Long> getQuestionByCoursewareId(Long coursewareId,List<Long> excludeIds) {
        return baseMapper.findAllQuestionIdByCoursewareId(coursewareId,excludeIds);
    }
}
