package com.joyfishs.utils.upload;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.joyfishs.utils.HttpServletUtil;
import com.joyfishs.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommonUtil {

    public static String downloadPath;

    @Value("${oss.downloadPath}")
    public void setDownloadPath(String downloadPath){
        CommonUtil.downloadPath = downloadPath;
        if(!CommonUtil.downloadPath.endsWith(File.separator)) CommonUtil.downloadPath += File.separator;
    }

    /**
     * 通用文件下载
     *
     * @param fileName
     * @param delete
     */
    public static void download(String fileName, boolean delete){
        log.info("commonUtil - download fileName:{}", fileName);
        log.info("commonUtil - download delete:{}", delete);

        try{
            if (!FileUtils.checkAllowDownload(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = downloadPath + fileName;

            HttpServletResponse response = HttpServletUtil.getResponse();

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());

            if (delete) FileUtils.deleteFile(filePath);
        } catch (Exception e) {
            log.error("下载文件失败 {}", e);
        }
    }
}
