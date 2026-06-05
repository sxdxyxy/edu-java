package com.joyfishs.dawa.assessment.controller;

import com.github.pagehelper.PageInfo;
import com.joyfishs.dawa.assessment.entity.PracticalAssessment;
import com.joyfishs.dawa.assessment.service.PracticalAssessmentService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实操考核控制器
 * 
 * @author safe-edu
 * @since 2026-03-29
 */
@RestController
@RequestMapping("/assessment")
public class PracticalAssessmentController extends BaseController {

    @Autowired
    private PracticalAssessmentService practicalAssessmentService;

    /**
     * 查询用户的考核记录列表
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> listByUser(@PathVariable Long userId) {
        List<PracticalAssessment> list = practicalAssessmentService.listByUserId(userId);
        return AjaxResult.success(list);
    }

    /**
     * 查询用户近期的考核记录
     */
    @GetMapping("/user/{userId}/recent")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> listRecent(@PathVariable Long userId,
                                  @RequestParam(defaultValue = "5") Integer limit) {
        List<PracticalAssessment> list = practicalAssessmentService.listRecentByUserId(userId, limit);
        return AjaxResult.success(list);
    }

    /**
     * 根据考核类型查询
     */
    @GetMapping("/type/{assessmentType}")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> listByType(@PathVariable String assessmentType) {
        List<PracticalAssessment> list = practicalAssessmentService.listByType(assessmentType);
        return AjaxResult.success(list);
    }

    /**
     * 查询指定日期的考核记录
     */
    @GetMapping("/date/{assessmentDate}")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> listByDate(@PathVariable String assessmentDate) {
        try {
            // 简单处理日期格式 yyyy-MM-dd
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse(assessmentDate);
            List<PracticalAssessment> list = practicalAssessmentService.listByDate(date);
            return AjaxResult.success(list);
        } catch (Exception e) {
            return AjaxResult.error("日期格式错误，应为 yyyy-MM-dd");
        }
    }

    /**
     * 查询未通过的考核记录
     */
    @GetMapping("/failed")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> listFailed() {
        List<PracticalAssessment> list = practicalAssessmentService.listFailed();
        return AjaxResult.success(list);
    }

    /**
     * 查询优秀的考核记录
     */
    @GetMapping("/excellent")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> listExcellent() {
        List<PracticalAssessment> list = practicalAssessmentService.listExcellent();
        return AjaxResult.success(list);
    }

    /**
     * 记录考核结果
     */
    @PostMapping("/record")
    @PreAuthorize("@ss.hasPermi('safety:assessment:edit')")
    public AjaxResult<?> recordAssessment(@RequestBody PracticalAssessment assessment) {
        try {
            PracticalAssessment saved = practicalAssessmentService.recordAssessment(assessment);
            return AjaxResult.success("考核记录成功", saved);
        } catch (Exception e) {
            return AjaxResult.error("记录失败：" + e.getMessage());
        }
    }

    /**
     * 更新考核结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('safety:assessment:edit')")
    public AjaxResult<?> updateAssessment(@PathVariable Long id,
                                        @RequestBody PracticalAssessment assessment) {
        try {
            PracticalAssessment updated = practicalAssessmentService.updateAssessment(id, assessment);
            return AjaxResult.success("更新成功", updated);
        } catch (Exception e) {
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取考核统计
     */
    @GetMapping("/statistics")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> getStatistics(@RequestParam(required = false) Long projectId) {
        // 通过项目ID或其他条件过滤统计信息
        // 这里只是一个示例实现
        Double overallPassRate = practicalAssessmentService.getOverallPassRate();
        long totalCount = practicalAssessmentService.countAll();
        long passedCount = practicalAssessmentService.countPassed();
        long failedCount = practicalAssessmentService.countFailed();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("overallPassRate", overallPassRate);
        statistics.put("totalCount", totalCount);
        statistics.put("passedCount", passedCount);
        statistics.put("failedCount", failedCount);
        
        return AjaxResult.success(statistics);
    }

    /**
     * 获取用户考核统计信息
     */
    @GetMapping("/stats/{userId}")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> getStats(@PathVariable Long userId) {
        Double passRate = practicalAssessmentService.getPassRate(userId);
        Double avgScore = practicalAssessmentService.getAverageScore(userId);
        List<PracticalAssessment> allList = practicalAssessmentService.listByUserId(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("passRate", passRate);
        stats.put("averageScore", avgScore);
        stats.put("totalCount", allList.size());
        
        // 统计各结果数量
        long excellentCount = allList.stream().filter(a -> "excellent".equals(a.getResult())).count();
        long passCount = allList.stream().filter(a -> "pass".equals(a.getResult())).count();
        long failCount = allList.stream().filter(a -> "fail".equals(a.getResult())).count();
        
        stats.put("excellentCount", excellentCount);
        stats.put("passCount", passCount);
        stats.put("failCount", failCount);
        
        return AjaxResult.success(stats);
    }

    /**
     * 查询考评员的考核记录
     */
    @GetMapping("/examiner/{examinerId}")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> listByExaminer(@PathVariable Long examinerId) {
        List<PracticalAssessment> list = practicalAssessmentService.listByExaminerId(examinerId);
        return AjaxResult.success(list);
    }

    /**
     * 分页查询所有考核记录
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> listPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String assessmentType,
            @RequestParam(required = false) String result) {
        startPage();
        Map<String, Object> params = new HashMap<>();
        if (userName != null && !userName.trim().isEmpty()) {
            params.put("userName", userName);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            params.put("phone", phone);
        }
        if (assessmentType != null && !assessmentType.trim().isEmpty()) {
            params.put("assessmentType", assessmentType);
        }
        if (result != null && !result.trim().isEmpty()) {
            params.put("result", result);
        }
        List<PracticalAssessment> list = practicalAssessmentService.listPageWithUser(params);
        PageInfo<PracticalAssessment> pageInfo = new PageInfo<>(list);

        Map<String, Object> ret = new HashMap<>();
        ret.put("rows", pageInfo.getList());
        ret.put("total", pageInfo.getTotal());
        return AjaxResult.success(ret);
    }

    /**
     * 获取考核详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> getById(@PathVariable Long id) {
        PracticalAssessment assessment = practicalAssessmentService.getById(id);
        if (assessment == null) {
            return AjaxResult.error("考核记录不存在");
        }
        return AjaxResult.success(assessment);
    }

    /**
     * 删除考核记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('safety:assessment:remove')")
    public AjaxResult<?> delete(@PathVariable Long id) {
        try {
            practicalAssessmentService.removeById(id);
            return AjaxResult.success("删除成功");
        } catch (Exception e) {
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取按类型统计的考核数据
     */
    @GetMapping("/statistics/by-type")
    @PreAuthorize("@ss.hasPermi('safety:assessment:list')")
    public AjaxResult<?> getStatisticsByType() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("safetyCount", practicalAssessmentService.countByType("safety"));
        stats.put("equipmentCount", practicalAssessmentService.countByType("equipment"));
        stats.put("emergencyCount", practicalAssessmentService.countByType("emergency"));
        stats.put("skillCount", practicalAssessmentService.countByType("skill"));
        return AjaxResult.success(stats);
    }
}
