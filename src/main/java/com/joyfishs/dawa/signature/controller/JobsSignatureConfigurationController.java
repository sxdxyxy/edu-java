package com.joyfishs.dawa.signature.controller;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import com.joyfishs.dawa.signature.domain.bo.JobsSignatureConfigurationBo;
import com.joyfishs.dawa.signature.domain.vo.JobsSignatureConfigurationVo;
import com.joyfishs.dawa.signature.domain.vo.TemplateVariableVo;
import com.joyfishs.dawa.signature.entity.JobsSignatureConfiguration;
import com.joyfishs.dawa.signature.service.JobsSignatureConfigurationService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.SecurityUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykf
 * @date 2023-06-27
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@Api(tags = "岗位签名配置")
@RequestMapping("/onePersonOneArchives/jobsConfiguration")
public class JobsSignatureConfigurationController extends BaseController {

    private final JobsSignatureConfigurationService jobsSignatureConfigurationService;

    @ApiOperation(value = "列表查询")
    @GetMapping("/list")
    public R<List<JobsSignatureConfigurationVo>> list(Long orgId, Integer workType, Long projectId) {
        if (ObjectUtil.isNull(orgId)) {
            orgId = SecurityUtil.getOrgId();
        }
        List<JobsSignatureConfigurationVo> result = jobsSignatureConfigurationService.queryList(orgId, workType, projectId);
        return R.ok(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{configurationId}")
    public R<JobsSignatureConfigurationVo> getInfo(@NotNull(message = "id不能为空") @PathVariable Long configurationId) {
        return R.ok(jobsSignatureConfigurationService.selectVoById(configurationId));
    }

    @ApiOperation(value = "新增保存")
    @Log(title = "岗位签名配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated @RequestBody JobsSignatureConfigurationBo bo) {
        JobsSignatureConfiguration configuration = new JobsSignatureConfiguration();
        BeanUtil.copyProperties(bo, configuration);
        configuration.setCreateBy(SecurityUtil.getUserId());
        configuration.setCreateTime(new Date());
        return toAjax2(jobsSignatureConfigurationService.save(configuration));
    }

    @ApiOperation(value = "修改保存")
    @Log(title = "岗位签名配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated @RequestBody JobsSignatureConfigurationBo bo) {
        JobsSignatureConfiguration configuration = new JobsSignatureConfiguration();
        BeanUtil.copyProperties(bo, configuration);
        configuration.setUpdateBy(SecurityUtil.getUserId());
        configuration.setUpdateTime(new Date());
        return toAjax2(jobsSignatureConfigurationService.updateById(configuration));
    }

    @ApiOperation(value = "删除")
    @Log(title = "岗位签名配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configurationId}")
    public R<Void> remove(@NotNull(message = "id不能为空") @PathVariable Long configurationId) {
        return toAjax2(jobsSignatureConfigurationService.del(configurationId));
    }

    @ApiOperation(value = "文档模板可用变量")
    @GetMapping("/templateVariableList")
    public R<List<TemplateVariableVo>> templateVariableList() {
        List<TemplateVariableVo> result = Lists.newArrayList();
        result.add(new TemplateVariableVo("{{orgName}}", "单位名称", "三峡发展有限公司"));
        result.add(new TemplateVariableVo("{{projectName}}", "工程名称", "宜昌市主城区污水厂网、生态水网共建项目二期PPP工程"));
        result.add(new TemplateVariableVo("{{teamName}}", "班组名称(作业队)", "xxx施工队"));
        result.add(new TemplateVariableVo("{{personName}}", "姓名", "张三"));
        result.add(new TemplateVariableVo("{{sex}}", "性别", "女"));
        result.add(new TemplateVariableVo("{{age}}", "年龄", "32"));
        result.add(new TemplateVariableVo("{{nation}}", "民族", "汉"));
        result.add(new TemplateVariableVo("{{degree}}", "学历-文化程度", "高中"));
        result.add(new TemplateVariableVo("{{phone}}", "联系电话", "18812345678"));
        result.add(new TemplateVariableVo("{{idCardNo}}", "身份证号码", "420503200801305260"));
        result.add(new TemplateVariableVo("{{homeAddress}}", "家庭住址", "湖北省宜昌市西陵区xx路xx号"));
        result.add(new TemplateVariableVo("{{emergencyContact}}", "家庭联系人", "金凤"));
        result.add(new TemplateVariableVo("{{emergencyContactPhone}}", "家庭联系人电话", "18812345678"));
        result.add(new TemplateVariableVo("{{entryTime}}", "进场时间", "2023年7月18日"));
        result.add(new TemplateVariableVo("{{@signImage}}", "签名图片", ""));
        result.add(new TemplateVariableVo("{{signDate}}", "签名时间", "2023年7月18日"));
        return R.ok(result);
    }
}
