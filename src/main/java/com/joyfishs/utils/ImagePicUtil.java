package com.joyfishs.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.lang.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImagePicUtil {
    // 缩放至的宽度
    public static int zoomWidth = 1024;
    // 缩略图的宽度
    public static int thumbnailWidth = 400;

    /**
     * 图片缩放
     * @param filePath
     * @param width
     * @param fileName
     * @return
     */
    public static MultipartFile getImagePic(String filePath, int width, String fileName){
        log.info("ImagePicUtil - getImagePic filePath:{}", filePath);
        log.info("ImagePicUtil - getImagePic width:{}", width);
        log.info("ImagePicUtil - getImagePic fileName:{}", fileName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Image image = ImageIO.read(new File(filePath));

            int image_width = image.getWidth(null);
            log.info("ImagePicUtil - getImagePic image_width:{}", image_width);

            if(image_width <= width){
                log.info("ImagePicUtil - getImagePic not scale");
                return null;
            }

            float scale = Float.parseFloat(String.valueOf(width)) / Float.parseFloat(String.valueOf(image_width));
            log.info("ImagePicUtil - getImagePic scale:{}", scale);

            // 等比缩放图片
            Image scaleImage = ImgUtil.scale(image, scale);

            // Image 转 BufferedImage
            BufferedImage bufferedImage = new BufferedImage(scaleImage.getWidth(null), scaleImage.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
            Graphics g = bufferedImage.getGraphics();
            g.drawImage(scaleImage, 0, 0, null);

            // 获取文件后缀
            String suffix = filePath.substring(filePath.lastIndexOf(".")+1);

            //将newImage写入字节数组输出流
            ImageIO.write( bufferedImage, suffix, baos );
        } catch (IOException e) {
            log.error("{}", e);
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                log.error("PDFUtil - imgToPdf error {}", e);
            }
        }

        MockMultipartFile mockMultipartFile = new MockMultipartFile(fileName, fileName, "image/jpeg", baos.toByteArray());
        log.info("ImagePicUtil - getImagePic ok ...");

        return mockMultipartFile;
    }

    /**
     * 通过文件路径
     * @param path
     * @return true-支持 false-不支持
     */
    public static boolean checkImageByPath(String path) {
        log.info("ImagePicUtil - checkImageByPath path:{}", path);
        // 文本课件没有封面
        if (Validator.isEmpty(path)) return true;

        // 获取文件后缀，并且转换小写
        String suffix = path.substring(path.lastIndexOf(".")+1).toLowerCase();
        log.info("ImagePicUtil - checkImageByPath suffix:{}", suffix);

        String str = constMap1.get(suffix);
        log.info("ImagePicUtil - checkImageByPath str:{}", str);
        if (Validator.isNotEmpty(str)) return true;

        return false;
    }

    /**
     * 支持的图片
     */
    private static final Map<String, String> constMap1 = new HashMap<String, String>() {
        {
            put("jpg", "jpg");
            put("jpeg", "jpeg");
            put("png", "png");
        }
    };

}
