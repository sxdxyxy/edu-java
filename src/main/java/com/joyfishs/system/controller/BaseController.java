package com.joyfishs.system.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyfishs.system.domain.R;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SqlUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.page.PageDomain;
import com.joyfishs.utils.page.TableDataInfo;
import com.joyfishs.utils.page.TableSupport;

import cn.hutool.core.date.DateUtil;

/** web层通用数据处理 **/
public class BaseController {

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtil.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     *
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> TableDataInfo<T> getDataTable(List<T> list) {
        TableDataInfo<T> rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.OK.value());
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        PageDomain pd = TableSupport.buildPageRequest();
        rspData.setPageSize(pd.getPageSize() != null ? pd.getPageSize() : 10);
        rspData.setPageNo(pd.getPageNum() != null ? pd.getPageNum() : 1);
        return rspData;
    }

    /**
     * 响应请求分页数据 (带转换)
     */
    protected <T, E> TableDataInfo<E> getDataTable(List<T> originalList, List<E> resultList) {
        TableDataInfo<E> rspData = new TableDataInfo<E>();
        rspData.setCode(HttpStatus.OK.value());
        rspData.setMsg("查询成功");
        rspData.setRows(resultList);
        rspData.setTotal(new PageInfo<T>(originalList).getTotal());
        PageDomain pd = TableSupport.buildPageRequest();
        rspData.setPageSize(pd.getPageSize() != null ? pd.getPageSize() : 10);
        rspData.setPageNo(pd.getPageNum() != null ? pd.getPageNum() : 1);
        return rspData;
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult<?> toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
    protected AjaxResult<?> toAjax(boolean bz) {
        return bz == true ? AjaxResult.success() : AjaxResult.error();
    }
    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected R<Void> toAjax2(boolean result) {
        return result ? R.ok() : R.fail();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StringUtils.format("redirect:{}", url);
    }
}
