package com.joyfishs.dawa.statistics.domain;

import java.math.BigDecimal;

import com.joyfishs.dawa.utils.Dictionary;
import com.joyfishs.system.entity.SysDataDictionaryItem;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author QuAoLi
 * @description: 个人统计返回对象
 * @date 2021-12-30 16:17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "个人统计数据")
public class DataStatisticsPersonal {

    @ApiModelProperty("人员id")
    private Long personId;

    @ApiModelProperty("姓名")
    private String name;

    /** 类型 多个类型用,分隔 字典0005 */
    private String type;

    public String getTypeName() {
        if (StrUtil.isEmpty(this.type)) {
            return "";
        }
        String[] typeArr = this.type.split(",");
        StringBuffer typeStr = new StringBuffer();
        for(int i = 0; i < typeArr.length; i++){
            SysDataDictionaryItem dictionaryItem = Dictionary.getDictionaryItem("0005", typeArr[i]);
            if (i != 0) {
                typeStr.append(",");
            }
            typeStr.append(dictionaryItem.getName());
        }
        return typeStr.toString();
    }

    @ApiModelProperty("年度计划学时")
    private BigDecimal planClassHour;

    @ApiModelProperty("年度学时")
    private BigDecimal yearClassHour;

    @ApiModelProperty("累计学时")
    private BigDecimal sumClassHour;

    @ApiModelProperty("人均学时")
    private BigDecimal avgClassHour;

    @ApiModelProperty("人员部门")
    private String orgName;

    @ApiModelProperty("完成课程数量")
    private int finishOfCourses ;

}
