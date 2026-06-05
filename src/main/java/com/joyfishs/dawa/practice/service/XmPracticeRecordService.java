package com.joyfishs.dawa.practice.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.answer.entity.AnswerReport;
import com.joyfishs.dawa.answer.service.AnswerReportItemService;
import com.joyfishs.dawa.answer.service.AnswerReportService;
import com.joyfishs.dawa.answer.vo.QuestionVo;
import com.joyfishs.dawa.practice.entity.PracticeCollection;
import com.joyfishs.dawa.practice.entity.PracticeRecord;
import com.joyfishs.dawa.practice.mapper.XmPracticeRecordMapper;
import com.joyfishs.utils.SecurityUtil;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class XmPracticeRecordService extends ServiceImpl<XmPracticeRecordMapper, PracticeRecord> {

    @Autowired
    private XmPracticeCollectionService xmPracticeCollectionService;

    @Autowired
    private XmPracticeWrongService xmPracticeWrongService;

    @Autowired
    private AnswerReportItemService answerReportItemService;

    @Autowired
    private AnswerReportService answerReportService;

    // 查询答题记录
    public List<PracticeRecord> findList() {
        Long personId = SecurityUtil.getPersonId();
        List<PracticeRecord> list = baseMapper.findList(personId);
        for (PracticeRecord record : list) {
            // 查询答题卡完成情况
            AnswerReport report = answerReportService.getById(record.getReportId());
            int count = report.getTotalQuestions() - report.getAnsweredQuestions();
            record.setType(count > 0 ? 0 : 1);
        }

        return list;
    }

    // 查询收藏记录
    public List<QuestionVo> findCollectionList() {
        Long personId = SecurityUtil.getPersonId();
        List<QuestionVo> collectionList = xmPracticeCollectionService.getBaseMapper().findCollectionList(personId);
        return getQuestionVos(personId, collectionList);
    }

    // 查询错题记录
    public List<QuestionVo> findWrongList(String courseType) {
        Long personId = SecurityUtil.getPersonId();
        List<QuestionVo> wrongList = xmPracticeWrongService.getBaseMapper().findWrongList(personId, courseType);
        return getQuestionVos(personId, wrongList);
    }

    /*// 查询错题记录-根据课程类别分类
    public Map<String, List<QuestionVo>> findWrongClassifyList() {
        Long personId = SecurityUtil.getPersonId();
        List<QuestionVo> wrongList = xmPracticeWrongService.getBaseMapper().findWrongClassifyList(personId);

        // 根据课程分类 分类
        Map<String, List<QuestionVo>> wrongMap = wrongList.stream()
                .filter(practiceVo -> practiceVo.getCourseTypeName() != null)
                .collect(Collectors.groupingBy(questionVo -> questionVo.getCourseTypeName()));

        return wrongMap;
    }*/

    public List<Map<String, Object>> findWrongClassifyList() {
        Long personId = SecurityUtil.getPersonId();
        return xmPracticeWrongService.getBaseMapper().findWrongClassifyList(personId);
    }

    // 封装答题的其他属性
    private List<QuestionVo> getQuestionVos(Long personId, List<QuestionVo> collectionList) {
        for (QuestionVo vo : collectionList) {
            this.getQuestionVo(personId, vo);
        }
        return collectionList;
    }

    private void getQuestionVo(Long personId, QuestionVo vo) {
        // 查询答题时间
        int answerTime = answerReportItemService.getBaseMapper().getAnswerTimeByPersonIdAndQuestionId(personId, vo.getQuestionId());
        // 正确率
        BigDecimal accuracyRate = answerReportItemService.getBaseMapper().getAccuracyRate(vo.getQuestionId());
        // 易错项
        String wrongItem = answerReportItemService.getBaseMapper().getWrongItem(vo.getQuestionId());
        vo.setAnswerTime(answerTime);
        vo.setAccuracyRate(accuracyRate);
        vo.setWrongItem(StrUtil.isBlank(wrongItem) ? null : wrongItem);

        // 查询该题目是否已收藏
        PracticeCollection collection = xmPracticeCollectionService.getBaseMapper().getByPersonIdAndQuestionId(personId, vo.getQuestionId());
        vo.setIsCollection(collection != null ? 1 : 0);
    }

    // 收藏题目
    public void executeCollection(Long questionId, List<String> myAnswerList) {
        Long personId = SecurityUtil.getPersonId();
        // 查询该题目是否收藏过
        PracticeCollection collection = xmPracticeCollectionService.getBaseMapper().getByPersonIdAndQuestionId(personId, questionId);
        if (collection == null || collection.getId() == null) {
            collection = new PracticeCollection();
            collection.setPersonId(personId);
            collection.setQuestionId(questionId);
            collection.setMyAnswers(JSONUtil.toJsonStr(myAnswerList));
            collection.setCreateTime(new Date());
            xmPracticeCollectionService.save(collection);
        }
    }

    /**
     * 取消收藏
     * @param questionId
     * @param type 1:我的收藏 2:错题集
     */
    public void cancelCollection(Long questionId, Integer type) {
        Long personId = SecurityUtil.getPersonId();
        if (type == 1) {
            xmPracticeCollectionService.getBaseMapper().deleteByPersonIdAndQuestionId(personId, questionId);
        } else if (type == 2) {
            xmPracticeWrongService.getBaseMapper().deleteByPersonIdAndQuestionId(personId, questionId);
        }
    }

    /**
     * 查看题目详情
     * @param questionId
     * @param type 1:我的收藏 2:错题集
     */
    public QuestionVo questionDetail(Long questionId, Integer type) {
        Long personId = SecurityUtil.getPersonId();
        QuestionVo vo = new QuestionVo();
        if (type == 1) {
            vo = xmPracticeCollectionService.getBaseMapper().getCollectionByQuestionId(personId, questionId);
        } else if (type == 2) {
            vo = xmPracticeWrongService.getBaseMapper().getWrongByQuestionId(personId, questionId);
        }
        this.getQuestionVo(personId, vo);
        return vo;
    }

}
