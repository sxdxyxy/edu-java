package com.joyfishs.dawa.course.controller;

import cn.hutool.core.util.ObjectUtil;
import com.joyfishs.dawa.course.domain.bo.CoursewareBo;
import com.joyfishs.dawa.course.domain.vo.CoursewareVo;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.course.entity.Courseware;
import com.joyfishs.dawa.course.service.CourseCoursewareService;
import com.joyfishs.dawa.course.service.CourseService;
import com.joyfishs.dawa.course.service.VodService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import com.joyfishs.utils.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "课件管理")
@RequestMapping("/courseManagement/courseware")
public class CourseCoursewareController extends BaseController {
    private final CourseService courseService;
    private final CourseCoursewareService courseCoursewareService;
    private final VodService vodService;

    @PostMapping("/addOrUpdate")
    @ApiOperation(value = "课件新增或编辑",
            notes = "如果为视频类型课件，不要设置fileUrl，提交后会返回腾讯云的客户端上传签名，使用腾讯的【Web 端上传 SDK】上传，参考https://cloud.tencent.com/document/product/266/9239")
    @Log(title = "课件新增或编辑", businessType = BusinessType.INSERT)
    public R<Courseware> add(@RequestBody CoursewareBo bo) throws Exception {
        if (!bo.isResources()) {
            if (StringUtils.isNull(bo.getCourseId())) {
                throw new CustomException("课程的ID不能空");
            }
            Course course = courseService.getById(bo.getCourseId());
            if (course == null || StringUtils.isNull(course.getId())) {
                throw new CustomException("未找到课程信息");
            }
        }
        return R.ok(courseCoursewareService.addOrUpdate(bo));
    }

    @GetMapping("/getUploadSignature")
    @ApiOperation(value = "课件视频上传签名")
    public R<String> getUploadSignature(@ApiParam(value = "课件id", required = true) @RequestParam Long coursewareId) throws Exception {
        if (ObjectUtil.isNull(courseCoursewareService.getById(coursewareId))) {
            return R.fail("课件id不正确");
        }
        String result = vodService.getUploadSignature(coursewareId);
        return R.ok("请使用此签名上传腾讯云vod", result);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@ss.hasPermi('courseware:del')")
    @ApiOperation(value = "删除课件")
    @Log(title = "删除课件", businessType = BusinessType.DELETE)
    public AjaxResult<?> delete(@RequestParam Long id) {
        courseCoursewareService.deleteCourseware(id);
        return AjaxResult.success();
    }

    @GetMapping("/details")
    @ApiOperation(value = "课件详情")
    public AjaxResult<?> get(@RequestParam Long coursewareId, Integer isEdit, Integer isDown) { // 是否编辑  1-是  0-否  isDown 1 - 是否下载
        return AjaxResult.success(courseCoursewareService.getDetailById(coursewareId, isEdit, isDown));
    }


    @GetMapping("/resource")
    @ApiOperation(value = "资源库-课件分页列表")
    public TableDataInfo<CoursewareVo> resource(@ApiParam(value = "课件类别  1 = 视频  3- 文档", required = false) @RequestParam(required = false) Integer classify) {
        startPage();
        List<CoursewareVo> coursewareList = courseCoursewareService.findResourceList(classify);
        return getDataTable(coursewareList);
    }

    @PostMapping("/addViewCount")
    @ApiOperation(value = "资源库-课件增加浏览次数")
    public R<Void> addViewCount(@ApiParam(value = "课件id", required = true) @RequestParam Long coursewareId)  {
        courseCoursewareService.addViewCount(coursewareId);
        return R.ok();
    }
}
