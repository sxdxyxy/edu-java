package com.joyfishs.dawa.project.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.message.service.MessageService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.project.domain.vo.ProjectList;
import com.joyfishs.dawa.project.entity.ProjectAttachments;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrain;
import com.joyfishs.dawa.project.service.ProjectAttachmentsService;
import com.joyfishs.dawa.project.service.ProjectTerminalTrainService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.page.TableDataInfo;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "终端培训")
@RequestMapping("/project/terminal")
public class ProjectTerminalTrainController extends BaseController {

    private final ProjectTerminalTrainService terminalTrainService;

    private final ProjectAttachmentsService projectAttachmentsService;

    private final PersonService personService;

    private final MessageService messageService;

    @PostMapping("/addOrUpdate")
    @PreAuthorize("@ss.hasPermi('terminal:train:edit')")
    @ApiOperation(value = "新增终端培训")
    public AjaxResult<?> addOrUpdate(@RequestBody @Validated ProjectTerminalTrain terminalTrain) {
        Long id = terminalTrain.getId();
        if (id == null) {  //新增的
            Long userId = SecurityUtil.getUserId();
            Person person = personService.getByUserId(userId);
            Long orgId = null;
            if (userId != 1) {
                if (person == null || person.getOrgId() == null) {
                    orgId = 0L;
                } else {
                    orgId = person.getOrgId();
                }
            } else {
                orgId = 0L;
            }

            terminalTrain.setOrgId(orgId);
            terminalTrain.setCreateBy(SecurityUtil.getUserId());
            terminalTrain.setCreateTime(new Date());
            terminalTrain.setIsDelete(YesOrNoState.NO.getState());
            //先保存一遍，获得id
            terminalTrainService.save(terminalTrain);

            //发布之后发送消息
            if (terminalTrain.getStatus() > 1) {
                messageService.sendMsgByOrgId(orgId, DateUtil.date(), 5, terminalTrain.getId(), terminalTrain.getTrainName());
            }
        }
        if (id != null) {  //更新
            terminalTrain.setUpdateBy(SecurityUtil.getUserId());
            terminalTrain.setUpdateTime(DateUtil.date());
        }
        //生成二维码后更新进去
        String s = "{\"type\":1,\"id\":" + terminalTrain.getId() + ",\"name\":\"" + terminalTrain.getTrainName() + "\"}";
        terminalTrain.setSignCode(s);
        return toAjax(terminalTrainService.updateById(terminalTrain));
    }

    @DeleteMapping("/del")
    @PreAuthorize("@ss.hasPermi('terminal:train:del')")
    @ApiOperation(value = "删除终端培训")
    public AjaxResult<?> del(@RequestParam String ids) {
        if (StringUtils.isEmpty(ids)) {
            return AjaxResult.success();
        }
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        terminalTrainService.del(idList);
        return toAjax(true);
    }

    @GetMapping("/pageList")
    @PreAuthorize("@ss.hasPermi('terminal:train:list')")
    @Log(title = "", businessType = BusinessType.SELECT)
    @ApiOperation(value = "分页查终端培训列表")
    public TableDataInfo<?> pageList(@RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate,
                                  @RequestParam(required = false) Long projectId) {

        terminalTrainService.refreshTrainStatus();

        Date sd = null;
        Date ed = null;
        if (startDate != null && !startDate.isEmpty()) {
            sd = DateUtil.parse(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            ed = DateUtil.parse(endDate);
        }
        startPage();
        List<ProjectList> list = terminalTrainService.findList(status, name, sd, ed, projectId);
        return getDataTable(list);
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermi('terminal:train:list')")
    @ApiOperation(value = "根据id查终端培训记录")
    public AjaxResult<?> get(@RequestParam Integer id) {
        ProjectTerminalTrain train = terminalTrainService.getById(id);
        if (train == null) {
            return AjaxResult.error("项目未找到，请核对");
        }
        return AjaxResult.success(train);
    }

    @GetMapping("/getAttachments")
    @PreAuthorize("@ss.hasPermi('terminal:train:list')")
    @ApiOperation(value = "根据终端培训记录id查附件")
    public AjaxResult<?> getAttachments(@RequestParam Long id) {
        List<ProjectAttachments> attachments = projectAttachmentsService.findList(id);
        return AjaxResult.success(attachments);
    }

    @PostMapping("/addAttachment")
    @PreAuthorize("@ss.hasPermi('terminal:train:edit')")
    @ApiOperation(value = "添加附件")
    public AjaxResult<?> addAttachment(@RequestBody @Validated ProjectAttachments attachment) {
        return toAjax(projectAttachmentsService.saveBy(attachment));
    }

    @DeleteMapping("/delAttachment/{id}")
    @PreAuthorize("@ss.hasPermi('terminal:train:edit')")
    @ApiOperation(value = "删除附件")
    public AjaxResult<?> delAttachment(@PathVariable Long id) {
        return toAjax(projectAttachmentsService.removeById(id));
    }

    @PutMapping("/release")
    @PreAuthorize("@ss.hasPermi('terminal:train:release')")
    @ApiOperation(value = "发布终端培训")
    public AjaxResult<?> release(@RequestParam Long id) {
        boolean bl = terminalTrainService.release(id);
        return toAjax(bl);
    }
}
