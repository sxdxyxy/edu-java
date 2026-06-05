package com.joyfishs.dawa.person.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人员核身鉴权记录
 * @author yangkaifeng
 */

@Data
@NoArgsConstructor
@TableName(value ="xm_person_detect_auth")
public class PersonDetectAuth {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 人员id */
    private Long personId;

    /** 人脸核身订单号 */
    private String orderNo;

    /** 业务流水号，腾讯返回 */
    private String bizSeqNo;

    /** 微信普通 H5才有,一次核身流程的标识，有效时间为7,200秒；
     完成核身后，可用该标识获取验证结果信息,等同orderNo
     */
    private String bizToken;

    /** 此次刷脸用户标识 */
    private String faceId;

    /** 0：表示身份验证成功且认证为同一人 */
    private String code;

    private String msg;

    /** 活体检测得分 */
    private String liveRate;

    /** 人脸比对得分 */
    private String similarity;

    /** 人脸核身时的 sdk 版本号 */
    private String sdkVersion;

    /** 进行刷脸的时间 */
    private LocalDateTime occurredTime;

    /** 人脸核身时的照片 */
    private String photoUrl;

    /** 人脸识别的视频 */
    private String videoUrl;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public PersonDetectAuth(Long personId, String orderNo) {
        this.personId = personId;
        this.orderNo = orderNo;
        this.createTime = LocalDateTime.now();
    }
}
