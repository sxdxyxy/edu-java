package com.joyfishs.dawa.message.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.message.entity.Message;
import com.joyfishs.dawa.message.service.MessageService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */
@Slf4j
@RestController
@RequestMapping("/message")
@Api(tags = "消息通知")
public class MessageController extends BaseController {

    @Autowired
    MessageService messageService;

    @GetMapping("/receive")
    @ApiOperation(value = "消息设置已读")
    public AjaxResult<?> receive(@RequestParam Long id) {
        return AjaxResult.success(messageService.executeReceive(id));
    }

    @GetMapping("/pageList")
    @ApiOperation(value = "分页消息列表")
    public TableDataInfo<Message> pageList() {
        startPage();
        List<Message> list = messageService.queryList();
        return getDataTable(list);
    }

}
