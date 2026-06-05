package com.joyfishs.sms.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SysSms {

    /* 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /* 用户ID */
    private Long userId;

    /* 手机号码 */
    private String phone;

    /* 短信类型 */
    private String type;

    /* 验证码 */
    private String code;

    /* 短信内容 */
    private String smsContent;

    /* 是否使用 1-是 0-否 */
    private int isUse;

    /* 是否推送 */
    private int isPush;

    /* 创建时间 */
    private Date createTime;

}
