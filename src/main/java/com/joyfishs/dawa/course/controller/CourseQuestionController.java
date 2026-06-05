package com.joyfishs.dawa.course.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.course.domain.vo.QuestionCountVo;
import com.joyfishs.dawa.course.entity.CourseQuestion;
import com.joyfishs.dawa.course.enums.QuestionType;
import com.joyfishs.dawa.course.service.CourseQuestionService;
import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import com.joyfishs.utils.page.TableDataInfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "课程题目管理")
@RequestMapping("courseManagement/question")
public class CourseQuestionController extends BaseController {

    @Autowired
    private CourseQuestionService courseQuestionService;
    @Autowired
    private SysOssService sysOssService;

    @PostMapping("/addOrUpdate")
    @PreAuthorize("@ss.hasPermi('question:edit')")
    @ApiOperation(value = "题目新增或修改")
    @Log(title = "题目新增或修改", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody CourseQuestion t) {
        courseQuestionService.addOrUpdate(t);
        return AjaxResult.success();
    }


	@DeleteMapping("/delete")
	@PreAuthorize("@ss.hasPermi('question:edit')")
    @ApiOperation(value = "删除题目")
	@Log(title = "删除题目", businessType = BusinessType.DELETE)
	public AjaxResult<?> delete(Long id,String deleteReason) {
		courseQuestionService.deleteQuestion(id,deleteReason);
		return AjaxResult.success();
	}

    @GetMapping("/details")
    @PreAuthorize("@ss.hasPermi('question:detail')")
    @ApiOperation(value = "查看题目详情")
    public AjaxResult<?> details(Long id) {
        return AjaxResult.success(courseQuestionService.details(id));
    }

    @GetMapping("/listPage")
    @PreAuthorize("@ss.hasPermi('course:list')")
    @ApiOperation(value = "题目分页列表")
    public TableDataInfo listPage(CourseQuestion t) {
        startPage();
        List<CourseQuestion> questionList = courseQuestionService.listPage(t);
        return getDataTable(questionList);
    }


    @PostMapping("/importTemplate")
    @PreAuthorize("@ss.hasPermi('question:importTemplate')")
    @ApiOperation(value = "批量导入题目")
    @Log(title = "批量导入", businessType = BusinessType.INSERT)
    public AjaxResult<?> importTemplate(String fileUrl,Long courseId) {
        if (StringUtils.isNull(courseId)) {
            throw new CustomException("课程ID不能为空！");
        }
        File tempExcel = sysOssService.downloadByUrl(fileUrl);
        //读取文件
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(tempExcel));
        List<List<Object>> readAll = reader.read();

        //储存题目对象，进行批量写入
        List<CourseQuestion> questionList = new ArrayList<>();


        //第个元素是字段名称，可删除掉
        readAll.remove(0);
        //遍历第一个List
        String questionType;
        if(CollUtil.isEmpty(readAll)){
            throw new CustomException("没有指定题目类型!");
        }
        for (List<Object> object : readAll) {
            questionType = (String) object.get(0);

            //题目类型不为
            if ((!"单选题".equals(questionType)
                    && !"多选题".equals(questionType)
                    && !"简答题".equals(questionType)
                    && !"填空题".equals(questionType)
                    && !"判断题".equals(questionType)
            )
            ) {
                throw new CustomException("题目类型错误!,名称为：" + object.get(1));
            }

            if ("简答题".equals(questionType) || "填空题".equals(questionType)) {
                throw new CustomException("简答题和填空题暂不支持导入!");
            }

            if (object.get(1) == null) {
                throw new CustomException("不能上传提干为空的题目！ 请检查后再进行上传！");
            }

            //系列判断。。。。。。。
            if ("单选题".equals(questionType) || "多选题".equals(questionType)) {
                if (StringUtils.isNull(object.get(2)) || StringUtils.isNull(object.get(3))
                        || StringUtils.isNull(object.get(4)) || StringUtils.isNull(object.get(5))) {
                    throw new CustomException("答案内容不能为空！ 请修改题目名称为："+String.valueOf(object.get(1)));
                }
            }

            if ("单选题".equals(String.valueOf(questionType))
                    && (String.valueOf(object.get(8)).length() > 1
                    || String.valueOf(object.get(8)).length() < 1
                    || !String.valueOf(object.get(8)).matches("[A-D]"))) {
                throw new CustomException("单选题正确答案只能有一个，并且是大写字母A-D,请检查题目名称为："+ String.valueOf(object.get(1)));
            }

            if ("多选题".equals(String.valueOf(questionType)) && (String.valueOf(object.get(8)).length() < 1 || String.valueOf(object.get(8)).length() > 6)) {
                throw new CustomException("多选题必须是大写字母，请检查题目名称为："+ String.valueOf(object.get(1)));
            }

            //验证多选题正确答案是否有多个相同的选项
            if (courseQuestionService.duplicationStr(String.valueOf(object.get(8)))) {
                throw new CustomException("正确答案不可有相同的选项！ 请检查题目名称为："+String.valueOf(object.get(1)));
            }

            if ("判断题".equals(String.valueOf(questionType))) {
               if (!"正确".equals(String.valueOf(object.get(8))) && !"错误".equals(String.valueOf(object.get(8)))) {
                   throw new CustomException("判断题的正确答案有误! 请检查题目名称为："+String.valueOf(object.get(1)));
               }
            }

            if (StringUtils.isNull(object.get(8))) {
                throw new CustomException("正确答案内容不能为空! 请检查题目名称为："+String.valueOf(object.get(1)));
            }

            QuestionType questionTypeEnum = null;
            if ("单选题".equals(questionType)) {
                questionTypeEnum = QuestionType.SingleChoice;
            }
            if ("多选题".equals(questionType)) {
                questionTypeEnum = QuestionType.MultipleChoice;
            }
            if ("判断题".equals(questionType)) {
                questionTypeEnum = QuestionType.TrueOrFalse;
            }
            if ("简答题".equals(questionType)) {
                questionTypeEnum = QuestionType.ShortAnswer;
            }
            if ("填空题".equals(questionType)) {
                questionTypeEnum = QuestionType.FillInTheBlank;
            }

            if("填空题".equals(String.valueOf(questionType)) && (
                    StringUtils.isNull(String.valueOf(object.get(2)))
                    || StringUtils.isNull(String.valueOf(object.get(3)))
                    || StringUtils.isNull(String.valueOf(object.get(4)))
                    || StringUtils.isNull(String.valueOf(object.get(5)))
                    || StringUtils.isNull(String.valueOf(object.get(6)))
                    )
            ) {
                throw new CustomException("填空题答案不能为空！ 请检查题目名称为： "+ String.valueOf(object.get(1)));
            }

            //题目封装
            CourseQuestion courseQuestion = new CourseQuestion();

            //基本字段信息
            courseQuestion.setCreateTime(new Date());
            courseQuestion.setCreateBy(SecurityUtil.getUserId());
            courseQuestion.setCourseId(courseId);
            courseQuestion.setIsDelete(YesOrNoState.NO.getState());

            courseQuestion.setQuestionType(questionTypeEnum);
            courseQuestion.setQuestionName(String.valueOf(object.get(1)));
            courseQuestion.setAnswerParse(String.valueOf(object.get(9)));

            if ("判断题".equals(questionType)) {
                if ("正确".equals(String.valueOf(object.get(8)))) {
                    courseQuestion.setRightAnswers("A");
                } else {
                    courseQuestion.setRightAnswers("B");
                }

                //塞入集合
                questionList.add(courseQuestion);
                continue;
            }

            if ("填空题".equals(questionType)) {
                List<String> answerList = new ArrayList<>();
                if (StringUtils.isNotNull(object.get(2))) {
                    answerList.add(String.valueOf(object.get(2)));
                }
                if (StringUtils.isNotNull(object.get(3))) {
                    answerList.add(String.valueOf(object.get(3)));
                }
                if (StringUtils.isNotNull(object.get(4))) {
                    answerList.add(String.valueOf(object.get(4)));
                }
                if (StringUtils.isNotNull(object.get(5))) {
                    answerList.add(String.valueOf(object.get(5)));
                }
                if (StringUtils.isNotNull(object.get(6))) {
                    answerList.add(String.valueOf(object.get(6)));
                }
                courseQuestion.setAnswerList(answerList);

                questionList.add(courseQuestion);
                continue;
            }

            if ("多选题".equals(questionType) && StringUtils.isEmpty(String.valueOf(object.get(2)))) {
                throw new CustomException("多选题答案A不能为空！请检查题目名称为："+String.valueOf(object.get(1)));
            }
            if ("多选题".equals(questionType) && StringUtils.isEmpty(String.valueOf(object.get(3)))) {
                throw new CustomException("多选题答案B不能为空！请检查题目名称为："+String.valueOf(object.get(1)));
            }
            if ("多选题".equals(questionType) && StringUtils.isEmpty(String.valueOf(object.get(4)))) {
                throw new CustomException("多选题答案C不能为空！请检查题目名称为："+String.valueOf(object.get(1)));
            }


            courseQuestion.setAnswerA(String.valueOf(object.get(2)));
            courseQuestion.setAnswerB(String.valueOf(object.get(3)));
            courseQuestion.setAnswerC(String.valueOf(object.get(4)));
            courseQuestion.setAnswerD(String.valueOf(object.get(5)));
            if ("多选题".equals(questionType)) {
                courseQuestion.setAnswerE(String.valueOf(object.get(6)));
            }
            if ("多选题".equals(questionType)) {
                courseQuestion.setAnswerF(String.valueOf(object.get(7)));
            }
            courseQuestion.setRightAnswers(String.valueOf(object.get(8)));
            //塞入集合
            questionList.add(courseQuestion);
        }

        //批量写入
        courseQuestionService.saveBatch(questionList);
        return AjaxResult.success("导入成功！");
    }


    /**
     * 项目管理查询
     * @param ids
     * @return
     */
    @PostMapping("/getQuestionNumByCourseIds")
    @PreAuthorize("@ss.hasPermi('question:detail')")
    public AjaxResult<?> getQuestionNumByCourseIds(@RequestBody String ids) {
       int startIndex = ids.indexOf("[") + 1;
        int endIndex = ids.indexOf("]");

        String s = ids.substring(startIndex, endIndex);
        List<Long> collect = Arrays.stream(s.split(",")).map(Long::valueOf).collect(Collectors.toList());
        if(collect.isEmpty()) {
            return AjaxResult.success(new ArrayList<>());
        }
        List<QuestionCountVo> list = courseQuestionService.getQuestionNumByCourseIds(collect);
        return AjaxResult.success(list);
    }

}
