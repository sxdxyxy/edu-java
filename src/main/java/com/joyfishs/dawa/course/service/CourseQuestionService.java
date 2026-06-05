package com.joyfishs.dawa.course.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.joyfishs.dawa.answer.vo.QuestionVo;
import com.joyfishs.dawa.course.domain.vo.CourseWordVo;
import com.joyfishs.dawa.course.domain.vo.QuestionCountVo;
import com.joyfishs.dawa.course.domain.vo.QuestionWordVo;
import com.joyfishs.dawa.course.entity.CourseQuestion;
import com.joyfishs.dawa.course.enums.QuestionType;
import com.joyfishs.dawa.course.mapper.CourseQuestionMapper;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 考试题目表 服务实现类
 * </p>
 *
 * @author xiaodai
 * @since 2021-08-17
 */
@Slf4j
@Service
public class CourseQuestionService extends ServiceImpl<CourseQuestionMapper, CourseQuestion> {

    /**
     * 新增或修改题目
     * @param t
     */
    public void addOrUpdate(CourseQuestion t) {
        if (StringUtils.isNull(t.getCourseId())) {
            throw new CustomException("所属课程ID不能为空！");
        }
        if (ObjUtil.isEmpty(t.getQuestionType())) {
            throw new  CustomException("题目类型不能为空！");
        }
        if(StringUtils.isEmpty(t.getRightAnswers())) {
            throw new  CustomException("正确答案不能为空！");
        }

        if (QuestionType.ShortAnswer.equals(t.getQuestionType()) || QuestionType.FillInTheBlank.equals(t.getQuestionType())) {
            throw new CustomException("暂不支持新增填空题、和简答题!");
        }

        CourseQuestion question = getById(t.getId());

        if (question == null || StringUtils.isNull(t.getId())){
            t.setIsDelete(YesOrNoState.NO.getState());
            t.setCreateTime(new Date());
            t.setCreateBy(SecurityUtil.getUserId());
            save(t);
        }else {
            t.setUpdateTime(new Date());
            t.setUpdateBy(SecurityUtil.getUserId());
            updateById(t);
        }
    }



    /**
     * 删除题目
     * @param id 题目ID
     * @param deleteReason 删除原因
     */
    public void deleteQuestion(Long id, String deleteReason) {
        if (StringUtils.isNull(id) && StringUtils.isNull(deleteReason)) {
            throw new CustomException("题目ID或删除理由不能为空！");
        }

        CourseQuestion question = getById(id);
        if (question == null || StringUtils.isNull(question.getId())) {
            throw new CustomException("未找到该题目！");
        }

        question.setIsDelete(YesOrNoState.YES.getState());
        question.setUpdateTime(new Date());
        question.setDeleteReason(deleteReason);
        question.setDeleteBy(SecurityUtil.getUserId());

        updateById(question);
    }


    /**
     * 查看题目详情
     * @param id 题目ID
     * @return 题目详情
     */
    public CourseQuestion details(Long id) {
        if (StringUtils.isNull(id)) {
            throw new CustomException("题目ID不能为空！");
        }

        CourseQuestion question = getById(id);
        if (question == null || StringUtils.isNull(question.getId())) {
            throw new CustomException("未找到该题目！");
        }
        return question;
    }


    /**
     * 查询题目列表
     * @param t 参数
     * @return 题目集合
     */
    public List<CourseQuestion> listPage(CourseQuestion t) {
        List<CourseQuestion> list = baseMapper.questionList(t);
        log.info("Question List Query Result: {} items", list != null ? list.size() : 0);
        return list;
    }

    /**
     * 通过课程ID获取 题目ID
     * @param id 课程ID
     * @return 所有题目ID
     */
    public List<Long> getQuestionIds(Long id,List<Long> excludeids,boolean excludeType) {
//        LambdaQueryWrapper<XmCourseQuestion> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(XmCourseQuestion::getCourseId,id);
//        queryWrapper.eq(XmCourseQuestion::getIsDelete,YesOrNoState.NO.getState());
//        if(excludeType) queryWrapper.notIn(XmCourseQuestion::getQuestionType,4,5);
//        //排除部分
//        if(excludeids != null && excludeids.size() > 0) queryWrapper.notIn(XmCourseQuestion::getId,excludeids);
//        queryWrapper.select(XmCourseQuestion::getId);
        List<Long> list = baseMapper.selectIdList(id);
         return list;
    }


    /**
     * 通过课程ID获取题目
     * @param id 课程ID
     * @return 题目集合
     */
    public List<CourseQuestion> getQuestionId(Long id) {
        LambdaQueryWrapper<CourseQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseQuestion::getCourseId,id);

        List<CourseQuestion> questionList = list(queryWrapper);
        return questionList;
    }


    /**
     * 验证一个字母是否为大写
     * @param str 字符串
     * @return
     */
    public boolean isCapital(String str){

        return str.matches("[A-D]+");

        //小写 返回true，不是小写返回false
//        if(Character.isLowerCase(str.charAt(0))) return false;
//        return true;
    }

    /**
     * 返回题目数量
     * @param id
     * @return
     */
    public Integer getQuestionCount(Long id) {
        return baseMapper.getQuestionCount(id);
    }

    /**
     * 验证字符串中是否包含相同的内容
     * @param str 验证的字符串
     * @return true -包含； false-不包含
     */
    public boolean duplicationStr(String str){
        System.out.println(str);
        for (int i = 0; i < str.length(); i++) {
            if (str.lastIndexOf(str.charAt(i)) !=i ) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询题目类别数量
     * @param ids
     * @return
     */
	public List<QuestionCountVo> getQuestionNumByCourseIds(List<Long> ids) {
	    return baseMapper.getQuestionNumByCourseIds(ids);
	}


    /**
     *  返回题目
     * @param courseId  课程ID
     * @return 题目列表
     */
    public List<CourseQuestion> findQuestionList(Long courseId) {
        return baseMapper.findQuestionByCourseId(courseId);
    }

    /**
     * 通过题目ID List 获取题目
     * @param questionId 题目IDList
     * @return 题目
     */
    public List<CourseQuestion> getByIdIN(String questionId) {
        return baseMapper.getByIdIN(questionId);
    }

    /**
     * 课程的IDS
     * @param courseIds
     * @return
     */
    public List<CourseQuestion> findByCourseIdIN(String courseIds) {
        return baseMapper.findByCourseIdIN(courseIds);
    }

    /**
     * 对比答案
     */
    public boolean compareAnswer(Long questionId, List<String> answerList) {
        if (CollUtil.isEmpty(answerList)) {
            return false;
        }
        CourseQuestion details = this.details(questionId);
        //正确答案
        List<String> rightAnswersList = details.getRightAnswersList();
        //多选，填空
        if (QuestionType.MultipleChoice.equals(details.getQuestionType()) || QuestionType.FillInTheBlank.equals(details.getQuestionType())) {
            //长度不一样直接返回false
            if (rightAnswersList.size() != answerList.size()) {
                return false;
            }
            //循环比对
            //正确答案
            boolean tag = false;
            //传入的答案
            for (String s1 : answerList) {
                if (ListUtil.lastIndexOf(rightAnswersList, s1::equals) >= 0) {
                    //找到就跳出
                    tag = true;
                    break;
                }
            }
            if (!tag) {
                return false;
            }
            return true;
        } else {
            boolean equals = details.getRightAnswers().equals(answerList.get(0));
            return equals;
        }
    }

    /**
     * 题目id查询题目
     * @param id
     * @return
     */
	public QuestionVo getQuestionVo(Long id) {
	    return baseMapper.getQuestionVo(id);
	}

    public static void main(String[] args) throws IOException {
        List<CourseWordVo> courseList = Lists.newArrayList();
        CourseWordVo course1 = new CourseWordVo().setId(1).setCourseType("安全操作规程").setCourseName("电焊工安全操作规程");
        course1.getQuestionList().add(new QuestionWordVo()
                .setId(course1.getId())
                .setQuestionName("以下关于电焊工安全操作规程说法错误的是（ ）。")
                .setQuestionType("单选题")
                .setAnswerA("电焊工必须经过专业安全技术培训，掌握了焊接操作技术和设备的基本原理及其使用方法，考试合格，持证上岗")
                .setAnswerB("非电焊工严禁进行电焊作业")
                .setAnswerC("操作前应首先检查焊机和工具，如焊钳和焊接电缆的绝缘、焊机外壳保护接地和焊机的各接线点等，确认安全合格方可作业")
                .setRightAnswers("D")
                .setAnswerParse("操作时应穿电焊工作服、绝缘鞋和戴电焊手套、防护面罩等安全防护用品，高处作业时系安全带。"));
        course1.getQuestionList().add(new QuestionWordVo()
                .setId(course1.getId())
                .setQuestionName("操作时遇下列情况必须切断电源（ ）。")
                .setQuestionType("多选题")
                .setAnswerA("改变电焊机接头时")
                .setAnswerB("更换焊件需要改接二次回路时")
                .setAnswerC("转移工作地点搬动焊机时")
                        .setAnswerD("焊机发生故障需进行检修时")
                        .setAnswerE("更换保险装置时")
                .setRightAnswers("A，B，C，D，E，F")
                .setAnswerParse("无"));
        CourseWordVo course2 = new CourseWordVo().setId(2).setCourseType("安全操作规程2").setCourseName("电焊工安全操作规程2");
        course2.getQuestionList().add(new QuestionWordVo()
                .setId(course2.getId())
                .setQuestionName("在密封容器内焊接时，应采取通风措施；间歇作业时焊工应到外面休息；容器内照明电压不得超过24V；应用绝缘材料将焊工身体与焊件隔离。（ ））")
                .setQuestionType("判断题")
                .setRightAnswers("错误")
                .setAnswerParse("容器内照明电压不得超过12V."));
        courseList.add(course1);
        courseList.add(course2);
        Map<String, Object> data = new HashMap<>();
        data.put("courseList", courseList);
        Configure config = Configure.builder()
                .useSpringEL()
                .build();
        XWPFTemplate template = XWPFTemplate.compile("D:\\project\\questionTemplate.docx", config);
        template.render(data);
        template.writeToFile("D:\\project\\questionTemplate-sample.docx");
    }
}
