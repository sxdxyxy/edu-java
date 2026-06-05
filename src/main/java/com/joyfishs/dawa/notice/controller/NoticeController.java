package com.joyfishs.dawa.notice.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.notice.domain.bo.MustReadBo;
import com.joyfishs.dawa.notice.domain.bo.NewsBo;
import com.joyfishs.dawa.notice.domain.bo.NoticeBo;
import com.joyfishs.dawa.notice.domain.vo.NoticeVo;
import com.joyfishs.dawa.notice.entity.Notice;
import com.joyfishs.dawa.notice.entity.NoticePerson;
import com.joyfishs.dawa.notice.service.NoticeService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;
import com.joyfishs.utils.page.TableDataInfo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@Api(tags = "新闻，公告，必读文件")
@RequestMapping("/notice")
public class NoticeController extends BaseController {

    private final NoticeService noticeService;

    @PostMapping("/addOrUpdateNews")
  //  @PreAuthorize("@ss.hasPermi('notice:edit')")
    @ApiOperation(value = "新闻资讯新增或修改")
    @Log(title = "新闻资讯新增或修改", businessType = BusinessType.INSERT)
    public AjaxResult<?> addOrUpdateNews(@RequestBody @Validated NewsBo news) {
        return saveOrUpdate(news, 1);
    }

    @PostMapping("/addOrUpdateNotice")
    //@PreAuthorize("@ss.hasPermi('notice:edit')")
    @ApiOperation(value = "通知公告新增或修改")
    @Log(title = "通知公告新增或修改", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody @Validated NoticeBo notice) {
        return saveOrUpdate(notice, 2);
    }

    @PostMapping("/addOrUpdateMustRead")
   // @PreAuthorize("@ss.hasPermi('notice:edit')")
    @ApiOperation(value = "必读文件新增或修改")
    @Log(title = "必读文件新增或修改", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody @Validated MustReadBo notice) {
        return saveOrUpdate(notice, 3);
    }

    private AjaxResult<?> saveOrUpdate(NewsBo news, Integer type) {
        // 如果非即时发布，发布时间小于当前时间，则报错,当前时间给2分钟误差
        if (news.getIsNowPublish() == 0 && news.getPublishTime().isBefore(LocalDateTime.now().plusMinutes(-2))) {
            return AjaxResult.error("发布时间已过，请重新设置发布时间");
        }
        Notice notice = null;
        if (ObjectUtil.isNotNull(news.getId())) {
            notice = noticeService.getById(news.getId());
            // 如果已发布,则不能修改
            if (notice.getStatus() == 1) {
                return AjaxResult.error("已发布状态不能编辑修改");
            }
            notice.setUpdateBy(SecurityUtil.getUserId());
            notice.setUpdateTime(new Date());
        } else {
            notice = new Notice();
            notice.setCreateBy(SecurityUtil.getUserId());
            notice.setCreateTime(new Date());
            notice.setIsDelete(YesOrNoState.NO.getState());
        }
        BeanUtil.copyProperties(news, notice);
        notice.setIsCalculate(0);
        notice.setType(type);
        notice.setCreateOrgId(SecurityUtil.getLoginUser().getPerson().getOrgId());
        if (type == 3) {
            notice.setIsMustRead(1);
            notice.setIsCalculate(1);
            MustReadBo mustRead = (MustReadBo) news;
            notice.setClassHour(mustRead.getClassHour());
        }
        return AjaxResult.success(noticeService.saveOrUpdateNotice(notice));
    }

    @PostMapping("/del")
   // @PreAuthorize("@ss.hasPermi('notice:del')")
    @Log(title = "删除", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除")
    public AjaxResult<?> del(@RequestParam Long id, String deleteReason) {
        if (null == id) throw new CustomException("删除id不能为空");
        return toAjax(noticeService.del(id, deleteReason));
    }

    @PostMapping("/cancel")
  //  @PreAuthorize("@ss.hasPermi('notice:edit')")
    @ApiOperation(value = "撤销发布")
    @Log(title = "撤销发布", businessType = BusinessType.DELETE)
    public AjaxResult<?> cancel(@RequestParam Long id) {
        return toAjax(noticeService.cancel(id));
    }

    @GetMapping("/pageListNotice")
   // @PreAuthorize("@ss.hasPermi('notice:list')")
    @ApiOperation(value = "通知公告分页列表(我创建的)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "标题", required = false)
    })
    public TableDataInfo<NoticeVo> pageListNotice(@RequestParam(required = false) String title) {
        startPage();
        List<NoticeVo> list = noticeService.queryListCreate(SecurityUtil.getUserId(), 2, title);
        return getDataTable(list);
    }

    @GetMapping("/pageListNews")
   // @PreAuthorize("@ss.hasPermi('notice:list')")
    @ApiOperation(value = "新闻资讯分页列表(我创建的)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "标题", required = false)
    })
    public TableDataInfo<NoticeVo> pageListNews(@RequestParam(required = false) String title) {
        startPage();
        List<NoticeVo> list = noticeService.queryListCreate(SecurityUtil.getUserId(), 1, title);
        return getDataTable(list);
    }

    @GetMapping("/pageListMustRead")
   // @PreAuthorize("@ss.hasPermi('notice:list')")
    @ApiOperation(value = "必读文件分页列表(我创建的)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "标题", required = false)
    })
    public TableDataInfo<NoticeVo> pageListMustRead(@RequestParam(required = false) String title) {
        startPage();
        List<NoticeVo> list = noticeService.queryListCreate(SecurityUtil.getUserId(), 3, title);
        return getDataTable(list);
    }

    // 公告详情
    @GetMapping("/get")
    @ApiOperation(value = "查询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true),
            @ApiImplicitParam(name = "isEdit", value = "是否用来编辑： 1-是  0-否，区别是给浏览量加1，用来编辑就不会改变浏览量计数", required = true)
    })
    public R<NoticeVo> get(@RequestParam Long id, @RequestParam Integer isEdit) {
        NoticeVo notice = noticeService.get(id, isEdit);
        return R.ok(notice);
    }

    @GetMapping("/read")
    @ApiOperation(value = "设置指定id为已读")
    public AjaxResult<?> read(@RequestParam Long id) {
        if (!SecurityUtil.isLogin()) {
            return AjaxResult.success();
        }
        Long userId = SecurityUtil.getUserId();
        Long personId = SecurityUtil.getPersonId();
        return AjaxResult.success(noticeService.executeRead(id,userId,personId));
    }

    @PostMapping("/pagePersonList")
   // @PreAuthorize("@ss.hasPermi('notice:edit')")
    @ApiOperation(value = "查询已读或未读人员列表")
    public TableDataInfo<NoticePerson>  pagePersonList(@RequestParam Long noticeId, @RequestParam Integer isRead) {
        startPage();
        List<NoticePerson> list = noticeService.queryPersonList(noticeId, isRead);
        return getDataTable(list);
    }
}
