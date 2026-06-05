package com.joyfishs.oss.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.signature.entity.SignedDocument;
import com.joyfishs.oss.domain.UploadResult;
import com.joyfishs.oss.entity.SysOss;
import com.joyfishs.oss.mapper.SysOssMapper;
import com.joyfishs.system.config.CosConfig;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.job.*;
import com.qcloud.cos.model.ciModel.common.MediaOutputObject;
import com.qcloud.cos.utils.IOUtils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysOssService extends ServiceImpl<SysOssMapper, SysOss> {
    @Resource
    private CosConfig cosConfig;
    @Resource
    private COSClient cosClient;
    /**
     * 分页查询
     *
     * @param sysOss
     * @return
     */
    public List<SysOss> queryList(SysOss sysOss){
        LambdaQueryWrapper<SysOss> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(sysOss.getFileName())) {
            queryWrapper.like(SysOss::getFileName, sysOss.getFileName());
        }
        return list(queryWrapper);
    }

    /**
     * 删除文件上传记录
     *
     * @param ids
     */
    public void delete(Integer[] ids){
        for (Integer id : ids) {
            try {
                SysOss oss = getById(id);
                cosClient.deleteObject(cosConfig.getBucketName(),oss.getNewName());
            } catch (Exception err) {
                log.error("删除文件出现错误:" + id, err);
            }
        }
        removeByIds(Arrays.asList(ids));
    }

    /**
     * 根据文件全路径数组删除上传文件
     *
     * @param urls
     */
    public void deleteByUrl(String[] urls){
        for (String url : urls) {
            try {
                LambdaQueryWrapper<SysOss> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysOss::getUrl, url);
                SysOss sysOss = getOne(queryWrapper);
                cosClient.deleteObject(cosConfig.getBucketName(),sysOss.getNewName());
                removeById(sysOss.getId());
            } catch (Exception err) {
                log.error("删除文件出现错误:" + url, err);
            }
        }
    }

    public void download(Long id, HttpServletResponse response) {
        SysOss oss = getById(id);
        if (oss == null || StringUtils.isEmpty(oss.getId())) {
            return;
        }
        String fileName = oss.getNewName();
        log.info("SysOssService - show fileName:{}", fileName);
        COSObjectInputStream cosObjectInput = null;
        ServletOutputStream outputStream = null;
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(cosConfig.getBucketName(), fileName);
            COSObject cosObject = cosClient.getObject(getObjectRequest);
            cosObjectInput = cosObject.getObjectContent();
            ObjectMetadata objectMetadata = cosObject.getObjectMetadata();
            response.setContentType(objectMetadata.getContentType());
            // 根据实际的文件类型找到对应的 contentType
            String contentType = MediaTypeFactory.getMediaType(oss.getFileType()).map(MediaType::toString)
                    .orElse("application/vnd.ms-excel");
            outputStream = response.getOutputStream();
            response.setContentType(contentType);
            response.setCharacterEncoding("utf-8");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
            outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = cosObjectInput.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("发送文件出错：",e);
        } finally {
            IOUtils.closeQuietly(cosObjectInput, log);
            IOUtils.closeQuietly(outputStream, log);
        }
    }

    public File download(String fileKey) {
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(cosConfig.getBucketName(), fileKey);
            File file = FileUtil.createTempFile();
            ObjectMetadata metadata = cosClient.getObject(getObjectRequest,file);
            log.info("下载文件大小{}",metadata.getContentLength());
            return file;
        } catch (Exception e) {
            log.error("下载文件出错：",e);
        }
        return null;
    }

    /**
     * 通过url获取sysOss
     * @param fileUrl
     */
    public SysOss findByUrl(String fileUrl) {
        SysOss sysOss = baseMapper.findByUrl(fileUrl);
        if (sysOss == null || StringUtils.isNull(sysOss.getId())) {
            throw new CustomException("文件未找到！");
        }
        return sysOss;
    }

    /**
     * 通过url获取sysOss
     * @param fileUrl
     */
    public File downloadByUrl(String fileUrl) {
        SysOss sysOss = findByUrl(fileUrl);
//        File tempFile = FileUtil.createTempFile(OnePersonOneArchives.EXCEL,true);
        File tempFile = FileUtil.createTempFile(".xlsx",true);
        HttpUtil.downloadFile(sysOss.getUrl(),tempFile);
        return tempFile;
    }

    public String buildJson(SysOss oss) {
        Map<String, String> jsonMap = new HashMap<String, String>() {{
            put("uid", oss.getId());
            put("name", oss.getFileName());
            put("url", oss.getUrl());
        }};
        return JSONUtil.toJsonStr(jsonMap);
    }

    public String buildFileKey(String path, String extName) {
        StringBuilder sb = new StringBuilder(path);
        sb.append(StrUtil.C_SLASH)
                .append(DateUtil.format(new Date(), "yyyyMMdd"))
                .append(StrUtil.C_SLASH)
                .append(IdUtil.objectId())
                .append(StrUtil.DOT)
                .append(extName);
        return sb.toString();
    }

    public AjaxResult<?> upload(MultipartFile file, String sourceId) throws IOException {
        InputStream is = new BufferedInputStream(file.getInputStream());
        String extName = FileNameUtil.extName(file.getOriginalFilename());
        String fileKey = buildFileKey(StringUtils.isNotEmpty(sourceId) ? sourceId : CosConfig.COMMON, extName);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        //设置流的大小
        objectMetadata.setContentLength(file.getSize());
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), fileKey, is, objectMetadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        if (putObjectResult.getETag() == null) {
            return AjaxResult.error("文件上传到腾讯云出错");
        }
        String originalFilename = FileNameUtil.getName(file.getOriginalFilename());

        // 保存文件信息
        SysOss sysOss = new SysOss();
        sysOss.setSourceId(sourceId);
        sysOss.setFileName(originalFilename);
        sysOss.setNewName(fileKey);
        sysOss.setFileType(extName);
        sysOss.setUrl(cosConfig.getDomain() + fileKey);
        // 富文本上传图片 没带token 增加是否登录判断
        if (SecurityUtil.isLogin()) {
            sysOss.setCreateBy(SecurityUtil.getUserId());
        }
        sysOss.setCreateTime(new Date());
        sysOss.setIsDelete(YesOrNoState.NO.getState());

        save(sysOss);
        // 返回兼容UEditor的参数
        return AjaxResult.success().put("url", sysOss.getUrl())
                .put("name", sysOss.getFileName())
                .put("id", sysOss.getId())
                .put("uid", sysOss.getId())
                .put("newName", sysOss.getNewName())
                .put("createTime", sysOss.getCreateTime());
    }

    public UploadResult upload(File file, String fileKey) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), fileKey, file);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            if (putObjectResult.getETag() == null) {
                throw new CustomException("上传文件到腾讯云失败");
            }
            String originalFilename = FileNameUtil.getName(file);

            SysOss sysOss = new SysOss();
            sysOss.setFileName(originalFilename);
            sysOss.setNewName(fileKey);
            sysOss.setFileType(FileNameUtil.extName(originalFilename));
            sysOss.setUrl(cosConfig.getDomain() + fileKey);
            // 富文本上传图片 没带token 增加是否登录判断
            if (SecurityUtil.isLogin()) {
                sysOss.setCreateBy(SecurityUtil.getUserId());
            }
            sysOss.setCreateTime(new Date());
            sysOss.setIsDelete(YesOrNoState.NO.getState());
            save(sysOss);
            return UploadResult.builder().url(sysOss.getUrl()).fileKey(fileKey).build();
        } catch (Exception e) {
            throw new CustomException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
    }

    public FileProcessJobResponse fileCompress(List<SignedDocument> signedDocs, String archivesFileKey) {
        //1.创建任务请求对象
        FileProcessRequest request = new FileProcessRequest();
        request.setBucketName(cosConfig.getBucketName());
        request.setTag(FileProcessJobType.FileCompress);
        FileCompressConfig fileCompressConfig = request.getOperation().getFileCompressConfig();
        fileCompressConfig.setFormat("zip");
        fileCompressConfig.setFlatten("1");
        List<KeyConfig> keyList = fileCompressConfig.getKeyConfigList();
        signedDocs.forEach(item -> {
            KeyConfig keyConfig = new KeyConfig();
            keyConfig.setKey(item.getFileName());
            keyConfig.setRename(item.getDocumentName() + StrUtil.DOT + FileNameUtil.extName(item.getFileName()));
            keyList.add(keyConfig);
        });
        MediaOutputObject output = request.getOperation().getOutput();
        output.setBucket(cosConfig.getBucketName());
        output.setRegion(cosConfig.getRegion());
        output.setObject(archivesFileKey);
        return cosClient.createFileProcessJob(request);
    }

    public FileProcessJobResponse describeFileProcessJob(String jobIdy) {
        FileProcessRequest request = new FileProcessRequest();
        request.setBucketName(cosConfig.getBucketName());
        request.setJobId(jobIdy);
        return cosClient.describeFileProcessJob(request);
    }

    public Map<String, String> getPostPolicy(String path,String extName){
        String bucketName = cosConfig.getBucketName();
        String endpoint = cosClient.getClientConfig().getEndpointBuilder().buildGeneralApiEndpoint(bucketName);
        String key = buildFileKey(path, extName);
        String qSignAlgorithm = "sha1";
        long startTimestamp = System.currentTimeMillis() / 1000;
        long endTimestamp = startTimestamp +  30 * 60;
        String endTimestampStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").
                format(endTimestamp * 1000);
        String keyTime = startTimestamp + ";" + endTimestamp;
        Map<String, String> formFields = new HashMap<>();
        formFields.put("cosHost", endpoint);
        formFields.put("cosKey", key);
        formFields.put("qAk", cosConfig.getSecretId());
        formFields.put("qKeyTime", keyTime);
        String policy = "{\n" +
                "    \"expiration\": \"" + endTimestampStr + "\",\n" +
                "    \"conditions\": [\n" +
                "        { \"bucket\": \"" + bucketName + "\" },\n" +
                "        { \"key\": \"" + key + "\" },\n" +
                "        { \"q-sign-algorithm\": \"sha1\" },\n" +
                "        { \"q-ak\": \"" + cosConfig.getSecretId() + "\" },\n" +
                "        { \"q-sign-time\":\"" + keyTime + "\" }\n" +
                "    ]\n" +
                "}";
        // policy需要base64
        String encodedPolicy = new String(Base64.getEncoder().encodeToString(policy.getBytes()));
        // 设置policy
        formFields.put("policy", encodedPolicy);
        formFields.put("qSignAlgorithm", qSignAlgorithm);
        // 根据编码后的policy和secretKey计算签名
        COSSigner cosSigner = new COSSigner();
        String signature = cosSigner.buildPostObjectSignature(cosConfig.getSecretKey(),
                keyTime, policy);
        // 设置签名
        formFields.put("qSignature", signature);
        return formFields;
    }

    /**
     * COS工作流回调
     * @param callbackStr

    public void cosCallback(String callbackStr) {
        log.info("SysOssService - cosCallback callbackStr:{}", callbackStr);

        JSONObject jsonObject = JSONUtil.parseObj(callbackStr);
        JSONObject workflowExecution = jsonObject.getJSONObject("WorkflowExecution");

        String state = workflowExecution.getStr("State");
        log.info("SysOssService - cosCallback state:{}", state);

        if(!"Success".equals(state)) return;

        String object = workflowExecution.getStr("Object");
        log.info("SysOssService - cosCallback object:{}", object);

        if(Validator.isNotEmpty(object)){
            String url = object;
            String bakUrl = String.format("%s_bak%s", object.substring(0, object.lastIndexOf(".")), object.substring(object.lastIndexOf(".")));
            String transcodingUrl = String.format("%s_transcoding%s", object.substring(0, object.lastIndexOf(".")), object.substring(object.lastIndexOf(".")));

            log.info("SysOssService - cosCallback url:{}", url);
            log.info("SysOssService - cosCallback bakUrl:{}", bakUrl);
            log.info("SysOssService - cosCallback transcodingUrl:{}", transcodingUrl);

            // 备份源视频文件
            UpLoadUtil.copyObject(url, bakUrl);
            UpLoadUtil.deleteObject(url);

            // 转移转码后的视频文件
            UpLoadUtil.copyObject(transcodingUrl, url);
            UpLoadUtil.deleteObject(transcodingUrl);
        }
    }
     */
}
