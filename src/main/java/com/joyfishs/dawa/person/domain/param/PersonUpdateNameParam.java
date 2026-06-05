package com.joyfishs.dawa.person.domain.param;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 修改人员姓名参数
 */
@Data
@Accessors(chain = true)
public class PersonUpdateNameParam {

    /** 人员ID **/
    @NotNull(message = "人员ID不能为空")
    private Long id;

    /** 姓名 */
    @NotEmpty(message = "姓名不能为空")
    private String name;

}
