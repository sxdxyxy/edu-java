package com.joyfishs.system.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.person.domain.param.FacePhotoRequest;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.student.service.ProjectPersonStudyRecordService;
import com.joyfishs.system.config.async.AsyncFactory;
import com.joyfishs.system.config.async.AsyncManager;
import com.joyfishs.system.config.security.captcha.CaptchaAuthenticationToken;
import com.joyfishs.system.config.security.face.FaceAuthenticationToken;
import com.joyfishs.system.config.security.weixin.OpenPlatformAuthenticationToken;
import com.joyfishs.system.config.security.Md5PasswordEncoder;
import com.joyfishs.system.config.weixin.WxMaProperties;
import com.joyfishs.system.config.weixin.WxMpConfiguration;
import com.joyfishs.system.config.weixin.WxMpProperties;
import com.joyfishs.system.domain.LoginRes;
import com.joyfishs.system.entity.*;
import com.joyfishs.system.entity.vo.PersonVo;
import com.joyfishs.system.enums.DeviceType;
import com.joyfishs.system.enums.RoleType;
import com.joyfishs.system.pojo.LoginMenuTreeNode;
import com.joyfishs.tencent.service.FaceAiService;
import com.joyfishs.utils.Constants;
import com.joyfishs.utils.MessageUtil;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * 登录校验方法
 **/
@Slf4j
@Service
public class SysLoginService {
    @Autowired
    private TokenService tokenService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private PersonService personService;
    @Autowired
    private SysOrgService sysOrgService;
    @Autowired
    private ProjectPersonStudyRecordService studyRecordService;
    @Autowired
    private WxMaProperties wxMaProperties;
    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private WxMpProperties wxMpProperties;
    @Autowired
    private FaceAiService faceAiService;

    /**
     * 账号密码登录
     *
     * @param loginBody 登陆用户实体
     * @return 结果
     */
    public LoginRes login(UsernamePasswordLoginBody loginBody, RoleType roleType, boolean isCheckState) {
        Authentication authentication = null;
        LoginRes result = new LoginRes();
        try {
            // 该方法会去调用CustomUserService.loadUserByUsername
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginBody.getUsername(), loginBody.getPassword()));
            
            // 登录成功后，尝试升级密码到 BCrypt
            upgradePasswordIfNecessary(authentication, loginBody.getPassword());
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLoginInfo(loginBody.getUsername(), Constants.LOGIN_FAIL, MessageUtil.message("user.password.not.match")));
                return result.setError("用户名密码不匹配", 401);
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLoginInfo(loginBody.getUsername(), Constants.LOGIN_FAIL, e.getMessage()));
                return result.setError(e.getMessage());
            }
        } finally {
            Md5PasswordEncoder.getInstance().clearSalt();
        }
        return login(authentication, roleType, isCheckState);
    }

    /**
     * 自动识别角色登录:优先以管理员身份登录,若该用户没有管理员角色,再回退到学员身份。
     * 密码错或用户不存在时直接返回失败(两种身份都不通过);管理员身份被停用/无人员信息时回退到学员身份。
     *
     * @param loginBody 登录请求体
     * @param isCheckState 是否校验账号状态
     * @return 登录结果,LoginRes.roleCode 表示实际登录的角色(student / platform_manager)
     */
    public LoginRes autoLogin(UsernamePasswordLoginBody loginBody, boolean isCheckState) {
        LoginRes managerRes = tryLoginWithRole(loginBody, RoleType.MANAGER, isCheckState);
        if (managerRes.getCode() == 200) {
            managerRes.setRoleCode(RoleType.MANAGER.getValue());
            return managerRes;
        }
        // 仅在"该用户没有该角色"时回退到学员;其他错误(用户名/密码错、账号停用等)直接透传
        if (!"登录用户没有该角色".equals(managerRes.getErrorMsg())) {
            return managerRes;
        }
        LoginRes studentRes = tryLoginWithRole(loginBody, RoleType.STUDENT, isCheckState);
        if (studentRes.getCode() == 200) {
            studentRes.setRoleCode(RoleType.STUDENT.getValue());
        }
        return studentRes;
    }

    protected LoginRes tryLoginWithRole(UsernamePasswordLoginBody loginBody, RoleType roleType, boolean isCheckState) {
        return login(loginBody, roleType, isCheckState);
    }

    /**
     * 登录验证
     *
     * @return 结果
     */
    public LoginRes login(Authentication authentication, RoleType roleType, boolean isCheckState) {
        // P0-5 空指针修复：添加完整的空值检查
        if (authentication == null) {
            log.error("login - authentication is null");
            throw new CustomException("认证信息为空");
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof LoginUser)) {
            log.error("login - principal is not LoginUser, type={}", 
                principal != null ? principal.getClass().getName() : "null");
            throw new CustomException("认证主体类型错误");
        }
        
        LoginUser loginUser = (LoginUser) principal;
        
        SysUser user = loginUser.getUser();
        if (user == null) {
            log.error("login - loginUser.getUser() is null");
            throw new CustomException("用户信息为空");
        }
        
        // 安全访问用户属性
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLoginDate(LocalDateTime.now());
        sysUserService.updateById(user);
        AsyncManager.me().execute(AsyncFactory.recordLoginInfo(loginUser.getUsername(), Constants.LOGIN_SUCCESS, "登录成功"));
        LoginRes result = new LoginRes();
        // 登录时校验是否存在角色
        if (user.getId() == null) {
            log.error("login - user.getId() is null");
            throw new CustomException("用户 ID 为空");
        }
        SysUserRole sysUserRole = sysUserRoleService.getByUserIdAndRoleCode(user.getId(), roleType.getValue());
        if (ObjectUtil.isEmpty(sysUserRole)) {
            return result.setError("登录用户没有该角色");
        }

        Integer status = user.getStatus();
        // 登录时是否校验账号状态
        if (isCheckState && status != 1) {
            String msg = status == 2 ? "审核中，请稍后再试" : "已停用";
            return result.setError("您的账号" + msg);
        }

        loginUser.setCurrentRoleCode(roleType.getValue());

        // 2022-02-24 修改 所有用户都有人员信息 登录时需要查询人员信息及组织
        Person person = personService.getByUserId(user.getId());
        if (person == null) {
            return result.setError("未查询到人员信息",404);
        }
        if (person.getState() == 0) {
            return result.setError("您已离职，无法登录");

        }
        PersonVo personVo = new PersonVo();
        BeanUtil.copyProperties(person, personVo);
        loginUser.setPerson(personVo);

        result.setIdentity(person.getVerified());
        
        // 初始化角色和权限信息，确保 Token 立即生效
        initLoginUserRolesAndPermissions(loginUser);
        
        // 生成token
        result.setToken(tokenService.createToken(loginUser));
        return result;
    }

    /**
     * 初始化登录用户的角色和权限信息
     */
    private void initLoginUserRolesAndPermissions(LoginUser loginUser) {
        // 获取当前登录账号的所有角色信息
        List<SysRole> sysRoleList = null;
        if (!loginUser.ifAdmin()) {
            sysRoleList = sysUserRoleService.findRoleListByUserId(loginUser.getUser().getId());
        } else {
            sysRoleList = sysRoleService.findAll();
        }
        loginUser.setRoleList(sysRoleList);

        // 获取当前登录账号的当前角色信息
        SysRole sysRole = null;
        for (SysRole role : sysRoleList) {
            if (loginUser.getCurrentRoleCode().equals(role.getCode())) {
                sysRole = role;
            }
        }
        if (sysRole == null) {
            // 如果没有匹配到角色，默认取第一个（如果有的话）
            if (!sysRoleList.isEmpty()) {
                sysRole = sysRoleList.get(0);
                loginUser.setCurrentRoleCode(sysRole.getCode());
            } else {
                throw new CustomException("当前用户没有分配角色");
            }
        }
        loginUser.setCurrentRole(sysRole);

        // 获取当前登录账号当前角色的菜单
        List<SysMenu> sysMenuList = null;
        if (!loginUser.ifAdmin()) {
            sysMenuList = sysRoleMenuService.findMenuListByRoleId(sysRole.getId());
        } else {
            sysMenuList = sysMenuService.findAll();
        }
        List<LoginMenuTreeNode> loginMenuTreeNodeList = sysMenuService.convertSysMenuToLoginMenu(sysMenuList);
        loginUser.setMenus(loginMenuTreeNodeList);

        // 获取当前登录账号当前角色的权限
        Set<String> permissions = new HashSet<>();
        if (loginUser.ifAdmin()) {
            permissions.add(PermissionService.ALL_PERMISSION);
        } else {
            for (SysMenu sysMenu : sysMenuList) {
                if (StringUtils.isNotEmpty(sysMenu.getPermission())) permissions.add(sysMenu.getPermission());
            }
        }
        loginUser.setPermissions(permissions);
    }

    /**
     * 手机短信验证码登录
     *
     * @param loginBody 登陆实体
     * @return 结果
     */
    public LoginRes captchaLogin(CaptchaLoginBody loginBody, boolean isCheckState) {
        CaptchaAuthenticationToken authenticationToken = new CaptchaAuthenticationToken(loginBody.getPhone(), loginBody.getCaptcha());
        Authentication authenticate = null;
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            AsyncManager.me().execute(AsyncFactory.recordLoginInfo(loginBody.getPhone(), Constants.LOGIN_FAIL, e.getMessage()));
            LoginRes result = new LoginRes();
            return result.setError(e.getMessage());
        }
        return login(authenticate, RoleType.STUDENT, isCheckState);
    }

    /**
     * 刷脸登录
     *
     * @param loginBody 登陆实体
     * @return 结果
     */
    public LoginRes faceLogin(FacePhotoRequest loginBody, DeviceType type, boolean isCheckState) {
        Authentication authenticate = null;
        LoginRes result = new LoginRes();
        try {
            Long personId = faceAiService.searchPersons(loginBody.getFacePhotoUrl());
            if (ObjUtil.isNull(personId)) {
                throw new UsernameNotFoundException("没有检索到匹配的人脸");
            }
            FaceAuthenticationToken authenticationToken = new FaceAuthenticationToken(personId);
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            AsyncManager.me().execute(AsyncFactory.recordLoginInfo("刷脸登录", Constants.LOGIN_FAIL, e.getMessage()));
            return result.setError(e.getMessage());
        }
        return login(authenticate, RoleType.STUDENT, isCheckState);
    }

    /**
     * 微信开放平台登录
     *
     * @param loginBody 登陆实体
     * @return 结果
     */
    public LoginRes weiXinLogin(WeiXinLoginBody loginBody, DeviceType type, boolean isCheckState) {
        Authentication authenticate = null;
        LoginRes result = new LoginRes();
        try {
            OpenPlatformAuthenticationToken authenticationToken = null;
            if (DeviceType.MINI_PROGRAM.equals(type)) {
                WxMaProperties.Config config = wxMaProperties.getConfigs().get(0);
                if (!wxMaService.switchover(config.getAppid())) {
                    throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", config.getAppid()));
                }
                WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(loginBody.getCode());
                log.info("开放平台登录SessionKey={}，Openid={}，Unionid={}", session.getSessionKey(), session.getOpenid(), session.getUnionid());
                result.setOpenid(session.getOpenid());
                result.setUnionid(session.getUnionid());
                authenticationToken = new OpenPlatformAuthenticationToken(session.getUnionid());
            } else {
                WxMpProperties.MpConfig config = wxMpProperties.getByPlatform(type);
                WxMpService mpService = WxMpConfiguration.getMpServices(config.getAppId());
                WxOAuth2AccessToken accessToken = mpService.getOAuth2Service().getAccessToken(loginBody.getCode());
                log.info("开放平台登录DeviceType={}，Openid={}，Unionid={}", type, accessToken.getOpenId(), accessToken.getUnionId());
                result.setOpenid(accessToken.getOpenId());
                result.setUnionid(accessToken.getUnionId());
                authenticationToken = new OpenPlatformAuthenticationToken(accessToken.getUnionId());
            }
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            AsyncManager.me().execute(AsyncFactory.recordLoginInfo(loginBody.getCode(), Constants.LOGIN_FAIL, e.getMessage()));
            return result.setError(e.getMessage()).setCode(404);
        }
        return login(authenticate, RoleType.STUDENT, isCheckState);
    }

    /**
     * 设置登陆用户当前角色信息
     */
    public String setLoginUserRole() {
        LoginUser loginUser = SecurityUtil.getLoginUser();
        initLoginUserRolesAndPermissions(loginUser);

        tokenService.refreshToken(loginUser);


        // 2022-05-21 新增判断身份证和人脸识别是否为空
        String identity = "yes";

        Person person = personService.getByUserId(SecurityUtil.getUserId());
        if (person == null) throw new CustomException("未查询到人员信息");
        if (StringUtils.isEmpty(person.getFacePhotoUrl())
                || StringUtils.isEmpty(person.getIdPhotoFace()) || "[]".equals(person.getIdPhotoFace())
                || StringUtils.isEmpty(person.getIdPhotoBack()) || "[]".equals(person.getIdPhotoBack())) {
            identity = "no";
        }
        if (!person.getVerified()) {
            identity = "no";
        }
        return identity;
    }

    public PersonVo getUserInfo(Long userId) {
        Person person = personService.getByUserId(userId);
        if (person == null) throw new CustomException("未查询到人员信息");
        PersonVo personVo = new PersonVo();
        BeanUtil.copyProperties(person, personVo);
        SysOrg sysOrg = person.getOrgId() != null ? sysOrgService.getById(person.getOrgId()) : null;
        if (sysOrg != null) {
            if (sysOrg.getType() == 1) {
                // 如果组织为单位，则直接返回单位名称
                personVo.setUnit(sysOrg.getName());
            } else {
                // 先查询出部门信息，再查询所属单位
                SysOrg parentOrg = sysOrgService.findUnitByOrgId(sysOrg.getPid());
                if (parentOrg != null) {
                    personVo.setUnit(parentOrg.getName());
                }
                personVo.setDept(sysOrg.getName());
            }
        }
        personVo.setPersonId(person.getId());

        // 学时
        BigDecimal yearClassHour = studyRecordService.getBaseMapper().getYearSumStudyHours(person.getId());
        personVo.setYearClassHour(yearClassHour);
        BigDecimal sumClassHour = studyRecordService.getBaseMapper().getSumStudyHours(person.getId());
        personVo.setSumClassHour(sumClassHour);

        return personVo;
    }

    /**
     * 如果用户还在使用旧的 MD5 密码，自动将其升级为 BCrypt
     */
    private void upgradePasswordIfNecessary(Authentication authentication, String rawPassword) {
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            return;
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = loginUser.getUser();
        if (user == null || StringUtils.isEmpty(user.getPassword())) {
            return;
        }

        // 如果密码不是以 {bcrypt} 开头，说明是旧的 MD5 密码（或者是没加前缀的旧数据）
        if (!user.getPassword().startsWith("{bcrypt}")) {
            log.info("用户 {} 正在升级密码到 BCrypt...", user.getUserName());
            try {
                // 使用新的 BCrypt 编码器（带 {bcrypt} 前缀）进行编码
                String newPassword = SecurityUtil.encryptPassword(rawPassword);
                user.setPassword(newPassword);
                // 升级后不再需要旧的 Salt
                user.setSalt("");
                sysUserService.updateById(user);
                log.info("用户 {} 密码升级成功。", user.getUserName());
            } catch (Exception e) {
                log.error("用户 {} 密码升级失败: {}", user.getUserName(), e.getMessage());
            }
        }
    }
}
