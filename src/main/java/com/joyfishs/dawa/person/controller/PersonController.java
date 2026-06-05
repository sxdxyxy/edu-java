package com.joyfishs.dawa.person.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.joyfishs.dawa.person.domain.param.*;
import com.joyfishs.dawa.person.domain.po.IDCardOCRRequest;
import com.joyfishs.dawa.person.domain.result.IDCardOCRResult;
import com.joyfishs.dawa.person.domain.result.PersonDetail;
import com.joyfishs.dawa.person.domain.result.PersonListResult;
import com.joyfishs.dawa.person.domain.result.SimplePerson;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.statistics.domain.StudyRecordExportVo;
import com.joyfishs.dawa.statistics.service.PersonStatisticsService;
import com.joyfishs.oss.domain.UploadResult;
import com.joyfishs.system.annotation.CheckVerify;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.tencent.service.FaceAiService;
import com.joyfishs.tencent.service.OcrService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import com.joyfishs.utils.page.TableDataInfo;
import com.joyfishs.utils.poi.ExcelUtil;
import com.tencentcloudapi.iai.v20200303.models.FaceInfo;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * @author yangkaifeng
 */
@Slf4j
@RestController
@Api(tags = "人员管理")
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController extends BaseController {

    //个人单位id
    static Long PERSON_ORG_ID = 22L;
    private final PersonService personService;
    private final FaceAiService faceAiService;
    private final OcrService ocrService;
    private final PersonStatisticsService personStatisticsService;

//    @PostMapping("/update")
//    @ApiOperation(value = "人员更新")
//    @Log(title = "人员更新", businessType = BusinessType.INSERT)
//    public R<Boolean> udpate(@RequestBody @Validated PersonSaveReqVO vo) {
//        return R.ok(personService.updatePerson(vo));
//    }

    @PostMapping("/addOrUpdate")
    @ApiOperation(value = "人员新增")
    @Log(title = "人员新增", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody @Validated Person person) {
        return AjaxResult.success(personService.saveOrUpdatePerson(person));
    }

    @PostMapping("/updateName")
    @ApiOperation(value = "修改人员姓名")
    @Log(title = "修改人员姓名", businessType = BusinessType.UPDATE)
    public AjaxResult<?> updateName(@RequestBody @Validated PersonUpdateNameParam personUpdateNameParam) {
        log.info("XmPersonController - updateName personUpdateNameParam:{}", personUpdateNameParam);
        return AjaxResult.success(personService.updateName(personUpdateNameParam));
    }

    @PostMapping("/del")
    @PreAuthorize("@ss.hasPermi('person:del')")
    @ApiOperation(value = "删除人员")
    @Log(title = "删除人员", businessType = BusinessType.DELETE)
    public AjaxResult<?> del(Long id, String deleteReason) {
        if (null == id) {
            throw new CustomException("删除id不能为空");
        }
        return toAjax(personService.del(id, deleteReason));
    }

    @GetMapping("/pageList")
    @PreAuthorize("@ss.hasPermi('person:list')")
    @ApiOperation(value = "人员列表")
    public TableDataInfo<PersonListResult> pageList(PersonListQueryRequest req) {
        startPage();
        List<Person> list = personService.findList(req);
        List<PersonListResult> personListResults = personService.packPersonResult(list);
        return getDataTable(list, personListResults);
    }

    @GetMapping("/get")
    @ApiOperation(value = "人员详情")
    public AjaxResult<?> get(@RequestParam String id) {
        Long personId = null;
        if ("undefined".equals(id)) {
            personId = SecurityUtil.getPersonId();
        } else {
            personId = Long.parseLong(id);
        }
        Person person = personService.get(personId);
        return AjaxResult.success(person);
    }

    @GetMapping("/scanCodeGet")
    @ApiOperation(value = "扫码人员详情")
    public R<Object> scanCodeGet(@RequestParam Long id) {
        Person person = personService.get(id);
        PersonDetail result = personStatisticsService.summary(person);
        if (!SecurityUtil.isAdmin()) {
            SimplePerson sp = new SimplePerson();
            BeanUtil.copyProperties(result, sp);
            sp.setGroupedData(sp.filteredGroupedData());
            return R.ok(sp);
        }
        return R.ok(result);
    }

    @ApiOperation(value = "学习情况")
    @GetMapping("/graduateRecord")
    public R<StudyRecordExportVo> exportStudyRecord(@RequestParam Long personId, @RequestParam Long projectId) {
        Person person = personService.getById(personId);
        StudyRecordExportVo result = personStatisticsService.buildStudyRecordVo(projectId, person);
        return R.ok(result);
    }

    @ApiOperation(value = "获取人员二维码")
    @GetMapping("/getQRCode")
    public R<String> getQRCode(@RequestParam Long id) throws WxErrorException {
        return R.ok("人员二维码", personService.getQRCode(id));
    }

    @GetMapping("/transferOrg")
    @PreAuthorize("@ss.hasPermi('person:transfer')")
    @ApiOperation(value = "人员转移部门")
    @Log(title = "人员转移部门", businessType = BusinessType.UPDATE)
    public AjaxResult<?> transferOrg(@RequestParam String ids, @RequestParam Long targetOrgId) {
        log.info("XmPersonController - transferOrg ids:{},targetOrgId:{}", ids, targetOrgId);
        return toAjax(personService.transferOrg(ids, targetOrgId));
    }

    @PostMapping("/setOrg")
    @Log(title = "人员设置单位", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "人员设置单位")
    public AjaxResult<?> add(@RequestParam Long targetOrgId) {
        if (PERSON_ORG_ID.equals(targetOrgId)) {
            throw new CustomException("不能设置个人单位");
        }
        Long personId = SecurityUtil.getPersonId();
        Person person = personService.changeOrg(personId, targetOrgId);
        personService.updateById(person);
        return AjaxResult.success();
    }

    @GetMapping("/quit")
    @PreAuthorize("@ss.hasPermi('person:quit')")
    @ApiOperation(value = "离职")
    @Log(title = "离职", businessType = BusinessType.UPDATE)
    public AjaxResult<?> quit(@RequestParam String ids) {
        return toAjax(personService.quit(ids));
    }

    @GetMapping("/rejoin")
    @PreAuthorize("@ss.hasPermi('person:rejoin')")
    @ApiOperation(value = "重新入职")
    @Log(title = "重新入职", businessType = BusinessType.UPDATE)
    public AjaxResult<?> rejoin(@RequestParam String ids) {
        return toAjax(personService.rejoin(ids));
    }

    // 重置密码
    @GetMapping("/resetPwd")
    @PreAuthorize("@ss.hasPermi('person:resetPwd')")
    @ApiOperation(value = "重置密码")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    public AjaxResult<?> resetPwd(@RequestParam Long id) {
        log.info("XmPersonController - resetPwd id:{}", id);

        return toAjax(personService.resetPwd(id));
    }

    @GetMapping("/setAdmin")
    @PreAuthorize("@ss.hasPermi('person:setAdmin')")
    @ApiOperation(value = "设置管理员")
    @Log(title = "设置管理员", businessType = BusinessType.UPDATE)
    public AjaxResult<?> setAdmin(@RequestParam String ids) {
        log.info("XmPersonController - setAdmin ids:{}", ids);
        personService.setAdmin(ids);
        return AjaxResult.success();
    }

    @GetMapping("/cancelAdmin")
    @PreAuthorize("@ss.hasPermi('person:setAdmin')")
    @ApiOperation(value = "取消管理员")
    @Log(title = "取消管理员", businessType = BusinessType.UPDATE)
    public AjaxResult<?> cancelAdmin(@RequestParam Long id) {
        personService.cancelAdmin(id);
        return AjaxResult.success();
    }

    @GetMapping("/exportList")
    @PreAuthorize("@ss.hasPermi('person:export')")
    @ApiOperation(value = "导出人员")
    @Log(title = "导出人员", businessType = BusinessType.EXPORT)
    public AjaxResult<?> export(PersonListQueryRequest req) {
        List<Person> personList = personService.findList(req);
        List<PersonListResult> list = personService.packPersonResult(personList);
        ExcelUtil<PersonListResult> excelUtil = new ExcelUtil<>(PersonListResult.class);
        return excelUtil.exportExcel(list, "人员列表");
    }

    @PostMapping("/importList")
    @ApiOperation(value = "导入人员")
    @PreAuthorize("@ss.hasPermi('person:import')")
    public AjaxResult<?> importList(String fileUrl, Long orgId) {
        personService.importTemplate(fileUrl, orgId);
        return AjaxResult.success("导入成功！");
    }

    @GetMapping("/getFaceId")
    @ApiOperation(value = "人脸核身，上传当前人员人脸头像获取faceId")
    public AjaxResult<?> getFaceId() {
        JSONObject jsonObject = personService.getFaceId(SecurityUtil.getPersonId());
        return AjaxResult.success(jsonObject);
    }

    @PostMapping("/updateFacePhoto")
    @ApiOperation(value = "更新人脸照片")
    public AjaxResult<?> updateFacePhoto(@RequestBody FacePhotoRequest req) {
        Person person = personService.get(SecurityUtil.getPersonId());
        if (ObjectUtil.isNull(person)) {
            return AjaxResult.error("人员id不存在。");
        }

        // 转换URL格式：将腾讯云COS链接转换为静态资源链接
        String facePhotoUrl = req.getFacePhotoUrl();
        if (StringUtils.isNotEmpty(facePhotoUrl) && facePhotoUrl.startsWith("https://safe-edu-1309460949.cos.ap-shanghai.myqcloud.com/")) {
            facePhotoUrl = facePhotoUrl.replace("https://safe-edu-1309460949.cos.ap-shanghai.myqcloud.com/", "https://static.joyfishs.com/");
        }

        //检测头像是否合格
        FaceInfo[] faceInfos = faceAiService.detectFaceAttributes(facePhotoUrl);
        if (ObjectUtil.isNull(faceInfos)) {
            return AjaxResult.error("上传头像照片不合格，请重新拍摄。");
        }
        if (faceInfos.length > 1) {
            return AjaxResult.error("此照片包含多个头像，请重新拍摄只包含自己的照片。");
        }
        FaceInfo face1 = faceInfos[0];
        if (face1.getFaceQualityInfo().getScore() == null || face1.getFaceQualityInfo().getScore() < 70) {
            return AjaxResult.error("上传头像照片不合格，请重新拍摄。");
        }
        float score = faceAiService.detectLiveFaceAccurate(facePhotoUrl);
        if (score < 70) {
            return AjaxResult.error("此照片疑似非真人，请重新拍摄本人照片。");
        }
        personService.updateFacePhoto(person, facePhotoUrl);
        return AjaxResult.success();
    }

    @GetMapping("/updateAvatar")
    @ApiOperation(value = "修改头像")
    public AjaxResult<?> updateAvatar(String avatar) {
        Person person = personService.get(SecurityUtil.getPersonId());
        personService.updateAvatar(person, avatar);
        return AjaxResult.success();
    }

    @PostMapping("/verifiedFace")
    @ApiOperation(value = "验证人脸头像和身份证是否一致")
    public AjaxResult<?> verifiedFace() {
        Person person = personService.get(SecurityUtil.getPersonId());
        String res = personService.verifiedFace(person);
        if (StringUtils.isEmpty(res)) {
            return AjaxResult.success();
        }
        return AjaxResult.error(res);
    }

    @PostMapping("/updateIdCard")
    @ApiOperation(value = "修改身份号和上传身份证图片")
    public AjaxResult<?> updateIdCard(@RequestBody UpdateIdCardParam param) throws IOException {
        personService.updateIdCard(param);
        return AjaxResult.success();
    }

    @PostMapping("/uploadIdCardPhoto")
    @ApiOperation(value = "上传身份证照片并OCR识别")
    public R<IDCardOCRResult> uploadIdCardPhoto(@RequestBody IDCardOCRRequest req) {
        IDCardOCRResponse ocrResp = ocrService.idCardOCR(req.getIdPhotoFaceUrl(), req.getBack());
        if (ObjectUtil.isNull(ocrResp)) {
            return R.fail("照片未检测到身份证，请重新选择");
        }
        JSONObject jsonObj = JSONObject.parseObject(ocrResp.getAdvancedInfo());
        //错误码检测
        String errorMsg = idCardOcrWarnInfos(jsonObj.getJSONArray("WarnInfos"));
        if (StringUtils.isNotEmpty(errorMsg)) {
            return R.fail(errorMsg + "请重新拍摄");
        }
        //图片质量
        if (jsonObj.getIntValue("Quality") < 50 || jsonObj.getIntValue("BorderCodeValue") > 50) {
            return R.fail("照片不合格，请重新拍摄");
        }
        IDCardOCRResult result = new IDCardOCRResult();
        result.setPersonId(SecurityUtil.getPersonId());
        result.setName(ocrResp.getName());
        result.setCardNum(ocrResp.getIdNum());
        UploadResult uploadResult = personService.executeUpload2Base64(jsonObj.getString("IdCard"));
        if (req.getBack()) {
            result.setBackFullImg(uploadResult.getUrl());
        } else {
            result.setFrontFullImg(uploadResult.getUrl());
        }
        return R.ok(result);
    }

    private String idCardOcrWarnInfos(JSONArray warnInfos) {
        if (warnInfos.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < warnInfos.size(); i++) {
            int errorCode = warnInfos.getIntValue(i);
            String errorMsg = getIdCardOcrWarnInfo(errorCode);
            if (StringUtils.isNotNull(errorMsg)) {
                sb.append(errorMsg).append(',');
            }
        }
        return sb.toString();
    }

    private String getIdCardOcrWarnInfo(int errorCode) {
        switch (errorCode) {
            case -9100:
                return "身份证有效日期不合法";
            case -9101:
                return "身份证边框不完整";
            case -9102:
                return "身份证可能是复印件";
            case -9103:
                return "身份证是翻拍";
            case -9105:
                return "身份证框内有遮挡";
            case -9104:
                return "是临时身份证";
            case -9106:
                return "照片有可能会被修改过";
            case -9107:
                return "身份证照片有反光";
        }
        return null;
    }

    @CheckVerify(phone = "newPhone")
    @ApiOperation(value = "修改手机号")
    @GetMapping("/updatePhone")
    public AjaxResult<?> updatePhone(@RequestParam String oldPhone, @RequestParam String newPhone,
                                  @RequestParam String verifyCode, @RequestParam String password) {
        log.info("XmPersonController - updatePhone oldPhone:{},newPhone:{},verifyCode:{},password:{}", oldPhone, newPhone, verifyCode, password);
        personService.updatePhone(oldPhone, newPhone, password);
        return AjaxResult.success();
    }

    @PostMapping("/bindWeiXin")
    @ApiOperation(value = "绑定微信账号")
    public R<Boolean> bindWeixin(@RequestBody BindWeixinParam param) {
        log.info("XmPersonController - bindWeixin:{}", param);
        return R.ok(personService.bindWeixin(SecurityUtil.getPersonId(), param));
    }

    @PostMapping("/setWorkType")
    @ApiOperation(value = "设置工种")
    public R<Boolean> updateWorkType(@RequestParam Long personId, @RequestParam Integer workType) {
        return R.ok(personService.setWorkType(personId, workType));
    }

    @PostMapping("/setJobs")
    @ApiOperation(value = "设置婚姻状态")
    public R<Boolean> updateJobs(@RequestParam Long personId, @RequestParam Integer jobs) {
        return R.ok(personService.setJobs(personId, jobs));
    }

    @PostMapping("/setDegree")
    @ApiOperation(value = "设置学历")
    public R<Boolean> updateDegree(@RequestParam Long personId, @RequestParam Integer degreeId) {
        return R.ok(personService.setDegree(personId, degreeId));
    }

    @PostMapping("/setHomeAddress")
    @ApiOperation(value = "设置家庭住址")
    public R<Boolean> updateHomeAddress(@RequestBody UpdatePersonAddressRequest param) {
        return R.ok(personService.setHomeAddress(param));
    }

    @PostMapping("/setResidenceAddress")
    @ApiOperation(value = "设置户籍地址")
    public R<Boolean> updateResidenceAddress(@RequestBody UpdatePersonAddressRequest param) {
        return R.ok(personService.setResidenceAddress(param));
    }

    @PostMapping("/setBloodType")
    @ApiOperation(value = "设置血型")
    public R<Boolean> updateBloodType(@RequestParam Long personId, @RequestParam String bloodType) {
        return R.ok(personService.setBloodType(personId, bloodType));
    }

    @PostMapping("/setNation")
    @ApiOperation(value = "设置民族")
    public R<Boolean> updateNation(@RequestParam Long personId, @RequestParam String nation) {
        return R.ok(personService.setNation(personId, nation));
    }
}
