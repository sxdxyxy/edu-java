package com.joyfishs.ueditor.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.ueditor.domain.UeditorConfig;
import com.joyfishs.ueditor.domain.UeditorConfigJson;
import com.joyfishs.utils.AjaxResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("sys/ueditor")
public class UeditorContorller {

    @Autowired
    private SysOssService sysOssService;

    @Log(title = "富文本上传", businessType = BusinessType.INSERT)
    @RequestMapping("/config")
    public Object ueditorConfig(MultipartFile upfile, String action) throws IOException {
        log.info("UeditorContorller - ueditorConfig action:{}", action);

        switch (action) {
            case "config":
                log.info("UeditorContorller - ueditorConfig get config json");
                return new UeditorConfigJson();
            case "uploadimage":
                log.info("UeditorContorller - ueditorConfig uploadimage");

                AjaxResult<?> upload = sysOssService.upload(upfile, "ueditor");

                String url = upload.get("zoomUrl") != null ? upload.get("zoomUrl").toString() : upload.get("url").toString();
                url = String.format("%s?width=1024", url);
                return new UeditorConfig("SUCCESS", url, upfile.getSize(), upfile.getContentType(), upfile.getOriginalFilename(), upfile.getOriginalFilename());
        }
        return "请求失败";
    }

}
