package com.joyfishs.dawa.safetycode.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.mapper.ProjectMapper;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.safetycode.entity.SafetyCode;
import com.joyfishs.dawa.safetycode.mapper.SafetyCodeMapper;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.mapper.SysUserMapper;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 安全码 Service
 * <p>
 * 提供安全码的生成、更新、验证等功能
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-28
 */
@Slf4j
@Service
public class SafetyCodeService extends ServiceImpl<SafetyCodeMapper, SafetyCode> {

    /**
     * 默认有效期天数
     */
    private static final int DEFAULT_VALID_DAYS = 365;

    private final SafetyCodeEvaluator safetyCodeEvaluator;
    private final SysUserMapper sysUserMapper;
    private final ProjectMapper projectMapper;
    private final PersonService personService;

    public SafetyCodeService(
            SafetyCodeEvaluator safetyCodeEvaluator,
            SysUserMapper sysUserMapper,
            ProjectMapper projectMapper,
            PersonService personService) {
        this.safetyCodeEvaluator = safetyCodeEvaluator;
        this.sysUserMapper = sysUserMapper;
        this.projectMapper = projectMapper;
        this.personService = personService;
    }


    /**
     * 生成安全码
     * <p>
     * 为用户生成唯一的安全码,包含颜色标识和有效期
     * </p>
     *
     * @param userId 用户 ID
     * @return 安全码对象
     * @deprecated 使用 {@link #generateSafetyCode(Long, Long)} 代替以支持项目部关联
     */
    @Deprecated
    @Transactional
    public SafetyCode generateSafetyCode(Long userId) {
        return generateSafetyCode(userId, null);
    }

    /**
     * 生成安全码（带项目部关联）
     * <p>
     * 为用户生成唯一的安全码,包含颜色标识和有效期,并自动关联项目部和组织
     * </p>
     *
     * @param userId 用户 ID
     * @param projectId 项目部 ID（可为 null，用于向后兼容）
     * @return 安全码对象
     */
    @Transactional
    public SafetyCode generateSafetyCode(Long userId, Long projectId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }

        // 检查是否已存在安全码（按用户和项目查询）
        SafetyCode existingCode = getByUserIdAndProject(userId, projectId);
        if (existingCode != null && existingCode.getId() != null) {
            // 如果已存在,更新而非新建
            return updateSafetyCode(existingCode, projectId);
        }

        // 创建新的安全码
        SafetyCode safetyCode = new SafetyCode();
        safetyCode.setUserId(userId);

        // 设置项目部 ID
        safetyCode.setProjectId(projectId);

        // 自动设置组织 ID（从项目获取）
        if (projectId != null) {
            Project project = projectMapper.selectById(projectId);
            if (project != null) {
                safetyCode.setOrgId(project.getOrgId());
            }
        }

        // 生成唯一安全码值(使用 UUID 简化,实际可加密)
        String codeValue = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        safetyCode.setCode(codeValue);

        // 通过评估器确定初始颜色（带项目范围）
        Person person = personService.getByUserId(userId);
        Long personId = (person != null) ? person.getId() : userId;
        String initialColor = safetyCodeEvaluator.evaluateColorWithProjectScope(personId, projectId);
        safetyCode.setColor(initialColor);
        safetyCode.setStatus(SafetyCode.STATUS_ACTIVE);

        // 设置有效期(默认 365 天)
        LocalDateTime now = LocalDateTime.now();
        safetyCode.setValidFrom(now);
        safetyCode.setValidTo(now.plusDays(DEFAULT_VALID_DAYS));

        // 生成二维码数据 (使用安全码值作为二维码内容)
        try {
            cn.hutool.extra.qrcode.QrConfig config = new cn.hutool.extra.qrcode.QrConfig(200, 200);
            String base64QrCode = cn.hutool.extra.qrcode.QrCodeUtil.generateAsBase64(codeValue, config, "png");
            safetyCode.setQrCodeData("data:image/png;base64," + base64QrCode);
            // 同时设置 qrCode 字段用于前端展示
            safetyCode.setQrCode("data:image/png;base64," + base64QrCode);
        } catch (Exception e) {
            log.warn("Failed to generate QR code for userId: {}", userId, e);
        }

        // 保存并返回
        save(safetyCode);
        return safetyCode;
    }

    /**
     * 刷新安全码
     * <p>
     * 为用户重新生成新的安全码值,保持颜色和有效期不变或重新计算
     * </p>
     *
     * @param userId 用户 ID
     * @return 更新后的安全码
     * @deprecated 使用 {@link #refreshSafetyCode(Long, Long)} 代替以支持项目部关联
     */
    @Deprecated
    @Transactional
    public SafetyCode refreshSafetyCode(Long userId) {
        return refreshSafetyCode(userId, null);
    }

    /**
     * 刷新安全码（带项目部关联）
     * <p>
     * 为用户重新生成新的安全码值,保持颜色和有效期不变或重新计算
     * </p>
     *
     * @param userId 用户 ID
     * @param projectId 项目部 ID（用于颜色评估）
     * @return 更新后的安全码
     */
    @Transactional
    public SafetyCode refreshSafetyCode(Long userId, Long projectId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }

        SafetyCode safetyCode = getByUserIdAndProject(userId, projectId);
        if (safetyCode == null) {
            // 如果不存在安全码,先生成一个
            safetyCode = generateSafetyCode(userId, projectId);
        } else {
            // 重新计算颜色（带项目范围）
            String newColor = safetyCodeEvaluator.evaluateColorWithProjectScope(userId, projectId);

            // 更新现有的安全码
            // 重新生成安全码值
            String codeValue = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            safetyCode.setCode(codeValue);

            // 设置新颜色
            safetyCode.setColor(newColor);

            // 重置有效期
            LocalDateTime now = LocalDateTime.now();
            safetyCode.setValidFrom(now);
            safetyCode.setValidTo(now.plusDays(DEFAULT_VALID_DAYS)); // 有效期仍为一年

            // 保持状态为激活
            safetyCode.setStatus(SafetyCode.STATUS_ACTIVE);

            updateById(safetyCode);
        }

        return safetyCode;
    }

    /**
     * 更新安全码颜色
     * <p>
     * 根据用户 ID 更新安全码颜色
     * </p>
     *
     * @param userId 用户 ID
     * @param color 颜色标识:green/yellow/red
     * @return 更新后的安全码
     */
    @Transactional
    public SafetyCode updateColor(Long userId, String color) {
        if (userId == null || StringUtils.isEmpty(color)) {
            throw new IllegalArgumentException("用户 ID 和颜色不能为空");
        }

        // 验证颜色值
        if (!SafetyCode.COLOR_GREEN.equals(color) &&
            !SafetyCode.COLOR_YELLOW.equals(color) &&
            !SafetyCode.COLOR_RED.equals(color)) {
            throw new IllegalArgumentException("无效的颜色值:" + color);
        }

        SafetyCode safetyCode = getByUserId(userId);
        if (safetyCode == null) {
            // 如果不存在,先创建
            safetyCode = generateSafetyCode(userId);
        }

        safetyCode.setColor(color);
        updateById(safetyCode);

        return safetyCode;
    }

    /**
     * 获取用户安全码状态
     * <p>
     * 根据用户 ID 获取安全码
     * </p>
     *
     * @param userId 用户 ID
     * @return 安全码对象,不存在则返回 null
     */
    @Transactional(readOnly = true)
    public SafetyCode getStatus(Long userId) {
        if (userId == null) {
            return null;
        }
        return getByUserId(userId);
    }

    /**
     * 验证安全码有效性
     * <p>
     * 检查安全码是否在有效期内且状态正常，如果过期会更新状态
     * </p>
     *
     * @param userId 用户 ID
     * @return true-有效,false-无效
     */
    @Transactional
    public boolean validate(Long userId) {
        if (userId == null) {
            return false;
        }

        SafetyCode safetyCode = getByUserId(userId);
        if (safetyCode == null) {
            return false;
        }

        // 检查状态
        if (!SafetyCode.STATUS_ACTIVE.equals(safetyCode.getStatus())) {
            return false;
        }

        return validateExpiredStatus(safetyCode);
    }

    /**
     * 验证并更新过期状态
     * <p>
     * 如果安全码已过期，更新其状态为过期
     * </p>
     *
     * @param safetyCode 安全码对象
     * @return true-仍在有效期内,false-已过期
     */
    private boolean validateExpiredStatus(SafetyCode safetyCode) {
        LocalDateTime now = LocalDateTime.now();
        if (safetyCode.getValidFrom() != null && now.isBefore(safetyCode.getValidFrom())) {
            return false;
        }
        if (safetyCode.getValidTo() != null && now.isAfter(safetyCode.getValidTo())) {
            // 过期,更新状态
            safetyCode.setStatus(SafetyCode.STATUS_EXPIRED);
            updateById(safetyCode);
            return false;
        }
        return true;
    }

    /**
     * 根据用户 ID 重新评估并更新安全码颜色
     * <p>
     * 调用评估器根据最新数据重新计算颜色
     * </p>
     *
     * @param userId 用户 ID
     * @param projectId 项目部 ID（用于范围评估）
     * @param violationScore 违章扣分
     * @param safetyTrainingPassed 安全培训是否合格
     * @return 更新后的安全码
     */
    @Transactional
    public SafetyCode reevaluateColor(Long userId, Long projectId, Integer violationScore, Boolean safetyTrainingPassed) {
        // 获取人员信息
        Person person = personService.getByUserId(userId);
        Long personId = (person != null) ? person.getId() : userId;
        
        // 调用带项目范围评估的接口（新接口会自动计算分值，无须传入 violationScore）
        String newColor = safetyCodeEvaluator.evaluateColorWithProjectScope(personId, projectId);
        return updateColor(userId, newColor);
    }

    /**
     * 根据用户 ID 查询安全码
     *
     * @param userId 用户 ID
     * @return 安全码对象
     * @deprecated 使用 {@link #getByUserIdAndProject(Long, Long)} 代替
     */
    @Deprecated
    private SafetyCode getByUserId(Long userId) {
        return getByUserIdAndProject(userId, null);
    }

    /**
     * 根据用户 ID 和项目部查询安全码
     *
     * @param userId 用户 ID
     * @param projectId 项目部 ID（可为 null）
     * @return 安全码对象
     */
    public SafetyCode getByUserIdAndProject(Long userId, Long projectId) {
        LambdaQueryWrapper<SafetyCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SafetyCode::getUserId, userId);
        if (projectId != null) {
            wrapper.eq(SafetyCode::getProjectId, projectId);
        }
        wrapper.orderByDesc(SafetyCode::getCreateTime);
        wrapper.last("LIMIT 1");
        return getOne(wrapper);
    }

    /**
     * 更新现有安全码
     *
     * @param safetyCode 安全码对象
     * @param projectId 项目部 ID（用于颜色评估，可为 null）
     * @return 更新后的安全码
     */
    private SafetyCode updateSafetyCode(SafetyCode safetyCode, Long projectId) {
        // 重新计算颜色（带项目范围）
        String newColor = safetyCodeEvaluator.evaluateColorWithProjectScope(safetyCode.getUserId(), projectId);

        // 重新生成安全码值
        String codeValue = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        safetyCode.setCode(codeValue);

        // 设置新颜色
        safetyCode.setColor(newColor);

        // 重置有效期
        LocalDateTime now = LocalDateTime.now();
        safetyCode.setValidFrom(now);
        safetyCode.setValidTo(now.plusDays(DEFAULT_VALID_DAYS));

        // 重置状态为激活
        safetyCode.setStatus(SafetyCode.STATUS_ACTIVE);

        updateById(safetyCode);
        return safetyCode;
    }

    /**
     * 暂停安全码
     *
     * @param userId 用户 ID
     * @return 更新后的安全码
     */
    @Transactional
    public SafetyCode suspend(Long userId) {
        SafetyCode safetyCode = getByUserId(userId);
        if (safetyCode != null) {
            safetyCode.setStatus(SafetyCode.STATUS_SUSPENDED);
            updateById(safetyCode);
        }
        return safetyCode;
    }

    /**
     * 恢复安全码
     *
     * @param userId 用户 ID
     * @return 更新后的安全码
     */
    @Transactional
    public SafetyCode restore(Long userId) {
        SafetyCode safetyCode = getByUserId(userId);
        if (safetyCode != null) {
            safetyCode.setStatus(SafetyCode.STATUS_ACTIVE);
            updateById(safetyCode);
        }
        return safetyCode;
    }
    
    /**
     * 查询所有安全码列表（按权限过滤）
     */
    @Transactional(readOnly = true)
    public List<SafetyCode> listAll() {
        // 获取当前登录用户的 orgId，用于过滤管辖的项目
        Long currentOrgId = SecurityUtil.getOrgId();
        boolean isAdmin = SecurityUtil.isAdmin();

        LambdaQueryWrapper<SafetyCode> wrapper = new LambdaQueryWrapper<>();
        // 如果不是超级管理员，需要过滤出该 orgId 管辖的项目
        if (!isAdmin && currentOrgId != null) {
            wrapper.apply("project_id IS NOT NULL AND (SELECT p.org_id FROM xm_project p WHERE p.id = project_id) = {0}", currentOrgId);
        }
        wrapper.orderByDesc(SafetyCode::getCreateTime);
        List<SafetyCode> list = baseMapper.selectList(wrapper);

        // 批量关联查询用户信息和项目信息，避免 N+1 查询问题
        if (!list.isEmpty()) {
            // 收集所有需要查询的用户ID
            List<Long> userIds = list.stream()
                    .map(SafetyCode::getUserId)
                    .filter(userId -> userId != null)
                    .distinct()
                    .collect(Collectors.toList());

            // 收集所有需要查询的项目ID
            List<Long> projectIds = list.stream()
                    .map(SafetyCode::getProjectId)
                    .filter(projectId -> projectId != null)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询用户
            Map<Long, SysUser> userMap = new java.util.HashMap<>();
            if (!userIds.isEmpty()) {
                List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
                userMap = users.stream()
                        .collect(Collectors.toMap(SysUser::getId, user -> user));
            }

            // 批量查询项目
            Map<Long, Project> projectMap = new java.util.HashMap<>();
            if (!projectIds.isEmpty()) {
                List<Project> projects = projectMapper.selectBatchIds(projectIds);
                projectMap = projects.stream()
                        .collect(Collectors.toMap(Project::getId, project -> project));
            }

            // 填充关联数据
            for (SafetyCode code : list) {
                if (code.getUserId() != null) {
                    SysUser user = userMap.get(code.getUserId());
                    if (user != null) {
                        code.setUserName(user.getName());
                        code.setPhone(user.getPhone());
                        code.setIdCardNo(user.getIdCardNo());
                    }
                }
                if (code.getProjectId() != null) {
                    Project project = projectMap.get(code.getProjectId());
                    if (project != null) {
                        code.setProjectName(project.getProjectName());
                    }
                }
            }
        }

        return list;
    }

    /**
     * 带搜索条件的安全码列表
     */
    @Transactional(readOnly = true)
    public List<SafetyCode> listWithUser(Map<String, Object> params) {
        List<SafetyCode> list = listAll();

        // 过滤搜索条件
        return list.stream().filter(code -> {
            if (params.containsKey("userName")) {
                String userName = (String) params.get("userName");
                if (code.getUserName() == null || !code.getUserName().contains(userName)) {
                    return false;
                }
            }
            if (params.containsKey("phone")) {
                String phone = (String) params.get("phone");
                if (code.getPhone() == null || !code.getPhone().contains(phone)) {
                    return false;
                }
            }
            if (params.containsKey("status")) {
                String status = (String) params.get("status");
                if (!status.equals(code.getStatus())) {
                    return false;
                }
            }
            return true;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计指定组织今天新增的安全码数量
     */
    @Transactional(readOnly = true)
    public long countTodayNewCodes(Long orgId) {
        if (orgId == null) {
            // 当 orgId 为 null 时，统计所有组织
            return count();
        }
        return baseMapper.countTodayNewCodesByOrg(orgId);
    }

    /**
     * 统计指定组织有效安全码数量
     */
    @Transactional(readOnly = true)
    public long countValidCodes(Long orgId) {
        if (orgId == null) {
            // 当 orgId 为 null 时，统计所有有效安全码
            LambdaQueryWrapper<SafetyCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SafetyCode::getStatus, SafetyCode.STATUS_ACTIVE);
            wrapper.ge(SafetyCode::getValidTo, java.time.LocalDateTime.now());
            return count(wrapper);
        }
        return baseMapper.countValidCodesByOrg(orgId);
    }

    /**
     * 统计指定组织过期安全码数量
     */
    @Transactional(readOnly = true)
    public long countExpiredCodes(Long orgId) {
        if (orgId == null) {
            // 当 orgId 为 null 时，统计所有过期安全码
            LambdaQueryWrapper<SafetyCode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SafetyCode::getStatus, SafetyCode.STATUS_EXPIRED);
            return count(wrapper);
        }
        return baseMapper.countExpiredCodesByOrg(orgId);
    }

    /**
     * 核验安全码
     * <p>
     * 根据安全码字符串进行核验
     * </p>
     *
     * @param code 安全码字符串
     * @return 安全码对象，如果无效则返回null
     */
    @Transactional
    public SafetyCode verifySafetyCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        
        LambdaQueryWrapper<SafetyCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SafetyCode::getCode, code);
        wrapper.orderByDesc(SafetyCode::getCreateTime);
        wrapper.last("LIMIT 1");
        
        SafetyCode safetyCode = getOne(wrapper);
        
        // 检查安全码是否有效
        if (safetyCode != null) {
            LocalDateTime now = LocalDateTime.now();
            
            // 检查有效期
            if ((safetyCode.getValidFrom() != null && now.isBefore(safetyCode.getValidFrom())) ||
                (safetyCode.getValidTo() != null && now.isAfter(safetyCode.getValidTo()))) {
                
                // 过期，更新状态
                safetyCode.setStatus(SafetyCode.STATUS_EXPIRED);
                updateById(safetyCode);
                return null;
            }
            
            // 检查状态
            if (!SafetyCode.STATUS_ACTIVE.equals(safetyCode.getStatus())) {
                return null;
            }
        }
        
        return safetyCode;
    }

    /**
     * 根据颜色统计安全码数量
     */
    @Transactional(readOnly = true)
    public long countByColor(String color) {
        LambdaQueryWrapper<SafetyCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SafetyCode::getColor, color);
        return count(wrapper);
    }
}
