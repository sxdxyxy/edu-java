package com.joyfishs.ueditor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UeditorConfig {

    /* 状态 */
    private String state;

    /* 回显路径 */
    private String url;

    /* 大小 */
    private long size;

    /* 类型 */
    private String type;

    /* 文件title */
    private String title;

    /* 名称 */
    private String original;
}
