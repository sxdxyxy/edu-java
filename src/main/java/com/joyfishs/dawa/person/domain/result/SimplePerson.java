package com.joyfishs.dawa.person.domain.result;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */

@Data
@Accessors(chain = true)
@ApiModel(description = "学员简单信息，扫码后返回")
public class SimplePerson {
    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("出生日期")
    private String birthday;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("民族")
    private String nation;

    @ApiModelProperty("婚姻状态, 字典0002")
    private String jobs;

    @ApiModelProperty("工种名")
    private String workTypeName;

    @ApiModelProperty("学历 字典0004")
    private Integer degreeId;

    @ApiModelProperty("年度计划学时")
    private BigDecimal planClassHour;

    @ApiModelProperty("部门信息")
    private Long orgId;

    /* 组织名称 */
    private String orgName;

    private Map<String, List<DateItem>> groupedData;

    /**
     * 获取过滤后的groupedData，只包含type为0的DateItem对象
     * @return 过滤后的Map
     */
    public Map<String, List<DateItem>> filteredGroupedData() {
        if (groupedData == null) {
            return null;
        }
        return groupedData.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(),
                        entry.getValue().stream()
                                .filter(item -> item.getType() == 0)
                                .collect(Collectors.toList())
                ))
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}

