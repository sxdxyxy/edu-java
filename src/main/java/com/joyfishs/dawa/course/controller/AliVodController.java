package com.joyfishs.dawa.course.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.system.domain.R;
import com.yaoan.liveapi.CourseApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "阿里云Vod服务")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AliVodController {

    public final static String AppId = "afaf8a8adc5afbe5";

    @ApiOperation(value = "获取播放器播放令牌")
    @GetMapping("/getPlayToken")
    public R<String> getPlayToken() throws Exception {
        return R.ok("success",CourseApi.build(AppId).getToken());
    }
}
