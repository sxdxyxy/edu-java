package com.joyfishs.utils.page;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/** 表格分页数据对象 **/
@Data
public class TableDataInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 总记录数 **/
    private long total;

    /** 当前页码 **/
    private int pageNo;

    private int pageSize;

    /** 列表数据 **/
    private List<T> rows;

    /** 消息状态码 **/
    private int code;

    /** 消息内容 **/
    private String msg;

    /**
     * 表格数据对象
     *
     */
    public TableDataInfo() {
    }

    /**
     * 分页
     *
     * @param list 列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, int total, int pageNo,int pageSize) {
        this.rows = list;
        this.total = total;
        this.pageNo = pageNo;
    }

}
