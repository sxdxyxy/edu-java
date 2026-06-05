package com.joyfishs.dawa.qualification.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.joyfishs.utils.StringUtils;

/**
 * 资质证件文件上传工具类
 * <p>
 * 处理资质证件图片的上传、验证、存储等功能
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-30
 */
@Component
public class QualificationFileUploadUtil {

    @Value("${file.upload.path:/uploads/qualifications/}")
    private String uploadPath;

    @Value("${file.upload.maxSize:5242880}") // 5MB in bytes
    private long maxFileSize;

    /**
     * 保存上传的资质证件文件
     *
     * @param file 上传的文件
     * @param userId 用户ID
     * @param certType 证件类型
     * @return 保存后的文件路径
     * @throws IOException 文件操作异常
     */
    public String saveQualificationFile(MultipartFile file, Long userId, String certType) throws IOException {
        // 验证文件
        validateFile(file);

        // 创建存储目录
        String subDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String userDir = "user_" + userId;
        String fullDirPath = uploadPath + subDir + "/" + userDir + "/";
        
        File dir = new File(fullDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名
        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String fileName = System.currentTimeMillis() + "_" + userId + "_" + 
                         sanitizeFileName(certType) + "." + extension;
        
        String fullPath = fullDirPath + fileName;
        File destFile = new File(fullPath);

        // 保存文件
        try (FileOutputStream fos = new FileOutputStream(destFile)) {
            fos.write(file.getBytes());
        }

        return fullPath;
    }

    /**
     * 验证上传的文件
     *
     * @param file 上传的文件
     * @throws IllegalArgumentException 验证失败时抛出
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("文件大小不能超过" + (maxFileSize / (1024 * 1024)) + "MB");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("无法确定文件类型");
        }

        if (!isValidImageType(contentType)) {
            throw new IllegalArgumentException("只支持JPG和PNG格式的图片文件");
        }
    }

    /**
     * 检查是否为有效的图片类型
     *
     * @param contentType 内容类型
     * @return 是否为有效图片类型
     */
    private boolean isValidImageType(String contentType) {
        return "image/jpeg".equalsIgnoreCase(contentType) ||
               "image/jpg".equalsIgnoreCase(contentType) ||
               "image/png".equalsIgnoreCase(contentType);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg"; // 默认扩展名
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 清理文件名，移除不安全字符
     *
     * @param fileName 原始文件名
     * @return 清理后的文件名
     */
    private String sanitizeFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "unknown";
        }
        // 移除或替换特殊字符
        return fileName.replaceAll("[^a-zA-Z0-9\\-_]", "_");
    }

    /**
     * 构建访问URL
     *
     * @param storedFilePath 存储的文件路径
     * @return 可访问的URL
     */
    public String buildFileUrl(String storedFilePath) {
        // 实际应用中，这可能会转换为可通过Web访问的URL
        // 这里简化处理，直接返回相对路径或配置的访问路径
        return "/files/qualifications/" + storedFilePath.replace(uploadPath, "");
    }
}