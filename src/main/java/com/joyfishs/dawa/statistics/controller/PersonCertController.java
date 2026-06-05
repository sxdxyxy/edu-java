package com.joyfishs.dawa.statistics.controller;

import java.io.ByteArrayOutputStream;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.statistics.service.PersonStatisticsService;
import com.joyfishs.system.controller.BaseController;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkaifeng
 */
@Slf4j
@Controller
@RequestMapping("/person")
@Api(tags = "个人结业证书")
@RequiredArgsConstructor
public class PersonCertController extends BaseController {
    private final PersonService personService;
    private final PersonStatisticsService personStatisticsService;

    @ApiOperation(value = "结业凭证证书")
    @GetMapping("/passCertificate")
    public ModelAndView passCertificate(@RequestParam Long personId, @RequestParam Long projectId) {
        Person person = personService.getById(personId);
        ModelAndView modelAndView = new ModelAndView("certificate");
        modelAndView.addObject("certificate", personStatisticsService.buildStudyRecordVo(projectId, person));
        modelAndView.addObject("personId", personId);
        modelAndView.addObject("projectId", projectId);
        return modelAndView;
    }

    @ApiOperation(value = "查看学习记录")
    @GetMapping("/studyRecord")
    public ModelAndView exportStudyRecord(@RequestParam Long personId, @RequestParam Long projectId) {
        Person person = personService.getById(personId);
        ModelAndView modelAndView = new ModelAndView("studyRecord");
        modelAndView.addObject("record", personStatisticsService.buildStudyRecordVo(projectId, person));
        return modelAndView;
    }

    @GetMapping(value = "/getPassCertificateQRCode",produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation(value = "结业凭证二维码")
    @ResponseBody
    public byte[] getGraduationCert(@RequestParam Long personId, @RequestParam Long projectId, @RequestParam(required = false) Integer size) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        if(ObjUtil.isNull(size)){
            size = 100;
        }
        QrCodeUtil.generate("https://sts.joyfishs.com/api/person/passCertificate?personId=" + personId + "&projectId=" + projectId, size, size, ImgUtil.IMAGE_TYPE_JPG, result);
        return result.toByteArray();
    }

    @GetMapping(value = "/getStudyRecordQRCode",produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation(value = "结业凭证二维码")
    @ResponseBody
    public byte[] getStudyRecordQRCode(@RequestParam Long personId, @RequestParam Long projectId, @RequestParam(required = false) Integer size) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        if(ObjUtil.isNull(size)){
            size = 100;
        }
        QrCodeUtil.generate("https://sts.joyfishs.com/api/person/studyRecord?personId=" + personId + "&projectId=" + projectId, size, size, ImgUtil.IMAGE_TYPE_JPG, result);
        return result.toByteArray();
    }
}
