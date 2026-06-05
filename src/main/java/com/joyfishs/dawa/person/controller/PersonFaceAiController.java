package com.joyfishs.dawa.person.controller;

import com.joyfishs.dawa.person.domain.param.FacePhotoRequest;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.tencent.service.FaceAiService;
import com.joyfishs.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "人脸识别")
@RequestMapping("/person/face")
@RequiredArgsConstructor
public class PersonFaceAiController extends BaseController {

    static final Boolean FACE_VERIFY = Boolean.TRUE;
    static final Boolean LEARN_PAGE_FACE_VERIFY = Boolean.FALSE;
    private final FaceAiService faceAiService;

    @PostMapping("/detectLiveFaceAccurate")
    @ApiOperation(value = "验证人脸照片是否活体并且是当前登录人员")
    public R<Boolean> detectLiveFaceAccurate(@RequestBody FacePhotoRequest req) {
        float score = faceAiService.detectLiveFaceAccurate(req.getFacePhotoUrl());
        if (score < 70) {
            return R.fail("此照片疑似非真人，请重新拍摄本人照片。", Boolean.FALSE);
        }
        boolean result = faceAiService.verifyPerson(SecurityUtil.getPersonId(), req.getFacePhotoUrl());
        return R.ok(result);
    }

    /**
     * 是否开启人脸认证，过小程序审核后返回true
     *
     * @return
     */
    @GetMapping("/faceVerify")
    public R<Boolean> faceVerify() {
        return R.ok(FACE_VERIFY);
    }

    /**
     * learnPage视频播放页是否开启人脸认证
     *
     * @return
     */
    @GetMapping("/learnPageFaceVerify")
    public R<Boolean> learnPageFaceVerify() {
        return R.ok(LEARN_PAGE_FACE_VERIFY);
    }
}
