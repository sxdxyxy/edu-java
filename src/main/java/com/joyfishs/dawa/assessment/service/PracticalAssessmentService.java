package com.joyfishs.dawa.assessment.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.assessment.entity.PracticalAssessment;
import com.joyfishs.dawa.assessment.mapper.PracticalAssessmentMapper;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.mapper.ProjectMapper;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.mapper.SysUserMapper;
import com.joyfishs.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实操考核服务类
 * 
 * @author safe-edu
 * @since 2026-03-29
 */
@Service
public class PracticalAssessmentService extends ServiceImpl<PracticalAssessmentMapper, PracticalAssessment> {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ProjectMapper projectMapper;

    /**
     * 根据用户 ID 查询考核记录
     */
    public List<PracticalAssessment> listByUserId(Long userId) {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("assessment_date");
        return list(wrapper);
    }

    /**
     * 根据考核类型查询
     */
    public List<PracticalAssessment> listByType(String assessmentType) {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("assessment_type", assessmentType)
               .orderByDesc("assessment_date");
        return list(wrapper);
    }

    /**
     * 查询用户近期的考核记录
     */
    public List<PracticalAssessment> listRecentByUserId(Long userId, int limit) {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("assessment_date")
               .last("LIMIT " + limit);
        return list(wrapper);
    }

    /**
     * 查询指定日期的考核记录
     */
    public List<PracticalAssessment> listByDate(Date assessmentDate) {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("assessment_date", assessmentDate)
               .orderByAsc("location");
        return list(wrapper);
    }

    /**
     * 查询未通过的考核记录
     */
    public List<PracticalAssessment> listFailed() {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("result", "fail")
               .orderByDesc("assessment_date");
        return list(wrapper);
    }

    /**
     * 查询优秀的考核记录
     */
    public List<PracticalAssessment> listExcellent() {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("result", "excellent")
               .orderByDesc("assessment_date");
        return list(wrapper);
    }

    /**
     * 记录考核结果
     */
    @Transactional(rollbackFor = Exception.class)
    public PracticalAssessment recordAssessment(PracticalAssessment assessment) {
        // 根据分数自动设置结果
        if (assessment.getScore() == null) {
            throw new RuntimeException("分数不能为空");
        }
        
        String result;
        if (assessment.getScore() >= 90) {
            result = "excellent";
        } else if (assessment.getScore() >= 60) {
            result = "pass";
        } else {
            result = "fail";
        }
        assessment.setResult(result);
        assessment.setCreatedAt(new Date());

        // 自动设置 org_id：从关联的项目获取 org_id
        if (assessment.getOrgId() == null && assessment.getProjectId() != null) {
            com.joyfishs.dawa.project.entity.Project project = projectMapper.selectById(assessment.getProjectId());
            if (project != null && project.getOrgId() != null) {
                assessment.setOrgId(project.getOrgId());
            }
        }

        save(assessment);
        return assessment;
    }

    /**
     * 更新考核结果
     */
    @Transactional(rollbackFor = Exception.class)
    public PracticalAssessment updateAssessment(Long id, PracticalAssessment assessment) {
        PracticalAssessment existing = getById(id);
        if (existing == null) {
            throw new RuntimeException("考核记录不存在");
        }
        
        assessment.setId(id);
        assessment.setUpdatedAt(new Date());
        updateById(assessment);
        return getById(id);
    }

    /**
     * 统计用户考核通过率
     */
    public Double getPassRate(Long userId) {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        Long total = count(wrapper);
        
        if (total == 0) {
            return 0.0;
        }
        
        QueryWrapper<PracticalAssessment> passWrapper = new QueryWrapper<>();
        passWrapper.eq("user_id", userId)
                   .in("result", "pass", "excellent");
        Long passed = count(passWrapper);
        
        return (double) passed / total * 100;
    }

    /**
     * 统计用户平均分数
     */
    public Double getAverageScore(Long userId) {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .select("AVG(score)");
        
        // MyBatis Plus 不支持直接返回 AVG，需要手动计算
        List<PracticalAssessment> list = listByUserId(userId);
        if (list.isEmpty()) {
            return 0.0;
        }
        
        return list.stream()
                   .mapToInt(PracticalAssessment::getScore)
                   .average()
                   .orElse(0.0);
    }

    /**
     * 查询考评员的考核记录
     */
    public List<PracticalAssessment> listByExaminerId(Long examinerId) {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("examiner_id", examinerId)
               .orderByDesc("assessment_date");
        return list(wrapper);
    }
    
    /**
     * 统计全部考核的通过率
     */
    public Double getOverallPassRate() {
        long total = countAll();
        if (total == 0) {
            return 0.0;
        }
        long passed = countPassed();
        return (double) passed / total * 100;
    }
    
    /**
     * 统计所有考核数量
     */
    public long countAll() {
        return count();
    }
    
    /**
     * 统计所有通过的考核数量
     */
    public long countPassed() {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.in("result", "pass", "excellent");
        return count(wrapper);
    }
    
    /**
     * 统计所有失败的考核数量
     */
    public long countFailed() {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("result", "fail");
        return count(wrapper);
    }
    
    /**
     * 统计指定组织的考核总数
     */
    public long countTotalAssessments(Long orgId) {
        return baseMapper.countTotalByOrg(orgId);
    }
    
    /**
     * 统计指定组织通过考核的数量
     */
    public long countPassedAssessments(Long orgId) {
        return baseMapper.countPassedByOrg(orgId);
    }
    
    /**
     * 统计指定组织优秀考核的数量
     */
    public long countExcellentAssessments(Long orgId) {
        return baseMapper.countExcellentByOrg(orgId);
    }

    /**
     * 根据考核类型统计数量
     */
    public long countByType(String assessmentType) {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        wrapper.eq("assessment_type", assessmentType);
        return count(wrapper);
    }

    /**
     * 分页查询考核记录
     */
    public List<PracticalAssessment> listPage(Long userId, String assessmentType, String result, Date startDate, Date endDate, java.util.List<Long> managedOrgIds) {
        QueryWrapper<PracticalAssessment> wrapper = new QueryWrapper<>();
        // 显式选择数据库中的字段，排除扩展字段（userName, phone, idCardNo, examinerName）
        wrapper.select(
            "id", "user_id", "assessment_type", "assessment_date", "location", "examiner_id",
            "score", "result", "video_url", "evidence_photos", "remarks", "created_at", "updated_at"
        );
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (assessmentType != null && !assessmentType.trim().isEmpty()) {
            wrapper.eq("assessment_type", assessmentType);
        }
        if (result != null && !result.trim().isEmpty()) {
            wrapper.eq("result", result);
        }
        if (startDate != null) {
            wrapper.ge("assessment_date", startDate);
        }
        if (endDate != null) {
            wrapper.le("assessment_date", endDate);
        }
        // 如果有权限管理的机构 ID 列表，过滤出这些机构管辖的项目
        if (managedOrgIds != null && !managedOrgIds.isEmpty()) {
            // 先查询这些机构下的项目ID列表
            QueryWrapper<Project> projectWrapper = new QueryWrapper<>();
            projectWrapper.select("id").in("org_id", managedOrgIds);
            List<Project> projectList = projectMapper.selectList(projectWrapper);
            if (projectList != null && !projectList.isEmpty()) {
                List<Long> projectIds = projectList.stream().map(Project::getId).collect(Collectors.toList());
                wrapper.in("project_id", projectIds);
            } else {
                // 如果没有项目，返回空结果
                wrapper.eq("1", "0");
            }
        }
        wrapper.orderByDesc("assessment_date");
        return list(wrapper);
    }

    /**
     * 分页查询考核记录（带用户信息）
     */
    public List<PracticalAssessment> listPageWithUser(Map<String, Object> params) {
        // 获取当前登录用户有权限管理的机构 ID 列表
        java.util.List<Long> managedOrgIds = SecurityUtil.getManagedOrgIds();

        List<PracticalAssessment> list = listPage(
            params.containsKey("userId") ? (Long) params.get("userId") : null,
            params.containsKey("assessmentType") ? (String) params.get("assessmentType") : null,
            params.containsKey("result") ? (String) params.get("result") : null,
            params.containsKey("startDate") ? (Date) params.get("startDate") : null,
            params.containsKey("endDate") ? (Date) params.get("endDate") : null,
            managedOrgIds
        );

        // 关联查询用户信息和项目信息
        for (PracticalAssessment assessment : list) {
            if (assessment.getUserId() != null) {
                SysUser user = sysUserMapper.selectById(assessment.getUserId());
                if (user != null) {
                    assessment.setUserName(user.getName());
                    assessment.setPhone(user.getPhone());
                    assessment.setIdCardNo(user.getIdCardNo());
                }
            }
            if (assessment.getExaminerId() != null) {
                SysUser examiner = sysUserMapper.selectById(assessment.getExaminerId());
                if (examiner != null) {
                    assessment.setExaminerName(examiner.getName());
                }
            }
            if (assessment.getProjectId() != null) {
                Project project = projectMapper.selectById(assessment.getProjectId());
                if (project != null) {
                    assessment.setProjectName(project.getProjectName());
                }
            }
        }

        // 过滤搜索条件（用户姓名、手机号）
        return list.stream().filter(assessment -> {
            if (params.containsKey("userName")) {
                String userName = (String) params.get("userName");
                if (assessment.getUserName() == null || !assessment.getUserName().contains(userName)) {
                    return false;
                }
            }
            if (params.containsKey("phone")) {
                String phone = (String) params.get("phone");
                if (assessment.getPhone() == null || !assessment.getPhone().contains(phone)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }
}
