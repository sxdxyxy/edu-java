package com.joyfishs.oss.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.joyfishs.oss.entity.SysOss;
import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import com.joyfishs.utils.page.TableDataInfo;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "文件上传下载")
@Slf4j
@RestController
@RequestMapping("sys/oss")
public class SysOssController extends BaseController {

    @Autowired
    private SysOssService sysOssService;

    @ApiOperation(value = "文件分页查询")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sys:oss:list')")
    public TableDataInfo<?> getList(SysOss sysOss) {
        startPage();
        List<SysOss> list = sysOssService.queryList(sysOss);
        return getDataTable(list);
    }

    /**
     * 上传文件
     *
     * @param file file
     * @return RestResponse
     */
    @ApiOperation(value = "上传文件")
    @PostMapping("/upload")
    @PreAuthorize("@ss.hasPermi('sys:oss:edit')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "要上传的文件", required = true),
            @ApiImplicitParam(name = "sourceId", value = "分类文件夹名", required = false)
    })
    public AjaxResult<?> upload(@RequestParam(value = "file", required = true) MultipartFile file,
                         @RequestParam(value = "sourceId", required = false) String sourceId) throws IOException {
        if (null == file || file.isEmpty()) {
            throw new CustomException("上传文件不能为空");
        }
        return sysOssService.upload(file, sourceId);
    }

    @ApiOperation(value = "上传图片")
    @PostMapping("/uploadImg")
    @PreAuthorize("@ss.hasPermi('sys:oss:edit')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "要上传的图片文件", required = true),
            @ApiImplicitParam(name = "sourceId", value = "分类文件夹名", required = false)
    })
    public AjaxResult<?> uploadImg(@RequestParam(value = "file", required = true) MultipartFile file,
                         @RequestParam(value = "sourceId", required = false) String sourceId) throws IOException {
        if (null == file || file.isEmpty()) {
            throw new CustomException("上传文件不能为空");
        }
        return sysOssService.upload(file, sourceId);
    }

    @ApiOperation(value = "文件上传-图片Base64")
    @PostMapping("/upload/base64")
    public AjaxResult<?> uploadBase64(@RequestBody String base64) throws IOException {
        if(StringUtils.isEmpty(base64)) {
            throw new CustomException("上传文件Base64不能为空");
        }
        base64 = base64.substring(base64.indexOf(",")+1);
        byte[] data = Base64.decodeBase64(base64);
        String fileName = String.format("%s.%s", IdUtil.simpleUUID(), "png") ;
        MultipartFile file = new MockMultipartFile(fileName, fileName, "application/x-png", data);
        return sysOssService.upload(file, "base64");
    }

//
//    @GetMapping("/presigned-url")
//    @ApiOperation(value = "获取文件预签名地址", notes = "模式二：前端上传文件：用于前端直接上传")
//    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(@RequestParam("path") String path) throws Exception {
//        return success(fileService.getFilePresignedUrl(path));
//    }
//
//    @PostMapping("/create")
//    @ApiOperation(value = "创建文件", notes = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
//    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
//        return success(fileService.createFile(createReqVO));
//    }

    @ApiOperation(value = "删除上传文件")
    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermi('sys:oss:edit')")
    @Log(title = "文件删除", businessType = BusinessType.DELETE)
    public AjaxResult<?> delete(@RequestBody Integer[] ids) {
        log.info("SysOssController - delete ids:{}", (Object) ids);
        sysOssService.delete(ids);
        return AjaxResult.success();
    }

    @ApiOperation(value = "根据文件id下载文件")
    @GetMapping(value = "/download")
    public void download(@RequestParam Long id, HttpServletResponse response){
        sysOssService.download(id,response);
    }

    @ApiOperation(value = "获取UniApp客户端文件直传腾讯云COS签名")
    @GetMapping(value = "/getPostPolicy")
    public R<Map<String, String>> getPostPolicy(@RequestParam(value = "path", required = true) String path, @RequestParam(value = "extName", required = true) String extName) {
        return R.ok(sysOssService.getPostPolicy(path, extName));
    }
}
