package com.joyfishs.dawa.qualification.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.qualification.entity.Qualification;
import com.joyfishs.dawa.qualification.mapper.QualificationMapper;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.mapper.ProjectMapper;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.mapper.SysUserMapper;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;

/**
 * 资质证件 Service
 * <p>
 * 提供资质证件的增删改查、状态更新等功能
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-28
 */
@Service
public class QualificationService extends ServiceImpl<QualificationMapper, Qualification> {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ProjectMapper projectMapper;

    /**
     * 获取待审核的资质列表
     *
     * @return 待审核的资质列表
     */
    public List<Qualification> getPendingQualifications() {
        LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Qualification::getVerified, false)  // 未核验
               .eq(Qualification::getStatus, Qualification.STATUS_PENDING);  // 或者处于待审核状态
        return list(wrapper);
    }

    /**
     * 根据用户 ID 查询资质列表
     *
     * @param userId 用户 ID
     * @return 资质列表
     */
    public List<Qualification> listByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        
        LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Qualification::getUserId, userId);
        wrapper.orderByDesc(Qualification::getCreatedAt);
        return list(wrapper);
    }

    /**
     * 根据用户 ID 和状态查询资质列表
     *
     * @param userId 用户 ID
     * @param status 状态
     * @return 资质列表
     */
    public List<Qualification> listByUserIdAndStatus(Long userId, String status) {
        if (userId == null) {
            // 如果userId为null，则查找所有符合状态的记录
            LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
            if (status != null && !status.isEmpty()) {
                wrapper.eq(Qualification::getStatus, status);
            }
            wrapper.orderByDesc(Qualification::getCreatedAt);
            return list(wrapper);
        }
        
        LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Qualification::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Qualification::getStatus, status);
        }
        wrapper.orderByDesc(Qualification::getCreatedAt);
        return list(wrapper);
    }

    /**
     * 添加资质
     *
     * @param qualification 资质对象
     * @return 添加后的资质
     */
    @Transactional
    public Qualification addQualification(Qualification qualification) {
        if (qualification == null || qualification.getUserId() == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }

        if (StringUtils.isEmpty(qualification.getCertType())) {
            throw new IllegalArgumentException("证件类型不能为空");
        }

        if (StringUtils.isEmpty(qualification.getCertNo())) {
            throw new IllegalArgumentException("证件编号不能为空");
        }

        // 设置默认状态
        if (StringUtils.isEmpty(qualification.getStatus())) {
            qualification.setStatus(Qualification.STATUS_VALID);
        }

        // 设置默认核验状态
        if (qualification.getVerified() == null) {
            qualification.setVerified(false);
        }

        // 如果未设置创建时间，使用当前时间
        if (qualification.getCreatedAt() == null) {
            qualification.setCreatedAt(LocalDateTime.now());
        }

        save(qualification);
        return qualification;
    }

    /**
     * 更新资质
     *
     * @param qualification 资质对象
     * @return 更新后的资质
     */
    @Transactional
    public Qualification updateQualification(Qualification qualification) {
        if (qualification == null || qualification.getId() == null) {
            throw new IllegalArgumentException("资质 ID 不能为空");
        }

        // 检查是否存在
        Qualification existing = getById(qualification.getId());
        if (existing == null) {
            throw new IllegalArgumentException("资质不存在");
        }

        // 更新字段
        if (qualification.getCertType() != null) {
            existing.setCertType(qualification.getCertType());
        }
        if (qualification.getCertNo() != null) {
            existing.setCertNo(qualification.getCertNo());
        }
        if (qualification.getIssueDate() != null) {
            existing.setIssueDate(qualification.getIssueDate());
        }
        if (qualification.getExpiryDate() != null) {
            existing.setExpiryDate(qualification.getExpiryDate());
            // 根据到期日期自动更新状态
            updateStatusByExpiryDate(existing);
        }
        if (qualification.getIssuingAuthority() != null) {
            existing.setIssuingAuthority(qualification.getIssuingAuthority());
        }
        if (qualification.getCertPhotoUrl() != null) {
            existing.setCertPhotoUrl(qualification.getCertPhotoUrl());
        }
        if (qualification.getStatus() != null) {
            existing.setStatus(qualification.getStatus());
        }
        if (qualification.getVerified() != null) {
            existing.setVerified(qualification.getVerified());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        updateById(existing);
        return existing;
    }

    /**
     * 删除资质
     *
     * @param id 资质 ID
     * @return true-删除成功，false-删除失败
     */
    @Transactional
    public boolean deleteQualification(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("资质 ID 不能为空");
        }

        return removeById(id);
    }

    /**
     * 根据 ID 获取资质详情
     *
     * @param id 资质 ID
     * @return 资质对象
     */
    public Qualification getQualificationById(Long id) {
        if (id == null) {
            return null;
        }
        return getById(id);
    }

    /**
     * 核验资质
     *
     * @param id 资质 ID
     * @return 更新后的资质
     */
    @Transactional
    public Qualification verifyQualification(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("资质 ID 不能为空");
        }

        Qualification qualification = getById(id);
        if (qualification == null) {
            throw new IllegalArgumentException("资质不存在");
        }

        qualification.setVerified(true);
        qualification.setUpdatedAt(LocalDateTime.now());
        updateById(qualification);
        return qualification;
    }

    /**
     * 取消核验资质
     *
     * @param id 资质 ID
     * @return 更新后的资质
     */
    @Transactional
    public Qualification unverifyQualification(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("资质 ID 不能为空");
        }

        Qualification qualification = getById(id);
        if (qualification == null) {
            throw new IllegalArgumentException("资质不存在");
        }

        qualification.setVerified(false);
        qualification.setUpdatedAt(LocalDateTime.now());
        updateById(qualification);
        return qualification;
    }

    /**
     * 批量更新资质状态
     * <p>
     * 根据到期日期自动更新所有资质的状态
     * </p>
     */
    @Transactional
    public void batchUpdateStatus() {
        List<Qualification> allList = list();
        if (allList == null || allList.isEmpty()) {
            return;
        }

        LocalDate now = LocalDate.now();
        LocalDate threshold = now.plusDays(30);

        for (Qualification qual : allList) {
            if (qual.getExpiryDate() == null) {
                continue;
            }

            LocalDate expiryDate = qual.getExpiryDate();
            String oldStatus = qual.getStatus();
            String newStatus = oldStatus;

            if (expiryDate.isBefore(now)) {
                newStatus = Qualification.STATUS_EXPIRED;
            } else if (expiryDate.isBefore(threshold)) {
                newStatus = Qualification.STATUS_EXPIRING;
            } else {
                newStatus = Qualification.STATUS_VALID;
            }

            if (!oldStatus.equals(newStatus)) {
                qual.setStatus(newStatus);
                qual.setUpdatedAt(LocalDateTime.now());
                updateById(qual);
            }
        }
    }

    /**
     * 根据到期日期更新状态
     *
     * @param qualification 资质对象
     */
    private void updateStatusByExpiryDate(Qualification qualification) {
        if (qualification.getExpiryDate() == null) {
            return;
        }

        LocalDate now = LocalDate.now();
        LocalDate expiryDate = qualification.getExpiryDate();
        LocalDate threshold = now.plusDays(30);

        if (expiryDate.isBefore(now)) {
            qualification.setStatus(Qualification.STATUS_EXPIRED);
        } else if (expiryDate.isBefore(threshold)) {
            qualification.setStatus(Qualification.STATUS_EXPIRING);
        } else {
            qualification.setStatus(Qualification.STATUS_VALID);
        }
    }

    /**
     * 批量删除资质
     *
     * @param ids 资质ID列表
     * @return true-删除成功，false-删除失败
     */
    @Transactional
    public boolean batchDeleteQualifications(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        
        return removeBatchByIds(ids);
    }

    /**
     * 批量审核资质
     *
     * @param ids 资质ID列表
     * @return true-审核成功，false-审核失败
     */
    @Transactional
    public boolean batchAuditQualifications(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        List<Qualification> qualifications = listByIds(ids);
        if (qualifications == null || qualifications.isEmpty()) {
            return false;
        }

        for (Qualification qualification : qualifications) {
            qualification.setVerified(true);
            qualification.setUpdatedAt(LocalDateTime.now());
        }

        return updateBatchById(qualifications);
    }

    /**
     * 批量导入资质数据
     *
     * @param qualifications 资质列表
     * @return 成功导入的资质列表
     */
    @Transactional
    public List<Qualification> importQualifications(List<Qualification> qualifications) {
        if (qualifications == null || qualifications.isEmpty()) {
            return new ArrayList<>();
        }

        List<Qualification> results = new ArrayList<>();
        for (Qualification qualification : qualifications) {
            // 检查是否已存在相同证件编号的资质
            Qualification existing = getQualificationByCertNo(qualification.getCertNo());
            if (existing == null) {
                // 如果不存在，则新增
                qualification.setCreatedAt(LocalDateTime.now());
                qualification.setVerified(qualification.getVerified() != null ? qualification.getVerified() : false);
                qualification.setStatus(qualification.getStatus() != null ? qualification.getStatus() : Qualification.STATUS_VALID);
                save(qualification);
                results.add(qualification);
            } else {
                // 如果已存在，可以选择更新或者跳过，这里我们选择更新
                existing.setCertType(qualification.getCertType());
                existing.setIssueDate(qualification.getIssueDate());
                existing.setExpiryDate(qualification.getExpiryDate());
                existing.setIssuingAuthority(qualification.getIssuingAuthority());
                existing.setCertPhotoUrl(qualification.getCertPhotoUrl());
                existing.setUpdatedAt(LocalDateTime.now());
                updateById(existing);
                results.add(existing);
            }
        }

        return results;
    }

    /**
     * 根据证件编号查询资质
     *
     * @param certNo 证件编号
     * @return 资质对象
     */
    public Qualification getQualificationByCertNo(String certNo) {
        if (certNo == null) {
            return null;
        }

        LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Qualification::getCertNo, certNo);
        return getOne(wrapper);
    }

    /**
     * 查询即将过期的资质
     *
     * @return 资质列表
     */
    public List<Qualification> listExpiringSoon() {
        return baseMapper.selectExpiringSoon();
    }
    
    /**
     * 获取指定组织即将到期的资质证件
     *
     * @param orgId 组织ID
     * @param days 天数阈值
     * @return 即将到期的资质证件列表
     */
    public List<Qualification> getExpiringQualifications(Long orgId, int days) {
        return baseMapper.selectExpiringByOrgId(orgId, days);
    }

    /**
     * 统计全部资质数量
     */
    public long countAll() {
        return count();
    }

    /**
     * 根据状态统计资质数量
     */
    public long countByStatus(String status) {
        LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Qualification::getStatus, status);
        }
        return count(wrapper);
    }

    /**
     * 统计已核验的资质数量
     */
    public long countVerified() {
        LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Qualification::getVerified, true);
        return count(wrapper);
    }

    /**
     * 统计有效资质数量（状态为 valid 且未过期）
     */
    public long countValidQualifications(Long orgId) {
        LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Qualification::getStatus, "valid");
        wrapper.gt(Qualification::getExpiryDate, java.time.LocalDate.now());
        // orgId 不是 qualifications 表的字段，需要通过 project_id 关联查询
        if (orgId != null) {
            wrapper.apply("(SELECT p.org_id FROM xm_project p WHERE p.id = project_id) = {0}", orgId);
        }
        return count(wrapper);
    }

    /**
     * 根据证件类型统计数量
     */
    public long countByCertType(String certType) {
        LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Qualification::getCertType, certType);
        return count(wrapper);
    }

    /**
     * 分页查询资质记录
     */
    public List<Qualification> listPage(Long userId, String status, String certType, Boolean verified, java.util.List<Long> managedOrgIds) {
        LambdaQueryWrapper<Qualification> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Qualification::getUserId, userId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Qualification::getStatus, status);
        }
        if (certType != null && !certType.isEmpty()) {
            wrapper.eq(Qualification::getCertType, certType);
        }
        if (verified != null) {
            wrapper.eq(Qualification::getVerified, verified);
        }
        // 如果有权限管理的机构 ID 列表，过滤出这些机构管辖的项目
        if (managedOrgIds != null && !managedOrgIds.isEmpty()) {
            // 先查询这些机构下的项目ID列表
            QueryWrapper<Project> projectWrapper = new QueryWrapper<>();
            projectWrapper.select("id").in("org_id", managedOrgIds);
            List<Project> projectList = projectMapper.selectList(projectWrapper);
            if (projectList != null && !projectList.isEmpty()) {
                List<Long> projectIds = projectList.stream().map(Project::getId).collect(Collectors.toList());
                wrapper.in(Qualification::getProjectId, projectIds);
            } else {
                // 如果没有项目，返回空结果
                wrapper.eq(Qualification::getId, -1L);
            }
        }
        wrapper.orderByDesc(Qualification::getCreatedAt);
        return list(wrapper);
    }

    /**
     * 分页查询资质记录（带用户信息）
     */
    public List<Qualification> listPageWithUser(Map<String, Object> params) {
        // 获取当前登录用户有权限管理的机构 ID 列表
        java.util.List<Long> managedOrgIds = SecurityUtil.getManagedOrgIds();

        List<Qualification> list = listPage(
            params.containsKey("userId") ? (Long) params.get("userId") : null,
            params.containsKey("status") ? (String) params.get("status") : null,
            params.containsKey("certType") ? (String) params.get("certType") : null,
            params.containsKey("verified") ? (Boolean) params.get("verified") : null,
            managedOrgIds
        );

        // 关联查询用户信息和项目信息
        for (Qualification qualification : list) {
            if (qualification.getUserId() != null) {
                SysUser user = sysUserMapper.selectById(qualification.getUserId());
                if (user != null) {
                    qualification.setHolderName(user.getName());
                    qualification.setPhone(user.getPhone());
                    qualification.setIdCardNo(user.getIdCardNo());
                }
            }
            if (qualification.getProjectId() != null) {
                Project project = projectMapper.selectById(qualification.getProjectId());
                if (project != null) {
                    qualification.setProjectName(project.getProjectName());
                }
            }
        }

        // 过滤搜索条件（用户姓名、手机号）
        return list.stream().filter(qualification -> {
            if (params.containsKey("userName")) {
                String userName = (String) params.get("userName");
                if (qualification.getHolderName() == null || !qualification.getHolderName().contains(userName)) {
                    return false;
                }
            }
            if (params.containsKey("phone")) {
                String phone = (String) params.get("phone");
                if (qualification.getPhone() == null || !qualification.getPhone().contains(phone)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }
}
