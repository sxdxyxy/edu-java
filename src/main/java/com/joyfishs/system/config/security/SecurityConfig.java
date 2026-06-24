package com.joyfishs.system.config.security;

import com.google.common.collect.Lists;
import com.joyfishs.system.config.security.captcha.CaptchaAuthenticationProvider;
import com.joyfishs.system.config.security.captcha.CaptchaService;
import com.joyfishs.system.config.security.captcha.CaptchaUserDetailsService;
import com.joyfishs.system.config.security.face.FaceAuthUserDetailsService;
import com.joyfishs.system.config.security.face.FaceAuthenticationProvider;
import com.joyfishs.system.config.security.weixin.OpenPlatformAuthenticationProvider;
import com.joyfishs.system.config.security.weixin.OpenPlatformUserDetailsService;
import com.joyfishs.system.service.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * spring security配置
 **/
@SuppressWarnings("deprecation")
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 自定义用户认证逻辑
     **/
    @Autowired
    private CustomUserService customUserService;
    /**
     * 认证失败处理类
     **/
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出处理类
     **/
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private OpenPlatformUserDetailsService openPlatformUserDetailsService;

    @Autowired
    private CaptchaUserDetailsService captchaUserDetailsService;

    @Autowired
    private FaceAuthUserDetailsService faceAuthUserDetailsService;

    /**
     * token认证过滤器
     **/
    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    /**
     * 解决 无法直接注入 AuthenticationManager
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // CSRF禁用，因为不使用session
                .csrf().disable()
                // 认证失败处理类
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 过滤请求
                .authorizeRequests()

                // 对于登录 login 允许匿名访问
                .antMatchers("/login").anonymous()
                .antMatchers("/studentLogin").anonymous()
                .antMatchers("/autoLogin").anonymous()
                .antMatchers("/studentCaptchaLogin").anonymous()
                .antMatchers("/studentWeiXinWebLogin").anonymous()
                .antMatchers("/faceLogin").anonymous()
                .antMatchers("/studentMiniProgramLogin").anonymous()
                .antMatchers("/studentWeiXinAppLogin").anonymous()
                .antMatchers("/mobile/home/**").permitAll()
                .antMatchers("/notice/get").permitAll()
                .antMatchers("/notice/read").permitAll()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/person/passCertificate").permitAll()
                .antMatchers("/person/studyRecord").permitAll()
                .antMatchers("/person/getPassCertificateQRCode").permitAll()
                .antMatchers("/person/getStudyRecordQRCode").permitAll()
                .antMatchers("/").permitAll()
                // 腾讯vod通知
                .antMatchers("/vodEventReceive").permitAll()
                // Swagger 文档
                .antMatchers("/doc.html").permitAll()
                .antMatchers("/api-docs-ext").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()

                .antMatchers("/person/register/invitation").permitAll()
                .antMatchers("/person/register/add").permitAll()
                .antMatchers("/person/scanCodeGet").permitAll()
                // 文件下载接口例外
                .antMatchers("/sys/oss/show/**").permitAll()
                // 档案二维码公开访问
                .antMatchers("/safetySupervision/archives/config/data/qrcode/**").permitAll()
                // D6: dev-sts 本地二维码静态目录公开访问 (生产走 OSS 不需要)
                .antMatchers("/qrcode/**").permitAll()

                .antMatchers("/sys/ueditor/config").permitAll()
                // 获取短信验证码接口
                .antMatchers("/sys/sms/getCode").anonymous()
                .antMatchers("/sys/sms/loginCaptcha").anonymous()
                .antMatchers("/sys/sms/registerCaptcha").anonymous()
                .antMatchers("/sys/sms/changePhoneCaptcha").anonymous()
                // 获取数据词典项下拉选
                .antMatchers("/sys/dictionary/item/mobile/dropDown").permitAll()
                // 获取app版本管理相关信息
                .antMatchers("/sys/appVersion/getVersionNumByAppType", "/sys/appVersion/clearVersionCache").permitAll()
                // COS队列回调
                // .antMatchers("/sys/oss/cos/callback").anonymous()

                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                .and()
                .headers().frameOptions().disable();
        httpSecurity.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);
        // 添加JWT filter
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加CORS filter
        // httpSecurity.addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class);
        // httpSecurity.addFilterBefore(corsFilter, LogoutFilter.class);
    }


    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", bCryptPasswordEncoder());
        // 安全修复: 移除MD5编码器,强制使用BCrypt
        // 旧MD5密码需要在用户下次登录时自动升级到BCrypt
        // encoders.put("md5", Md5PasswordEncoder.getInstance());

        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
        // 安全修复: 默认编码器改为BCrypt,不再支持纯MD5密码
        // 旧密码需要通过 CustomUserService.upgradePasswordIfNecessary() 升级
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(bCryptPasswordEncoder());
        return delegatingPasswordEncoder;
    }

    @Bean
    public CaptchaAuthenticationProvider captchaAuthenticationProvider() {
        CaptchaAuthenticationProvider authenticationProvider = new CaptchaAuthenticationProvider(captchaUserDetailsService, captchaService);
        return authenticationProvider;
    }

    @Bean
    public OpenPlatformAuthenticationProvider openPlatformAuthenticationProvider() {
        OpenPlatformAuthenticationProvider authenticationProvider = new OpenPlatformAuthenticationProvider(openPlatformUserDetailsService);
        return authenticationProvider;
    }

    @Bean
    public FaceAuthenticationProvider faceAuthenticationProvider() {
        FaceAuthenticationProvider authenticationProvider = new FaceAuthenticationProvider(faceAuthUserDetailsService);
        return authenticationProvider;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(customUserService);
        return daoAuthenticationProvider;
    }

    /**
     * 定义认证管理器AuthenticationManager
     *
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authenticationProviders = Lists.newArrayList();
        authenticationProviders.add(captchaAuthenticationProvider());
        authenticationProviders.add(daoAuthenticationProvider());
        authenticationProviders.add(openPlatformAuthenticationProvider());
        authenticationProviders.add(faceAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(authenticationProviders);
        return authenticationManager;
    }
}
