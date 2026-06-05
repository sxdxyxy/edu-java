package com.joyfishs.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SysIndexController {

    /**
     * 访问首页，提示语
     */
    @GetMapping({"/","/index.html"})
    public String index() {
        return "这是后台管理框架，请通过前端地址访问。";
    }
}
