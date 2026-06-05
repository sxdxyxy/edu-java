package com.joyfishs.dawa.access.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.access.dto.AccessRecordDTO;
import com.joyfishs.dawa.access.entity.AccessRecord;
import com.joyfishs.dawa.access.mapper.AccessRecordMapper;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.mapper.ProjectMapper;
import com.joyfishs.dawa.safetycode.entity.SafetyCode;
import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.mapper.SysUserMapper;
import com.joyfishs.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 准入记录服务类
 *
 * @author safe-edu
 * @since 2026-03-29
 */
@Service
public class AccessRecordService extends ServiceImpl<AccessRecordMapper, AccessRecord> {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SafetyCodeService safetyCodeService;

    /**
     * 根据用户 ID 查询准入记录
     */
    public List<AccessRecord> listByUserId(Long userId) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("access_time");
        return list(wrapper);
    }

    /**
     * 根据用户 ID 和项目 ID 查询准入记录
     */
    public List<AccessRecord> listByUserIdAndProject(Long userId, Long projectId) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (projectId != null) {
            wrapper.eq("project_id", projectId);
        }
        wrapper.orderByDesc("access_time");
        return list(wrapper);
    }

    /**
     * 统计当前在场人数（按项目）
     */
    public int countOnsite(Long projectId) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.ge("access_time", getTodayStart())
               .isNull("exit_time");
        if (projectId != null) {
            wrapper.eq("project_id", projectId);
        }
        return Math.toIntExact(count(wrapper));
    }

    /**
     * 根据项目 ID 查询准入记录
     */
    public List<AccessRecord> listByProjectId(Long projectId) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
               .orderByDesc("access_time");
        return list(wrapper);
    }

    /**
     * 查询用户今日的准入记录
     */
    public List<AccessRecord> listTodayByUserId(Long userId) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .ge("access_time", getTodayStart())
               .orderByDesc("access_time");
        return list(wrapper);
    }

    /**
     * 记录进场
     */
    @Transactional(rollbackFor = Exception.class)
    public AccessRecord recordAccess(Long userId, Long projectId, String accessType,
                                      String safetyCodeColor, String gateId, String cameraSnapshot,
                                      String location, String remarks) {
        AccessRecord record = new AccessRecord();
        record.setUserId(userId);
        record.setProjectId(projectId);
        record.setAccessTime(new Date());
        record.setAccessType(accessType);
        record.setGateId(gateId);
        record.setCameraSnapshot(cameraSnapshot);
        record.setLocation(location);
        record.setRemarks(remarks);
        record.setCreatedAt(new Date());

        // 自动设置 org_id：从关联的项目获取 org_id
        Project project = projectMapper.selectById(projectId);
        if (project != null && project.getOrgId() != null) {
            record.setOrgId(project.getOrgId());
        }

        // 自动获取安全码颜色快照
        if (safetyCodeColor == null && userId != null && projectId != null) {
            SafetyCode safetyCode = safetyCodeService.getByUserIdAndProject(userId, projectId);
            if (safetyCode != null) {
                safetyCodeColor = safetyCode.getColor();
            }
        }
        record.setSafetyCodeColor(safetyCodeColor);

        save(record);
        return record;
    }

    /**
     * 记录出场
     */
    @Transactional(rollbackFor = Exception.class)
    public AccessRecord recordExit(Long accessId) {
        AccessRecord record = getById(accessId);
        if (record == null) {
            throw new RuntimeException("准入记录不存在");
        }

        record.setExitTime(new Date());

        // 记录出场时的安全码颜色快照
        if (record.getUserId() != null && record.getProjectId() != null) {
            SafetyCode safetyCode = safetyCodeService.getByUserIdAndProject(record.getUserId(), record.getProjectId());
            if (safetyCode != null) {
                // 仅在未设置颜色时设置（保留进场时的颜色快照）
                if (record.getSafetyCodeColor() == null) {
                    record.setSafetyCodeColor(safetyCode.getColor());
                }
            }
        }

        updateById(record);
        return record;
    }

    /**
     * 查询当前在场人员
     */
    public List<AccessRecord> listCurrentOnSite(Long projectId) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
               .isNotNull("access_time")
               .isNull("exit_time")
               .orderByAsc("access_time");
        return list(wrapper);
    }

    /**
     * 统计今日进场人数
     */
    public Integer countTodayAccess(Long projectId) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
               .ge("access_time", getTodayStart());
        return Math.toIntExact(count(wrapper));
    }

    /**
     * 获取今日零点时间
     */
    private Date getTodayStart() {
        Date now = new Date();
        // 简化处理，实际应该使用时区转换
        long midnight = now.getTime() - (now.getTime() % (24 * 60 * 60 * 1000L));
        return new Date(midnight);
    }
    
    /**
     * 统计指定组织今日入场人数
     */
    public long countTodayEntries(Long orgId) {
        return baseMapper.countTodayEntriesByOrg(orgId);
    }
    
    /**
     * 统计指定组织今日出场人数
     */
    public long countTodayExits(Long orgId) {
        return baseMapper.countTodayExitsByOrg(orgId);
    }
    
    /**
     * 统计指定组织当前在场人数
     */
    public long getCurrentOnsiteCount(Long orgId) {
        return baseMapper.countCurrentOnsiteByOrg(orgId);
    }
    
    /**
     * 批量删除准入记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ID列表不能为空");
        }
        removeBatchByIds(ids);
    }
    
    /**
     * 批量入场登记
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AccessRecord> batchEntry(List<AccessRecordDTO> records) {
        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException("记录列表不能为空");
        }

        List<AccessRecord> accessRecords = new ArrayList<>();
        for (AccessRecordDTO dto : records) {
            AccessRecord record = new AccessRecord();
            record.setUserId(dto.getUserId());
            record.setProjectId(dto.getProjectId());
            record.setAccessTime(new Date());
            record.setAccessType(dto.getAccessType());
            record.setSafetyCodeColor(dto.getSafetyCodeColor());
            record.setGateId(dto.getGateId());
            record.setCameraSnapshot(dto.getCameraSnapshot());
            record.setLocation(dto.getLocation());
            record.setRemarks(dto.getRemarks());
            record.setCreatedAt(new Date());

            save(record);
            accessRecords.add(record);
        }
        return accessRecords;
    }
    
    /**
     * 批量出场登记
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AccessRecord> batchExit(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ID列表不能为空");
        }
        
        List<AccessRecord> updatedRecords = new ArrayList<>();
        for (Long id : ids) {
            AccessRecord record = getById(id);
            if (record == null) {
                throw new RuntimeException("准入记录不存在，ID：" + id);
            }
            
            record.setExitTime(new Date());
            updateById(record);
            updatedRecords.add(record);
        }
        return updatedRecords;
    }

    /**
     * 统计全部准入记录数量
     */
    public long count() {
        return baseMapper.selectCount(null);
    }

    /**
     * 统计今日进场人数（全局）
     */
    public long countTodayEntry() {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.ge("access_time", getTodayStart());
        return count(wrapper);
    }

    /**
     * 统计今日出场人数（全局）
     */
    public long countTodayExit() {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.ge("exit_time", getTodayStart())
               .isNotNull("exit_time");
        return count(wrapper);
    }

    /**
     * 统计当前在场人数（全局）
     */
    public long countCurrentOnSite() {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.isNotNull("access_time")
               .isNull("exit_time");
        return count(wrapper);
    }

    /**
     * 分页查询准入记录（带用户信息）
     */
    public List<AccessRecord> listPageWithUser(Map<String, Object> params) {
        // 获取当前登录用户有权限管理的机构 ID 列表
        java.util.List<Long> managedOrgIds = SecurityUtil.getManagedOrgIds();

        List<AccessRecord> list = listPage(
            params.containsKey("userId") ? (Long) params.get("userId") : null,
            params.containsKey("projectId") ? (Long) params.get("projectId") : null,
            params.containsKey("accessType") ? (String) params.get("accessType") : null,
            params.containsKey("startDate") ? (Date) params.get("startDate") : null,
            params.containsKey("endDate") ? (Date) params.get("endDate") : null,
            managedOrgIds
        );

        // 关联查询用户信息和项目信息
        for (AccessRecord record : list) {
            if (record.getUserId() != null) {
                SysUser user = sysUserMapper.selectById(record.getUserId());
                if (user != null) {
                    record.setUserName(user.getName());
                    record.setPhone(user.getPhone());
                    record.setIdCardNo(user.getIdCardNo());
                }
            }
            if (record.getProjectId() != null) {
                Project project = projectMapper.selectById(record.getProjectId());
                if (project != null) {
                    record.setProjectName(project.getProjectName());
                }
            }
        }

        // 过滤搜索条件（用户姓名、手机号）
        return list.stream().filter(record -> {
            if (params.containsKey("userName")) {
                String userName = (String) params.get("userName");
                if (record.getUserName() == null || !record.getUserName().contains(userName)) {
                    return false;
                }
            }
            if (params.containsKey("phone")) {
                String phone = (String) params.get("phone");
                if (record.getPhone() == null || !record.getPhone().contains(phone)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * 根据准入类型统计数量
     */
    public long countByAccessType(String accessType) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("access_type", accessType);
        return count(wrapper);
    }

    /**
     * 分页查询准入记录
     */
    public List<AccessRecord> listPage(Long userId, Long projectId, String accessType, Date startDate, Date endDate, java.util.List<Long> managedOrgIds) {
        QueryWrapper<AccessRecord> wrapper = new QueryWrapper<>();
        // 显式选择数据库中的字段，排除扩展字段（userName, phone, idCardNo, projectName）
        wrapper.select(
            "id", "user_id", "project_id", "access_time", "exit_time", "access_type",
            "safety_code_color", "gate_id", "camera_snapshot", "location", "remarks", "created_at"
        );
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (projectId != null) {
            wrapper.eq("project_id", projectId);
        }
        if (accessType != null && !accessType.trim().isEmpty()) {
            wrapper.eq("access_type", accessType);
        }
        if (startDate != null) {
            wrapper.ge("access_time", startDate);
        }
        if (endDate != null) {
            wrapper.le("access_time", endDate);
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
        wrapper.orderByDesc("access_time");
        return list(wrapper);
    }
}
