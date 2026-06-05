package com.joyfishs.utils;

import java.io.IOException;
import java.util.UUID;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.extra.qrcode.QrConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: dawa-java
 * @description: 二维码生成工具类
 * @author: Yjhon
 * @create: 2021-08-30 11:15
 */
@Slf4j
public class QrCodeUtil {
	public static String generateQrCode(String str){
		try {
			SysOssService ossService = SpringUtil.getBean(SysOssService.class);

			QrConfig qrConfig = new QrConfig();
			qrConfig.setHeight(200);
			qrConfig.setWidth(200);
			byte[] bytes = cn.hutool.extra.qrcode.QrCodeUtil.generatePng(str, qrConfig);

			String fileName = UUID.randomUUID() +".png";

			MultipartFile multipartFile = new MockMultipartFile(fileName,fileName,"png",bytes);

			AjaxResult<?> upload = ossService.upload(multipartFile,"common_qr_code");
			log.info("QrCodeUtil - addCode upload:{}", upload);

			String url = (String)upload.get("url");
			return url;

		} catch (IOException e) {
			throw new CustomException("生成二维码异常");
		}
	}
}
