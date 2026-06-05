package com.joyfishs.dawa.banner.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.banner.domain.vo.BannerVo;
import com.joyfishs.dawa.banner.entity.Banner;
import com.joyfishs.dawa.banner.service.BannerService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.page.TableDataInfo;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 轮播图
 *
 * @author ykf
 * @date 2023-06-09
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@Api(tags = "轮播图")
@RequestMapping("/banner")
public class BannerController extends BaseController {

    private final BannerService bannerService;

    @ApiOperation(value = "列表查询")
    @GetMapping("/list")
    public TableDataInfo<Banner> list() {
        startPage();
        List<Banner> result = bannerService.queryPageList();
        return getDataTable(result);
    }

    /**
     * @param bannerId 主键
     */
    @ApiOperation(value = "获取详情")
    @GetMapping("/{bannerId}")
    public R<BannerVo> getInfo(@NotNull(message = "id不能为空") @PathVariable Long bannerId) {
        return R.ok(bannerService.selectVoById(bannerId));
    }

    @ApiOperation(value = "新增保存")
    @Log(title = "轮播图", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated @RequestBody Banner bo) {
        bo.setCreateBy(SecurityUtil.getUserId());
        bo.setCreateTime(new Date());
        if (ObjectUtil.isNull(bo.getStartTime())) {
            bo.setStartTime(LocalDateTime.now());
        }
        return toAjax2(bannerService.save(bo));
    }

    @ApiOperation(value = "修改保存")
    @Log(title = "轮播图", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated @RequestBody Banner bo) {
        bo.setUpdateBy(SecurityUtil.getUserId());
        bo.setUpdateTime(new Date());
        return toAjax2(bannerService.updateById(bo));
    }

    @ApiOperation(value = "删除")
    @Log(title = "轮播图", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bannerId}")
    public R<Void> remove(@NotNull(message = "id不能为空") @PathVariable Long bannerId) {
        return toAjax2(bannerService.del(bannerId));
    }
}
