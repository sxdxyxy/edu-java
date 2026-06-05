package com.joyfishs.dawa.person.domain.result;

import java.util.List;
import java.util.Map;

import com.joyfishs.dawa.person.entity.Person;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */

@Data
@Accessors(chain = true)
@ApiModel(description = "学员信息详情，扫码后返回")
public class PersonDetail extends Person {
    /* 组织名称 */
    private String orgName;

    private Map<String, List<DateItem>> groupedData;

}

