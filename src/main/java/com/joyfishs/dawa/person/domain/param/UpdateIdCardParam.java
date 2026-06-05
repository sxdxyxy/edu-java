package com.joyfishs.dawa.person.domain.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 修改身份证信息参数
 */
@Data
@Accessors(chain = true)
public class UpdateIdCardParam {

    private Long personId;

    /** 人员姓名 **/
    private String name;

    /** 身份证号码 **/
    private String idCardNo;

    /** 身份证照片正面 -图片Base64 **/
    private String idPhotoFace;

    /** 身份证照片反面 -图片Base64 **/
    private String idPhotoBack;
}
