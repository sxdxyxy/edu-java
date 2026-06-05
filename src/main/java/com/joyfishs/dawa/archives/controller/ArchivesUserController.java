package com.joyfishs.dawa.archives.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.archives.domain.*;
import com.joyfishs.dawa.archives.service.ArchivesUserService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.signature.domain.vo.OnePersonOneArchivesVo;
import com.joyfishs.dawa.signature.entity.OnePersonOneArchives;
import com.joyfishs.dawa.signature.service.OnePersonOneArchivesService;
import com.joyfishs.dawa.signature.service.SignedDocumentService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.exception.CustomException;
import com.joyfishs.utils.page.TableDataInfo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 人员档案
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/archives/user")
public class ArchivesUserController extends BaseController {
    private final ArchivesUserService archivesUserService;
    private final PersonService personService;
    private final OnePersonOneArchivesService onePersonOneArchivesService;
    private final SignedDocumentService signedDocumentService;

    @ApiOperation(value = "人员档案列表")
    @GetMapping("/listPage")
    public TableDataInfo<?> listPage(
            @RequestParam(value = "orgId", required = false, defaultValue = "") Long orgId,
            @RequestParam(value = "projectId", required = false, defaultValue = "") Long projectId,
            @RequestParam(value = "name", required = false, defaultValue = "") String name) {
        startPage();
        List<ArchivesUser> list = archivesUserService.listPage(orgId, projectId, name);
        return getDataTable(list);
    }

    @ApiOperation(value = "自主培训列表")
    @GetMapping("/autoTrainPageList")
    public TableDataInfo<?> autoTrainPageList(
            @RequestParam(value = "personId", required = false, defaultValue = "") Long personId,
            @RequestParam(value = "projectName", required = false, defaultValue = "") String projectName,
            @RequestParam(value = "finishState", required = false, defaultValue = "") Integer finishState) {
        startPage();
        List<ArchivesUserAutoTrain> list = archivesUserService.autoTrainPageList(personId, projectName, finishState);
        return getDataTable(list);
    }

    @ApiOperation(value = "培训详情列表")
    @GetMapping("/autoTrainDetailPageList")
    public TableDataInfo<?> autoTrainDetailPageList(
            @RequestParam(value = "personId", required = false, defaultValue = "") Long personId,
            @RequestParam(value = "projectId", required = false, defaultValue = "") Long projectId,
            @RequestParam(value = "courseName", required = false, defaultValue = "") String courseName) {
        startPage();
        List<ArchivesUserAutoTrainDetail> list = archivesUserService.autoTrainDetailPageList(personId, projectId, courseName);
        return getDataTable(list);
    }
    @ApiOperation(value = "答题记录列表")
    @GetMapping("/autoTrainAnswerPageList")
    public TableDataInfo<?> autoTrainAnswerPageList(
            @RequestParam(value = "personId", required = false, defaultValue = "") Long personId,
            @RequestParam(value = "projectId", required = false, defaultValue = "") Long projectId) {
        startPage();
        List<ArchivesUserAutoTrainAnswer> list = archivesUserService.autoTrainAnswerPageList(personId, projectId);
        return getDataTable(list);
    }

    @ApiOperation(value = "终端培训列表")
    @GetMapping("/terminalTrainPageList")
    public TableDataInfo<?> terminalTrainPageList(
            @RequestParam(value = "personId", required = false, defaultValue = "") Long personId,
            @RequestParam(value = "projectName", required = false, defaultValue = "") String projectName) {
        startPage();
        List<ArchivesUserTerminalTrain> list = archivesUserService.terminalTrainPageList(personId, projectName);
        return getDataTable(list);
    }

    @GetMapping("/terminalTrainDetail")
    public AjaxResult<?> terminalTrainDetail(
            @RequestParam(value = "personId", required = false, defaultValue = "") Long personId,
            @RequestParam(value = "projectId", required = false, defaultValue = "") Long projectId) {
        ArchivesUserTerminalTrainDetail archivesUserTerminalTrainDetail = archivesUserService.terminalTrainDetail(personId, projectId);
        return AjaxResult.success(archivesUserTerminalTrainDetail);
    }

    @GetMapping("/selfStudyPageList")
    public TableDataInfo<?> selfStudyPageList(
            @RequestParam(value = "personId", required = false, defaultValue = "") Long personId,
            @RequestParam(value = "courseName", required = false, defaultValue = "") String courseName) {
        List<ArchivesUserSelfStudy> list = archivesUserService.selfStudyPageList(personId, courseName);
        return getDataTable(list);
    }

    @GetMapping("/listSignedDocument")
    public AjaxResult<?> signedDocumentList(
            @RequestParam(value = "personId", required = true) Long personId,
            @RequestParam(value = "projectId", required = false) Long projectId) {
        Person person = personService.getById(personId);
        if (ObjectUtil.isNull(person.getWorkType())) {
            throw new CustomException("还没有设置工种，请先设置基准工种脉络。");
        }
        OnePersonOneArchivesVo result = signedDocumentService.queryList(person);
        return AjaxResult.success(result);
    }

    /**
     * 构建压缩包
     * @param archiveId
     * @return
     */
    @GetMapping("/archivesCompressed")
    public AjaxResult<?> archivesCompressed(@RequestParam(value = "archiveId") Long archiveId,
                                                        @RequestParam(value = "projectName") String projectName,
                                                        @RequestParam(value = "teamName") String teamName,
                                                        @RequestParam(value = "businessTransactionDate", required = false) java.util.Date businessTransactionDate) {
        OnePersonOneArchives archive = onePersonOneArchivesService.getById(archiveId);
        //封面目录生成
        if (StrUtil.isEmpty(archive.getCatalogueName())) {
            archive.setProjectName(projectName);
            archive.setTeamName(teamName);
            archive.setBusinessTransactionDate(businessTransactionDate);
            signedDocumentService.createCatalogue(archive);
        }
        //压缩文件生成
        if (StrUtil.isEmpty(archive.getFileName())) {
            signedDocumentService.archiveCompress(archive);
        }//同步压缩任务状态
        if(!archive.getJobComplete()){
            signedDocumentService.syncCompressedJob(archive);
        }
        OnePersonOneArchivesVo result = new OnePersonOneArchivesVo();
        BeanUtil.copyProperties(archive, result);
        return AjaxResult.success(result);
    }
}
