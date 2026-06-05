package com.joyfishs.dawa.course.controller;

import cn.hutool.core.bean.BeanUtil;
import com.joyfishs.dawa.course.domain.vo.CourseVo;
import com.joyfishs.dawa.course.domain.vo.CoursewareVo;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.course.service.CourseService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@Api(tags = "课程管理")
@RequestMapping("/courseManagement/course")
public class CourseController extends BaseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/addOrUpdate")
    @PreAuthorize("@ss.hasPermi('course:add')")
    @ApiOperation(value = "课程新增或修改")
    @Log(title = "课程新增或修改", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody CourseVo t) {
        Course course = new Course();
        BeanUtil.copyProperties(t, course, "type");
        course.setType(Course.queryType(t.getType()));
        courseService.addOrUpdate(course);
        return AjaxResult.success();
    }

    @GetMapping("/sync")
    @PreAuthorize("@ss.hasPermi('course:add')")
    @ApiOperation(value = "同步第三方课程数据")
    public R<Void> sync() {
        courseService.sync();
        return R.ok();
    }

    @GetMapping("/downQuestion")
    @ApiOperation(value = "下载题库数据")
    public R<String> downQuestion(CourseVo t) throws IOException {
        Course course = new Course();
        BeanUtil.copyProperties(t, course, "type");
        course.setType(Course.queryType(t.getType()));
        return R.ok(courseService.buildQuestionWordFile(course));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@ss.hasPermi('course:delete')")
    @ApiOperation(value = "删除课程")
    @Log(title = "删除课程", businessType = BusinessType.DELETE)
    public AjaxResult<?> delete(String ids, String deleteReason) {
        courseService.deleteCourse(ids, deleteReason);
        return AjaxResult.success();
    }

    @GetMapping("/courseware/details")
    @PreAuthorize("@ss.hasPermi('courseware:list')")
    @ApiOperation(value = "课程课件列表")
    public TableDataInfo<CoursewareVo> get(Long id) {
        List<CoursewareVo> list = courseService.getDetailsById(id);
        return getDataTable(list);
    }


    @GetMapping("/details")
    @PreAuthorize("@ss.hasPermi('course:details')")
    @ApiOperation(value = "课程详情")
    public AjaxResult<?> getDetails(@RequestParam Long id) {
        Course t = courseService.getCourseById(id);
        return AjaxResult.success(t);
    }

    @GetMapping("/listPage")
    @PreAuthorize("@ss.hasPermi('course:list')")
    @ApiOperation(value = "课程分页列表")
    public TableDataInfo<?> listPage(CourseVo t) {
        startPage();
        Course course = new Course();
        BeanUtil.copyProperties(t, course, "type");
        course.setType(Course.queryType(t.getType()));
        List<Course> courseList = courseService.getCourseList(course);
        return getDataTable(courseList);
    }

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('course:list')")
    @ApiOperation(value = "分类分页查询课程列表")
    public TableDataInfo<?> listPage(@RequestParam(required = false) Integer classType,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) List<Integer> tags,
                                  @RequestParam String type) {
        startPage();
        return getDataTable(courseService.getCourseListByClass(classType, name, tags, Course.queryType(type)));
    }

    @GetMapping("/questionList")
    @PreAuthorize("@ss.hasPermi('course:question:list')")
    @ApiOperation(value = "通过课程获取下面所有题目ID")
    public AjaxResult<?> getQuestion(Course t) {
        List<Long> questionList = courseService.getQuestionIdList(t);
        return AjaxResult.success(questionList);
    }
}
