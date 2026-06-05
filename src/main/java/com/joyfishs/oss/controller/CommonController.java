package com.joyfishs.oss.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.upload.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("common")
public class CommonController extends BaseController {

    /**
     * 通用下载接口
     *
     * @param fileName
     */
    @GetMapping("/download")
    public void fileDownload(String fileName){
        log.info("commonController - fileDownload fileName:{}", fileName);

        CommonUtil.download(fileName, true);
    }


}
