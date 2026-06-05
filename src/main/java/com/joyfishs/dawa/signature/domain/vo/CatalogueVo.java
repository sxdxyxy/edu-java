package com.joyfishs.dawa.signature.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "CatalogueVo", description = "一人一档信息目录")
public class CatalogueVo {
    private int index;

    private String documentName;

    public CatalogueVo(int index, String documentName) {
        this.index = index;
        this.documentName = documentName;
    }
}
