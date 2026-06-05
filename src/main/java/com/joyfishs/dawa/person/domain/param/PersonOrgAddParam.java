package com.joyfishs.dawa.person.domain.param;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 人员项目关联参数
 */
@Data
@Accessors(chain = true)
public class PersonOrgAddParam {

    private Long orgId;

    private List<Long> personIds = new ArrayList<>();
}
