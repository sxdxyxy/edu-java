package com.joyfishs.dawa.person.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.person.domain.param.*;
import com.joyfishs.dawa.person.domain.result.PersonListResult;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.entity.PersonCertificate;
import com.joyfishs.dawa.person.entity.PersonRegister;
import com.joyfishs.dawa.person.enums.PersonChangeType;
import com.joyfishs.dawa.person.mapper.PersonMapper;
import com.joyfishs.oss.domain.UploadResult;
import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.config.CosConfig;
import com.joyfishs.system.config.security.Md5PasswordEncoder;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.enums.DeviceType;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.service.SysUserRoleService;
import com.joyfishs.system.service.SysUserService;
import com.joyfishs.tencent.service.FaceAiService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import com.joyfishs.utils.upload.FileUtils;
import com.tencentcloudapi.iai.v20200303.models.CompareFaceResponse;
import com.tencentcloudapi.iai.v20200303.models.GetPersonBaseInfoResponse;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * @author yangkaifeng
 */
@Slf4j
@Service
public class PersonService extends ServiceImpl<PersonMapper, Person> {

    final static String qrText = "微信扫一扫，查看个人档案";
    @Autowired
    private PersonRegisterService personRegisterService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private PersonCertificateService personCertificateService;
    @Autowired
    private SysOrgService sysOrgService;
    @Autowired
    private PersonChangeLogService personChangeLogService;
    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private FaceAiService faceAiService;
    @Autowired
    private PersonOrgService personOrgService;

    public static void main(String[] args) {
        Person person = new Person();
        File tempFile = FileUtil.createTempFile(".jpg", true);
        HttpUtil.downloadFile("https://static.joyfishs.com/qrcode/20250714/52bd9064-88f1-44c2-90f7-e6bb1e0c5184822139257180461149.jpg", tempFile);
        person.setQrCodeUrl("https://static.joyfishs.com/qrcode/20250714/52bd9064-88f1-44c2-90f7-e6bb1e0c5184822139257180461149.jpg");
        new PersonService().modifyImageText(person, tempFile);
        System.out.println(person.getQrCodeUrl());
    }

    @Transactional
    public boolean saveOrUpdatePerson(Person person) {
        // 获取原始数据用于比较
        Person originalPerson = null;
        if (person.getId() != null) {
            originalPerson = getById(person.getId());
        }

        // 检查是否需要进行人脸验证
        if (needFaceVerification(person, originalPerson)) {
            String verifyResult = verifiedFace(person);
            if (verifyResult != null) {
                throw new CustomException(verifyResult);
            }
        }

        if (null == person.getId()) {
            return executeSave(person);
        } else {
            return executeUpdate(person);
        }
    }

    /**
     * 修改人员姓名
     *
     * @param personUpdateNameParam
     * @return
     */
    @Transactional
    public boolean updateName(PersonUpdateNameParam personUpdateNameParam) {
        log.info("XmPersonService - updateName personUpdateNameParam:{}", personUpdateNameParam);

        Person person = getById(personUpdateNameParam.getId());
        log.info("XmPersonService - updateName person:{}", person);

        if (Validator.isNull(person) || Validator.isNull(person.getId())) {
            throw new CustomException("人员不存在");
        }
        if (Validator.isNotEmpty(person.getName())) {
            throw new CustomException("人员姓名不能随意修改");
        }

        person.setName(personUpdateNameParam.getName());
        log.info("XmPersonService - updateName person:{}", person);

        return executeUpdate(person);
    }

    // 新增人员
    private boolean executeSave(Person person) {
        checkParam(person, false);

        // 新建用户
        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(person, sysUser);

        sysUser.setSalt(DigestUtil.md5Hex(sysUser.getUserName()));
        Md5PasswordEncoder md5PasswordEncoder = Md5PasswordEncoder.getInstance();
        md5PasswordEncoder.setSalt(sysUser.getSalt());
        sysUser.setPassword(md5PasswordEncoder.encode(SysUser.DEFAULT_PASSWORD));
        sysUserService.saveOrUpdate(sysUser);
        // 保存学员角色信息
        sysUserRoleService.save(sysUser.getId(), CollectionUtil.newArrayList(SysRole.STUDENT_ROLE));

        person.setUserId(sysUser.getId());
        person.setState(1);
        person.setIsAdmin(2);
        person.setCreateBy(SecurityUtil.getUserId());
        person.setCreateTime(new Date());
        person.setIsDelete(YesOrNoState.NO.getState());

        // 记录人员变动
        personChangeLogService.executeSave(sysUser.getId(), PersonChangeType.CREATE.getReasonDesc(), person.getOrgId(), JSONObject.toJSONString(person), SecurityUtil.getUserId());

        return save(person);
    }

    private boolean executeUpdate(Person person) {
        checkParam(person, true);

        person.setUpdateBy(SecurityUtil.getUserId());
        person.setUpdateTime(new Date());

        Person xmPerson = getById(person.getId());
        if (ObjectUtil.isNull(xmPerson)) {
            throw new CustomException("人员不存在");
        }

        // 修改用户
        SysUser sysUser = sysUserService.getById(person.getUserId());
        if (ObjectUtil.isNull(sysUser)) {
            throw new CustomException("未查询到用户信息");
        }

        if (person.getIsRegisterVerify() == 1) { // 2022-05-12 改为注册自动审核通过 此代码无用
            // 如果审核通过,则人员和用户状态置为正常
            person.setState(1);
            sysUser.setStatus(1);

            // 根据用户名查询注册信息
            PersonRegister personRegister = personRegisterService.getByUserName(person.getUserName());
            if (ObjectUtil.isNull(personRegister)) {
                throw new CustomException("注册信息不存在");
            }
            // 新增人员成功之后 把注册信息删除
            personRegister.setIsDelete(YesOrNoState.YES.getState());
            personRegister.setDeleteBy(SecurityUtil.getUserId());
            personRegister.setDeleteTime(new Date());
            personRegisterService.updateById(personRegister);
        }

        sysUser.setUserName(person.getUserName());
        sysUser.setName(person.getName());
        if (ObjectUtil.isNotNull(person.getSex())) {
            sysUser.setSex(person.getSex());
        }
        if (StrUtil.isNotBlank(person.getIdCardNo())) {
            sysUser.setIdCardNo(person.getIdCardNo());
        }
        sysUser.setPhone(person.getPhone());
        sysUserService.saveOrUpdate(sysUser);

        // 记录人员变动
        personChangeLogService.executeSave(sysUser.getId(), PersonChangeType.UPDATE.getReasonDesc(), person.getOrgId(), JSONObject.toJSONString(person), SecurityUtil.getUserId());

        return updateById(person);
    }

    /**
     * 校验参数，检查是否存在相同的用户名
     */
    public void checkParam(Person person, boolean isExcludeSelf) {
        Long id = person.getId();
        String userName = person.getUserName();
        String phone = person.getPhone();
        LambdaQueryWrapper<Person> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Person::getUserName, userName)
                .eq(Person::getIsDelete, YesOrNoState.NO.getState());

        LambdaQueryWrapper<Person> queryWrapperByPhone = new LambdaQueryWrapper<>();
        queryWrapperByPhone.eq(Person::getPhone, phone)
                .eq(Person::getIsDelete, YesOrNoState.NO.getState());

        //是否排除自己，如果是则查询条件排除自己id
        if (isExcludeSelf) {
            queryWrapper.ne(Person::getId, id);
            queryWrapperByPhone.ne(Person::getId, id);
        }
        long countByAccount = this.count(queryWrapper);
        long countByPhone = this.count(queryWrapperByPhone);

        //大于等于1个则表示重复
        if (countByAccount >= 1) {
            throw new CustomException("用户名：" + person.getUserName() + "已存在");
        }
        if (countByPhone >= 1) {
            throw new CustomException("电话号码：" + person.getPhone() + "已存在");
        }
    }

    @Transactional
    public boolean del(Long id, String deleteReason) {
        Person person = getById(id);
        if (ObjectUtil.isNull(person)) {
            throw new CustomException("删除数据不存在");
        }
        person.setIsDelete(YesOrNoState.YES.getState());
        person.setDeleteBy(SecurityUtil.getUserId());
        person.setDeleteTime(new Date());
        person.setDeleteReason(deleteReason);

        // 删除用户
        sysUserService.del(CollectionUtil.newArrayList(person.getUserId()), "人员删除");

        // 删除注册信息
        personRegisterService.delByPersonId(id, deleteReason);

        // 记录人员变动
        personChangeLogService.executeSave(person.getUserId(), PersonChangeType.DELETE.getReasonDesc(), person.getOrgId(), JSONObject.toJSONString(person), SecurityUtil.getUserId());

        return updateById(person);
    }

    public List<Person> findList(PersonListQueryRequest req) {
        List<Person> personList;
        // 如果查询项目下的人员,则需要查询关联表
        if (req.getOrgType() != null && req.getOrgType() == 2) {
            personList = baseMapper.queryListByOrg(req);
        } else {
            personList = baseMapper.queryList(req);
        }
        return personList;
    }

    public List<PersonListResult> packPersonResult(List<Person> personList) {
        List<PersonListResult> list = new ArrayList<>();
        for (Person person : personList) {
            PersonListResult personResult = new PersonListResult();
            BeanUtil.copyProperties(person, personResult);
            // 查询单位部门信息
            String[] orgArr = sysOrgService.getOrgUnitDept(person.getOrgId());
            personResult.setUnit(orgArr[0]);
            personResult.setDept(orgArr[1]);
            list.add(personResult);
        }
        return list;
    }

    public Person get(Long id) {
        Person person = getById(id);
        // 查询证书列表
        List<PersonCertificate> certificateList = personCertificateService.listByPersonId(person.getId());
        person.setCertificateList(certificateList);

        SysOrg sysOrg = sysOrgService.getById(person.getOrgId());
        person.setOrgName(sysOrg.getName());

        // 查询所在项目组信息
        String projectTeam = personOrgService.getBaseMapper().getOrgNameByPersonId(id);
        person.setProjectTeam(projectTeam);

        // 转换人脸照片URL：将腾讯云COS链接转换为静态资源链接
        if (StringUtils.isNotEmpty(person.getFacePhotoUrl()) &&
            person.getFacePhotoUrl().startsWith("https://safe-edu-1309460949.cos.ap-shanghai.myqcloud.com/")) {
            String convertedUrl = person.getFacePhotoUrl().replace(
                "https://safe-edu-1309460949.cos.ap-shanghai.myqcloud.com/",
                "https://static.joyfishs.com/"
            );
            person.setFacePhotoUrl(convertedUrl);
        }

        return person;
    }

    // 转移部门
    @Transactional
    public boolean transferOrg(String ids, Long targetOrgId) {
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Long.parseLong(idStr)).collect(Collectors.toList());
        List<Person> personList = new ArrayList<>();
        for (Long id : idList) {
            personList.add(changeOrg(id, targetOrgId));
        }
        return updateBatchById(personList);
    }

    public Person changeOrg(Long personId, Long targetOrgId) {
        Person person = getById(personId);
        if (ObjectUtil.isNull(person)) {
            throw new CustomException("人员不存在");
        }
        Long oldOrgId = person.getOrgId();
        person.setOrgId(targetOrgId);
        person.setUpdateBy(SecurityUtil.getUserId());
        person.setUpdateTime(new Date());
        // 新增人员变动信息
        personChangeLogService.executeSave(person.getUserId(), PersonChangeType.TRANSFER.getReasonDesc(), oldOrgId, JSONObject.toJSONString(person), SecurityUtil.getUserId());
        personChangeLogService.executeSave(person.getUserId(), PersonChangeType.TRANSFER.getReasonDesc(), targetOrgId, JSONObject.toJSONString(person), SecurityUtil.getUserId());
        //注意没有更新，需要在外部update
        return person;
    }

    // 离职
    @Transactional
    public boolean quit(String ids) {
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Long.parseLong(idStr)).collect(Collectors.toList());
        List<Person> personList = new ArrayList<>();
        for (Long id : idList) {
            Person person = getById(id);
            if (ObjectUtil.isNull(person)) {
                throw new CustomException("人员不存在");
            }
            person.setState(0);
            person.setUpdateBy(SecurityUtil.getUserId());
            person.setUpdateTime(new Date());
            personList.add(person);

            SysUser sysUser = sysUserService.getById(person.getUserId());
            if (ObjectUtil.isNull(sysUser)) {
                throw new CustomException("用户不存在");
            }
            sysUser.setStatus(0);
            sysUserService.updateById(sysUser);

            // 新增人员变动信息
            personChangeLogService.executeSave(person.getUserId(), PersonChangeType.QUIT.getReasonDesc(), person.getOrgId(), JSONObject.toJSONString(person), SecurityUtil.getUserId());

        }
        return updateBatchById(personList);
    }

    // 重新入职
    @Transactional
    public boolean rejoin(String ids) {
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Long.parseLong(idStr)).collect(Collectors.toList());
        List<Person> personList = new ArrayList<>();
        for (Long id : idList) {
            Person person = getById(id);
            if (ObjectUtil.isNull(person)) {
                throw new CustomException("人员不存在");
            }
            person.setState(1);
            person.setUpdateBy(SecurityUtil.getUserId());
            person.setUpdateTime(new Date());
            personList.add(person);

            SysUser sysUser = sysUserService.getById(person.getUserId());
            if (ObjectUtil.isNull(sysUser)) {
                throw new CustomException("用户不存在");
            }
            sysUser.setStatus(1);
            sysUserService.updateById(sysUser);

            // 新增人员变动信息
            personChangeLogService.executeSave(person.getUserId(), PersonChangeType.REJOIN.getReasonDesc(), person.getOrgId(), JSONObject.toJSONString(person), SecurityUtil.getUserId());

        }
        return updateBatchById(personList);
    }

    // 重置密码
    @Transactional
    public boolean resetPwd(Long id) {
        Person person = getById(id);
        if (ObjectUtil.isNull(person)) {
            throw new CustomException("人员不存在");
        }
        // 修改用户密码
        return sysUserService.resetPwd(person.getUserId());
    }

    // 设置为管理员
    @Transactional
    public void setAdmin(String ids) {
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Long.parseLong(idStr)).collect(Collectors.toList());
        List<Person> personList = new ArrayList<>();
        for (Long id : idList) {
            Person person = getById(id);
            if (ObjectUtil.isNull(person)) {
                throw new CustomException("人员不存在");
            }
            person.setIsAdmin(1);
            person.setUpdateBy(SecurityUtil.getUserId());
            person.setUpdateTime(new Date());
            personList.add(person);

            // 赋权人员角色
            SysUser sysUser = sysUserService.getById(person.getUserId());
            if (ObjectUtil.isNull(sysUser)) {
                throw new CustomException("未查询到用户信息");
            }
            // 暂定管理员角色id为2
            sysUserRoleService.save(sysUser.getId(), CollectionUtil.newArrayList(SysRole.PLATFORM_MANAGER_ROLE));
        }
        updateBatchById(personList);
    }

    // 取消管理员
    @Transactional
    public void cancelAdmin(Long id) {
        Person person = getById(id);
        if (ObjectUtil.isNull(person)) {
            throw new CustomException("人员不存在");
        }
        person.setIsAdmin(2);
        person.setUpdateBy(SecurityUtil.getUserId());
        person.setUpdateTime(new Date());
        // 赋权人员角色
        SysUser sysUser = sysUserService.getById(person.getUserId());
        if (ObjectUtil.isNull(sysUser)) {
            throw new CustomException("未查询到用户信息");
        }
        updateById(person);
        sysUserRoleService.delByUserIdAndRoleId(sysUser.getId(), CollectionUtil.newArrayList(SysRole.PLATFORM_MANAGER_ROLE));
    }

    /**
     * 通过用户id查询
     *
     * @param userId
     * @return
     */
    public Person getByUserId(Long userId) {
        return baseMapper.getByUserId(userId);
    }

    public Person findByPhone(String phone) {
        return baseMapper.findByPhone(phone);
    }

    @Transactional
    public void importTemplate(String fileUrl, Long orgId) {

        if (Validator.isEmpty(orgId)) {
            throw new CustomException("请选择导入的单位部门！");
        }

        File tempExcel = sysOssService.downloadByUrl(fileUrl);
        //读取文件
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(tempExcel));
        List<List<Object>> readAll = reader.read();

        //储存对象，进行批量写入
        List<Person> personList = new ArrayList<>();

        //第1个元素是字段名称，可删除掉
        readAll.remove(0);
        //遍历第一个List
        for (List<Object> object : readAll) {
            String name = String.valueOf(object.get(1));
            String phone = String.valueOf(object.get(2));
            String types = String.valueOf(object.get(3)); // 用户
            String state = String.valueOf(object.get(4)); // 是否停用

            // 上传信息不能为空
            if (StringUtils.isEmpty(name) ||
                    StringUtils.isEmpty(phone) || StringUtils.isEmpty(types) || StringUtils.isEmpty(state)) {
                throw new CustomException("用户信息不能为空，请检查上传文件");
            }

            Person person = new Person();
            person.setName(name);
            person.setSex(0);
            person.setUserName(phone);
            person.setState("否".equals(state) ? 0 : 1);
            person.setIsAdmin(2);
            person.setPhone(phone);
            person.setOrgId(orgId);

            // 新建用户
            SysUser sysUser = new SysUser();
            BeanUtil.copyProperties(person, sysUser);
            // D1-B: Person.state 与 SysUser.status 字段名不同, BeanUtil 不映射;
            //      旧代码不显式设置 SysUser.status, 导致 INSERT 时 status=NULL,
            //      而 sys_user.status 默认 0 (停用), 后续基于 status=1 的查询不到,
            //      且某些上游 SQL 拼接 IS NULL / IN (...) 失败, 上抛为"系统繁忙"。
            //      显式赋值后, 新建用户为"启用", 与 sysUserService.saveOrUpdate 行为一致。
            sysUser.setStatus(person.getState() == null ? 1 : person.getState());
            sysUser.setPassword(SysUser.DEFAULT_PASSWORD);
            sysUserService.saveOrUpdate(sysUser);
            // 保存学员角色信息
            sysUserRoleService.save(sysUser.getId(), CollectionUtil.newArrayList(SysRole.STUDENT_ROLE));

            person.setUserId(sysUser.getId());
            person.setState(1);
            person.setCreateBy(SecurityUtil.getUserId());
            person.setCreateTime(new Date());
            person.setIsDelete(YesOrNoState.NO.getState());

            // 记录人员变动
            personChangeLogService.executeSave(sysUser.getId(), PersonChangeType.CREATE.getReasonDesc(), person.getOrgId(), JSONObject.toJSONString(person), SecurityUtil.getUserId());

            personList.add(person);
        }

        // D1-B: 包 try/catch 一次批量写入, 任一行失败能定位具体行, 不再"系统繁忙"
        try {
            this.saveBatch(personList);
        } catch (Exception e) {
            log.error("person importTemplate saveBatch failed, rows={}", personList.size(), e);
            throw new CustomException("批量写入人员失败: " + e.getMessage());
        }
    }

    /**
     * 修改身份证信息
     */
    @Transactional
    public void updateIdCard(UpdateIdCardParam param) throws IOException {
        Person person = getById(param.getPersonId());
        if (ObjectUtil.isNull(person)) {
            throw new CustomException("人员不存在");
        }
        //此身份证号码除自己外是否有其它人已注册
        Long repeatPersonId = this.baseMapper.repeatIdCardNoCheck(param.getPersonId(), param.getIdCardNo());
        if (ObjectUtil.isNotNull(repeatPersonId)) {
            throw new CustomException("该身份证账号已经被绑定");
        }
        String idPhotoFace, idPhotoBack;
        if (Validator.isUrl(param.getIdPhotoFace())) {
            idPhotoFace = '[' + sysOssService.buildJson(sysOssService.findByUrl(param.getIdPhotoFace())) + ']';
            idPhotoBack = '[' + sysOssService.buildJson(sysOssService.findByUrl(param.getIdPhotoBack())) + ']';
        } else {
            idPhotoFace = this.executeUploadBase64(param.getIdPhotoFace().substring(param.getIdPhotoFace().indexOf(",") + 1));
            idPhotoBack = this.executeUploadBase64(param.getIdPhotoBack().substring(param.getIdPhotoBack().indexOf(",") + 1));
        }

        person.setName(param.getName());
        person.setIdCardNo(param.getIdCardNo());
        person.setIdPhotoFace(idPhotoFace);
        person.setIdPhotoBack(idPhotoBack);
        person.setSex(IdcardUtil.getGenderByIdCard(param.getIdCardNo()) == 1 ? 1 : 2);
        person.setVerified(Boolean.FALSE);
        SysUser sysUser = sysUserService.getById(person.getUserId());
        if (ObjectUtil.isNull(sysUser)) {
            throw new CustomException("用户不存在");
        }

        sysUser.setName(param.getName());
        sysUser.setIdCardNo(param.getIdCardNo());
        sysUser.setSex(person.getSex());
        sysUserService.updateById(sysUser);

        PersonRegister personRegister = personRegisterService.getByUserName(sysUser.getUserName());
        if (Validator.isNotNull(personRegister) && Validator.isNotNull(personRegister.getId())) {
            personRegister.setName(param.getName());
            personRegister.setUpdateBy(SecurityUtil.getUserId());
            personRegister.setUpdateTime(new Date());
            personRegisterService.updateById(personRegister);
        }
        this.executeUpdate(person);
    }

    public String executeUploadBase64(String base64) throws IOException {
        if (StringUtils.isEmpty(base64)) {
            throw new CustomException("上传文件Base64不能为空");
        }
        byte[] data = Base64.decodeBase64(base64);
        String fileName = String.format("%s.%s", IdUtil.simpleUUID(), "png");
        MultipartFile file = new MockMultipartFile(fileName, fileName, "application/x-png", data);

        List<AjaxResult<?>> resultList = new ArrayList<>();
        AjaxResult<?> result = sysOssService.upload(file, "idPhoto");
        resultList.add(result);
        String[] includeProperties = {"uid", "name", "url"};
        SimplePropertyPreFilter includefilter = new SimplePropertyPreFilter();
        
        // 指定包含属性过滤器：转换成JSON字符串时，包含哪些属性
        for (String prop : includeProperties) { includefilter.getIncludes().add(prop); }

        String resultStr = JSON.toJSONString(resultList, includefilter);
        return resultStr;
    }

    public UploadResult executeUpload2Base64(String base64) {
        BufferedImage image = ImgUtil.toImage(base64);
        File pngImageFile = FileUtil.createTempFile(".png", true);
        ImgUtil.write(image, pngImageFile);
        String fileKey = "idPhoto" + StrUtil.C_SLASH + DateUtil.format(new Date(), "yyyyMMdd") + StrUtil.C_SLASH + IdUtil.objectId() + StrUtil.DOT + "png";
        UploadResult result = sysOssService.upload(pngImageFile, fileKey);
        return result;
    }

    @Transactional
    public void updatePhone(String oldPhone, String newPhone, String password) {
        SysUser sysUser = sysUserService.getById(SecurityUtil.getUserId());
        if (ObjectUtil.isNull(sysUser)) {
            throw new CustomException("未查询到用户信息");
        }
        if (!oldPhone.equals(sysUser.getPhone())) {
            throw new CustomException("原手机号不正确！");
        }
        Md5PasswordEncoder.getInstance().setSalt(sysUser.getSalt());
        if (!Md5PasswordEncoder.getInstance().matches(password, sysUser.getPassword())) {
            throw new CustomException("密码不正确！");
        }

        sysUser.setPhone(newPhone);
        sysUser.setUserName(newPhone);
        sysUserService.updateById(sysUser);

        Person person = this.getByUserId(sysUser.getId());
        if (ObjectUtil.isNull(person)) {
            throw new CustomException("未查询到人员信息");
        }
        person.setPhone(newPhone);
        this.updateById(person);

    }

    /**
     * 更新头像
     */
    @Transactional
    public void updateAvatar(Person person, String avatar) {
        person.setAvatar(avatar);
        this.updateById(person);
    }

    /**
     * 更新人脸识别头像照片
     */
    @Transactional
    public void updateFacePhoto(Person person, String facePhotoUrl) {
        person.setFacePhotoUrl(facePhotoUrl);
        person.setVerified(Boolean.FALSE);
        this.updateById(person);
    }

    /**
     * 判断是否需要进行人脸验证
     *
     * @param person 当前人员信息
     * @param originalPerson 原始人员信息（新增时为null）
     * @return true-需要验证，false-不需要验证
     */
    private boolean needFaceVerification(Person person, Person originalPerson) {
        // 如果是新增人员，不做人脸验证（因为此时人员ID还不存在，无法调用验证接口）
        if (originalPerson == null) {
            return false;
        }

        // 检查是否设置了身份证照片和人脸头像照片
        boolean hasIdPhoto = StringUtils.isNotEmpty(person.getIdPhotoFace());
        boolean hasFacePhoto = StringUtils.isNotEmpty(person.getFacePhotoUrl());

        // 如果没有设置身份证照片或人脸头像照片，不做验证
        if (!hasIdPhoto || !hasFacePhoto) {
            return false;
        }

        // 检查身份证照片是否发生了变化
        boolean idPhotoChanged = !StringUtils.equals(person.getIdPhotoFace(), originalPerson.getIdPhotoFace());

        // 检查人脸头像照片是否发生了变化
        boolean facePhotoChanged = !StringUtils.equals(person.getFacePhotoUrl(), originalPerson.getFacePhotoUrl());

        // 只有当身份证照片或人脸头像照片发生了变化时，才需要进行人脸验证
        return idPhotoChanged || facePhotoChanged;
    }

    /**
     * 验证人脸和身份证是否一致，如果一致则上传腾讯人脸库
     *
     * @param person
     */
    @Transactional
    public String verifiedFace(Person person) {
        if (StringUtils.isEmpty(person.getIdPhotoFace())) {
            return "请先拍摄身份证";
        }
        JSONArray jArray = JSONUtil.parseArray(person.getIdPhotoFace());
        cn.hutool.json.JSONObject jObj = jArray.getJSONObject(0);
        String idPhotoFaceUrl = jObj.getStr("url");
        CompareFaceResponse res = faceAiService.compareFace(person.getFacePhotoUrl(), idPhotoFaceUrl);
        if (res.getScore() < 50) {
            return "此头像照片和身份证不一致，请重新拍摄身份证或头像照片。";
        }
        //核对一致上传人脸库
        GetPersonBaseInfoResponse personFaceInfo = faceAiService.queryPersonRequest(person.getId());
        if (ObjectUtil.isNull(personFaceInfo)) {
            faceAiService.createPerson(person);
        } else {
            if (personFaceInfo.getFaceIds().length >= 5) {
                faceAiService.deleteFace(person.getId(), personFaceInfo.getFaceIds()[0]);
            }
            faceAiService.createFace(person);
        }
        person.setVerified(Boolean.TRUE);
        this.updateById(person);
        return null;
    }

    /**
     * 人脸核身，上传人脸头像和身份信息获取faceId
     */
    @Transactional
    public JSONObject getFaceId(Long personId) {
        Person person = getById(personId);
        if (StringUtils.isEmpty(person.getFacePhotoUrl())) {
            throw new CustomException("您还未上传用于人脸识别的头像，请前往个人资料里完善头像");
        }
        String imageBase64 = FileUtils.urlFileToBase64(person.getFacePhotoUrl() + FaceAiService.THUMBNAIL);
        return faceAiService.getFaceId(person.getId(), person.getName(), person.getIdCardNo(), imageBase64);
    }

    @Transactional
    public boolean bindWeixin(Long personId, BindWeixinParam param) {
        Person person = getById(personId);
        PersonRegister register = personRegisterService.findByPhone(person.getPhone());
        //导入人员没有PersonRegister
        if (ObjectUtil.isNull(register)) {
            register = new PersonRegister();
            register.setUserName(person.getPhone());
            register.setPhone(person.getPhone());
            register.setName(person.getName());
            register.setIsDelete(YesOrNoState.NO.getState());
            register.setCreateTime(new Date());
            register.setRegDevice(DeviceType.WEB);
            register.setOrgId(person.getOrgId());
            register.setPersonId(person.getId());
        }
        register.setOpenid(param.getOpenid());
        register.setUnionid(param.getUnionid());
        if (ObjectUtil.isNull(register.getId())) {
            return personRegisterService.save(register);
        }
        return personRegisterService.updateById(register);
    }

    /**
     * 查询组织下的人员
     *
     * @param orgId
     * @return
     */
    public List<Person> findListByOrgId(Long orgId) {
        return baseMapper.findListByOrgId(orgId);
    }

    public boolean setWorkType(Long personId, Integer workType) {
        LambdaUpdateWrapper<Person> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Person::getId, personId);
        updateWrapper.set(Person::getWorkType, workType);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    public boolean setJobs(Long personId, Integer jobs) {
        LambdaUpdateWrapper<Person> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Person::getId, personId);
        updateWrapper.set(Person::getJobs, jobs);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    public boolean setDegree(Long personId, Integer degreeId) {
        LambdaUpdateWrapper<Person> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Person::getId, personId);
        updateWrapper.set(Person::getDegreeId, degreeId);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    public boolean setHomeAddress(UpdatePersonAddressRequest req) {
        LambdaUpdateWrapper<Person> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Person::getId, req.getId());
        updateWrapper.set(Person::getHomeAddress, req.getAddress());
        return baseMapper.update(null, updateWrapper) > 0;
    }

    public boolean setResidenceAddress(UpdatePersonAddressRequest req) {
        LambdaUpdateWrapper<Person> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Person::getId, req.getId());
        updateWrapper.set(Person::getResidenceAddress, req.getAddress());
        return baseMapper.update(null, updateWrapper) > 0;
    }

    public boolean setBloodType(Long personId, String bloodType) {
        LambdaUpdateWrapper<Person> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Person::getId, personId);
        updateWrapper.set(Person::getBloodType, bloodType);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    public boolean setNation(Long personId, String nation) {
        LambdaUpdateWrapper<Person> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Person::getId, personId);
        updateWrapper.set(Person::getNation, nation);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    public String getQRCode(Long personId) throws WxErrorException {
        Person person = getById(personId);
        if (ObjectUtil.isNull(person)) {
            return null;
        }
        if (StringUtils.isNotEmpty(person.getQrCodeUrl())) {
            if (StringUtils.isEmpty(person.getQrCode())) {
                File tempFile = FileUtil.createTempFile(".jpg", true);
                HttpUtil.downloadFile(person.getQrCodeUrl(), tempFile);
                modifyImageText(person, tempFile);
                this.updateById(person);
            }
            return person.getQrCodeUrl();
        }
        File qrCodeFile = wxMaService.getQrcodeService().createQrcode("subPages/external/index?personId=" + personId, 430);
        person = modifyImageText(person, qrCodeFile);
        this.updateById(person);
        return person.getQrCodeUrl();
    }

    public Person modifyImageText(Person person, File imageFile) {
        try {
            // 1. 识别原始二维码内容
            String content = QrCodeUtil.decode(imageFile);
            if (StringUtils.isEmpty(content)) {
                return null;
            }
            person.setQrCode(content);

            // 2. 生成新的二维码配置
            QrConfig config = new QrConfig(300, 300);
            config.setMargin(1);

            // 3. 生成二维码图片
            BufferedImage qrImage = QrCodeUtil.generate(content, config);
            if (qrImage == null) {
                log.error("生成二维码图片失败");
                return null;
            }
            // 4. 创建带文字的图片
            int textHeight = 30; // 文字区域高度
            BufferedImage finalImage = new BufferedImage(
                    qrImage.getWidth(),
                    qrImage.getHeight() + textHeight,
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D graphics = finalImage.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, finalImage.getWidth(), finalImage.getHeight());

            // 绘制二维码
            graphics.drawImage(qrImage, 0, 0, null);

            // 绘制文字
            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font("Noto Sans CJK SC Regular", Font.PLAIN, 24)); // 使用字体检测方法确保字体可用

            // 计算文字位置居中
            FontMetrics fm = graphics.getFontMetrics();
            int textWidth = fm.stringWidth(qrText);
            int x = (finalImage.getWidth() - textWidth) / 2;
            int y = qrImage.getHeight() + 20; // 文字在底部区域垂直居中

            graphics.drawString(qrText, x, y);
            graphics.dispose();

            // 5. 保存图片
            File tempFile = FileUtil.createTempFile(".jpg", true);
            ImgUtil.write(finalImage, tempFile);
            String signedFileKey = (CosConfig.QR_CODE + StrUtil.C_SLASH + DateUtil.format(new Date(), "yyyyMMdd") + StrUtil.C_SLASH + imageFile.getName());
            UploadResult uploadResult = sysOssService.upload(tempFile, signedFileKey);
            if (uploadResult == null || StringUtils.isEmpty(uploadResult.getUrl())) {
                log.error("上传图片到OSS失败");
                return null;
            }
            log.info("图片上传成功，URL: {}", uploadResult.getUrl());

            person.setQrCodeUrl(uploadResult.getUrl());
            return person;
        } catch (Exception e) {
            log.error("处理二维码图片时发生异常", e);
            return null;
        }
    }
}
