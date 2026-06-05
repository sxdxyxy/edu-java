package com.joyfishs.tencent.service;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.entity.PersonDetectAuth;
import com.joyfishs.dawa.person.service.PersonDetectAuthService;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.DetectAuthRequest;
import com.tencentcloudapi.faceid.v20180301.models.DetectAuthResponse;
import com.tencentcloudapi.iai.v20200303.IaiClient;
import com.tencentcloudapi.iai.v20200303.IaiErrorCode;
import com.tencentcloudapi.iai.v20200303.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 人脸识别
 *
 * @author ykfnb
 */
@Slf4j
@Service
public class FaceAiService {

    @Autowired
    private SignTicketService signTicketService;
    @Autowired
    private GetSignService getSignService;
    @Autowired
    private TencentParamService tencentParamService;
    @Autowired
    private NonceTicketService nonceTicketService;

    @Autowired
    private PersonDetectAuthService personDetectAuthService;

    @Value("${face.accessKeyId}")
    private String accessKeyId;
    @Value("${face.accessKeySecret}")
    private String accessKeySecret;

    @Value("${tencent.secretId}")
    private String secretId;
    @Value("${tencent.secretKey}")
    private String secretKey;
    private IaiClient client;
    private FaceidClient faceIdClient;
    private final String PersonGroupId = "safe-edu";

    @PostConstruct
    public void init() {
        Credential credential = new Credential(accessKeyId, accessKeySecret);
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("iai.tencentcloudapi.com");
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        this.client = new IaiClient(credential, "ap-shanghai", clientProfile);

        Credential faceIdCred = new Credential(secretId, secretKey);
        HttpProfile faceIdHttpProfile = new HttpProfile();
        faceIdHttpProfile.setEndpoint("faceid.tencentcloudapi.com");
        ClientProfile faceIdClientProfile = new ClientProfile();
        faceIdClientProfile.setHttpProfile(faceIdHttpProfile);
        this.faceIdClient = new FaceidClient(faceIdCred, "", faceIdClientProfile);
    }
    public static final String THUMBNAIL = "?imageMogr2/thumbnail/2000x2000%3E";
    /**
     * 人脸检测与属性分析
     *
     * @param faceUrl 待分析人脸的照片url
     */
    public FaceInfo[] detectFaceAttributes(String faceUrl) {
        try {
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DetectFaceRequest req = new DetectFaceRequest();
            req.setNeedQualityDetection(1L);
            req.setMaxFaceNum(2L);
            req.setUrl(faceUrl+ FaceAiService.THUMBNAIL);
            // 返回的resp是一个DetectFaceAttributesResponse的实例，与请求对象对应
            DetectFaceResponse resp = client.DetectFace(req);
            log.info("人脸检测响应：{}", DetectFaceResponse.toJsonString(resp));
            return resp.getFaceInfos();
        } catch (TencentCloudSDKException e) {
            log.error("人脸检测出错：", e);
        }
        return null;
    }

    /**
     * 上传人脸库
     *
     * @param person
     */
    public CreatePersonResponse createPerson(Person person) {
        CreatePersonRequest req = new CreatePersonRequest();
        req.setGroupId(PersonGroupId);
        req.setPersonName(person.getName());
        req.setPersonId(person.getId().toString());
        if (ObjectUtil.isNotNull(person.getSex())) {
            req.setGender(person.getSex().longValue());
        } else {
            req.setGender(0L);
        }
        req.setUrl(person.getFacePhotoUrl()+ FaceAiService.THUMBNAIL);
        PersonExDescriptionInfo[] personExDescriptionInfos1 = new PersonExDescriptionInfo[2];
        PersonExDescriptionInfo personExDescriptionInfo1 = new PersonExDescriptionInfo();
        personExDescriptionInfo1.setPersonExDescriptionIndex(0L);
        personExDescriptionInfo1.setPersonExDescription(person.getPhone());
        personExDescriptionInfos1[0] = personExDescriptionInfo1;

        PersonExDescriptionInfo personExDescriptionInfo2 = new PersonExDescriptionInfo();
        personExDescriptionInfo2.setPersonExDescriptionIndex(1L);
        personExDescriptionInfo2.setPersonExDescription(person.getOrgId().toString());
        personExDescriptionInfos1[1] = personExDescriptionInfo2;

        req.setPersonExDescriptionInfos(personExDescriptionInfos1);
        req.setUniquePersonControl(2L);
        req.setQualityControl(3L);
        try {
            CreatePersonResponse resp = client.CreatePerson(req);
            log.info("上传人脸库响应：{}", CreatePersonResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            throw new CustomException(e.getMessage());
        }
    }
    /**
     * 根据personId查询人脸库中是否已经存在，不存在返回null
     *
     * @param personId
     */
    public GetPersonBaseInfoResponse queryPersonRequest(Long personId) {
        GetPersonBaseInfoRequest req = new GetPersonBaseInfoRequest();
        req.setPersonId(personId.toString());

        try {
            GetPersonBaseInfoResponse resp = client.GetPersonBaseInfo(req);
            log.info("查询人脸库中人员信息响应：{}", CreatePersonResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            if (StrUtil.equals(e.getErrorCode(), IaiErrorCode.INVALIDPARAMETERVALUE_PERSONIDNOTEXIST.getValue())) {
                return null;
            }
            throw new CustomException(e.getMessage());
        }
    }

    public CreateFaceResponse createFace(Person person){
        CreateFaceRequest req = new CreateFaceRequest();
        req.setPersonId(person.getId().toString());
        String[] urls = {person.getFacePhotoUrl()+ FaceAiService.THUMBNAIL};
        req.setUrls(urls);
        try {
            CreateFaceResponse resp = client.CreateFace(req);
            log.info("增加人脸响应：{}", CreatePersonResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            throw new CustomException(e.getMessage());
        }
    }

    public void deleteFace(Long personId,String faceId){
        DeleteFaceRequest req = new DeleteFaceRequest();
        req.setPersonId(personId.toString());
        String[] faceIds1 = {faceId};
        req.setFaceIds(faceIds1);
        try {
            DeleteFaceResponse resp = client.DeleteFace(req);
            log.info("删除人脸响应：{}", CreatePersonResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 人脸比对
     *
     * @param faceUrlA
     * @param faceUrlB
     * @return
     */
    public CompareFaceResponse compareFace(String faceUrlA, String faceUrlB) {
        CompareFaceRequest req = new CompareFaceRequest();
        req.setUrlA(faceUrlA+ FaceAiService.THUMBNAIL);
        req.setUrlB(faceUrlB+ FaceAiService.THUMBNAIL);
        try {
            CompareFaceResponse resp = client.CompareFace(req);
            log.info("人脸比对响应：{}", CompareFaceResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 人脸静态活体检测
     *
     * @param faceUrl
     */
    public Float detectLiveFaceAccurate(String faceUrl) {
        DetectLiveFaceAccurateRequest req = new DetectLiveFaceAccurateRequest();
        req.setUrl(faceUrl+ FaceAiService.THUMBNAIL);
        try {
            DetectLiveFaceAccurateResponse resp = client.DetectLiveFaceAccurate(req);
            log.info("人脸比对响应：{}", DetectLiveFaceAccurateResponse.toJsonString(resp));
            return resp.getScore();
        } catch (TencentCloudSDKException e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 人员验证
     *
     * @param personId
     * @param faceUrl
     */
    public boolean verifyPerson(Long personId, String faceUrl) {
        VerifyPersonRequest req = new VerifyPersonRequest();
        req.setUrl(faceUrl+ FaceAiService.THUMBNAIL);
        req.setPersonId(personId.toString());
        req.setQualityControl(3L);
        try {
            VerifyPersonResponse resp = client.VerifyPerson(req);
            log.info("人员验证响应：{}", VerifyPersonResponse.toJsonString(resp));
            return resp.getIsMatch();
        } catch (TencentCloudSDKException e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 人脸搜索
     *
     * @param faceUrl
     * @return
     */
    public Long searchPersons(String faceUrl) {
        SearchPersonsRequest req = new SearchPersonsRequest();
        req.setUrl(faceUrl+ FaceAiService.THUMBNAIL);
        req.setMinFaceSize(80L);
        req.setGroupIds(new String[]{PersonGroupId});
        req.setMaxPersonNum(1L);
        req.setQualityControl(3L);
        req.setFaceMatchThreshold(80F);
        req.setNeedPersonInfo(1L);
        try {
            SearchPersonsResponse resp = client.SearchPersons(req);
            log.info("人脸搜索响应：{}", SearchPersonsResponse.toJsonString(resp));
            if (resp.getPersonNum() == 0) {
                return null;
            }
            Result result = resp.getResults()[0];
            Candidate person = result.getCandidates()[0];
            return Long.valueOf(person.getPersonId());
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 实名核身鉴权
     * @param person
     * @param redirectUrl
     */
    public DetectAuthResponse detectAuth(Person person,String imageBase64,String redirectUrl){
        DetectAuthRequest req = new DetectAuthRequest();
        req.setIdCard(person.getIdCardNo());
        req.setName(person.getName());
        req.setRedirectUrl(redirectUrl);
        req.setRuleId("1");
        req.setImageBase64(imageBase64);
        try {
            DetectAuthResponse resp = faceIdClient.DetectAuth(req);
            log.info("实名核身鉴权响应：{}", DetectAuthResponse.toJsonString(resp));
            PersonDetectAuth detectAuth = new PersonDetectAuth(person.getId(),resp.getBizToken());
            detectAuth.setBizToken(resp.getBizToken());
            personDetectAuthService.save(detectAuth);
            return resp;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 人脸识别 上送身份信息
     * @param userId
     * @param imageBase64
     */
    public JSONObject getFaceId(Long userId, String name, String idCardNo, String imageBase64){
        String nonce = IdUtil.simpleUUID();
        List<String> signList = new ArrayList<>();
        signList.add(userId.toString());
        signList.add(tencentParamService.appId);
        signList.add("1.0.0");
        signList.add(nonce);

        String sign = getSignService.getSign(signList, signTicketService.getSignTicket());
        log.info("updateFace - sign:{}", sign);
        String orderNo = IdUtil.simpleUUID();
        PersonDetectAuth detectAuth = new PersonDetectAuth(userId,orderNo);

        String uploadFaceIdUrl = String.format("%s?orderNo=%s", tencentParamService.uploadFaceidUrl, orderNo);
        log.info("updateFace - uploadFaceIdUrl:{}", uploadFaceIdUrl);

        JSONObject param = new JSONObject();
        param.put("webankAppId", tencentParamService.appId);
        param.put("orderNo", orderNo);
        param.put("name", name);
        param.put("idNo", idCardNo);
        param.put("userId", userId);
        param.put("sourcePhotoStr", imageBase64);
        param.put("sourcePhotoType", "1");
        param.put("version", "1.0.0");
        param.put("sign", sign);
        param.put("nonce", nonce);
        String body = HttpRequest.post(uploadFaceIdUrl).body(param.toJSONString()).timeout(20000).execute().body();
        log.info("updateFace - body:{}", body);
        JSONObject bodyJson = JSONObject.parseObject(body);
        if(StringUtils.isNotEmpty(bodyJson.getString("code")) && "0".equals(bodyJson.getString("code"))){
            log.info("updateFace ok ...");
            JSONObject jObject= bodyJson.getJSONObject("result");
            String faceId = jObject.getString("faceId");
            String optimalDomain = jObject.getString("optimalDomain");
            String bizSeqNo = jObject.getString("bizSeqNo");
            log.info("updateFace faceId:{}", faceId);
            log.info("updateFace optimalDomain:{}", optimalDomain);
            optimalDomain = Validator.isEmpty(optimalDomain) ? "miniprogram-kyc.tencentcloudapi.com" : optimalDomain;
            detectAuth.setFaceId(faceId);
            detectAuth.setBizSeqNo(bizSeqNo);
            personDetectAuthService.save(detectAuth);
            return buildResultData(userId.toString(), orderNo, faceId, optimalDomain);
        } else {
            throw new CustomException("身份信息处理失败");
        }
    }

    /**
     * 封装返回数据
     * @param userId
     * @param faceId
     * @return
     */
    public JSONObject buildResultData(String userId,String orderNo, String faceId, String optimalDomain){
        log.info("updateFace - getRetData userId:{}", userId);
        log.info("updateFace - getRetData faceId:{}", faceId);
        log.info("updateFace - getRetData optimalDomain:{}", optimalDomain);

        String nonce = IdUtil.simpleUUID();
        log.info("UpdateFaceidService - getRetData nonce:{}", nonce);

        List<String> signList = new ArrayList<>();
        signList.add(userId);
        signList.add(tencentParamService.appId);
        signList.add("1.0.0");
        signList.add(nonce);
        log.info("updateFace - getRetData signList:{}", signList);

        String sign = getSignService.getSign(signList, nonceTicketService.getNonceTicket(userId));
        log.info("updateFace - getRetData sign:{}", sign);

//        String agreementNo = IdUtil.simpleUUID();
//        log.info("updateFace - getRetData agreementNo:{}", agreementNo);

        JSONObject paramJson = new JSONObject();
        paramJson.put("apiVersion", "1.0.0");
        paramJson.put("appId", tencentParamService.appId);
        paramJson.put("nonce", nonce);
        paramJson.put("userId", userId);
        paramJson.put("sign", sign);
        paramJson.put("orderNo", orderNo);
        paramJson.put("licence", tencentParamService.licence);
        paramJson.put("faceId", faceId);
        paramJson.put("optimalDomain", optimalDomain);
        paramJson.put("faceType", "1");
        paramJson.put("compareType", "0");
        log.info("updateFace - getRetData paramJson:{}", paramJson.toJSONString());

        return paramJson;
    }
}
