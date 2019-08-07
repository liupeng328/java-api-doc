package com.demo.bean;

import java.io.Serializable;

/**
 * 分页请求参数
 * <p>
 * <pre>
 *     分页对象 包含分页请求参数和分页响应参数
 *         请求参数：
 *              分页参数如下：
 *              当前第几页：    pageIndex
 *              该页多少条数据： pageSize
 *              排序字段：      orderBy  排序字段构成的字符串，多字段用英文逗号隔开 例如：id asc,name desc
 *         查询参数如下：
 *              查询参数 params 泛型类型 可以是Map也可以是任意的javabean对象
 *     响应数据 pageResult：
 *          数据总条数    total
 *          当前页数据列表 list
 * </pre>
 */
public class Page<T> extends com.baomidou.mybatisplus.plugins.Page implements Serializable {

    /**
     * 当前第几页
     */
    private Integer pageIndex;
    /**
     * 该页多少条数据
     */
    private Integer pageSize;
    /**
     * 排序字段：orderBy 排序字段构成的字符串，多字段用英文逗号隔开 例如：id asc,name desc
     */
    private String orderBy;
    /**
     * 查询参数 params 泛型T
     */
    private T params;
    /**
     * 响应数据，用于查询完毕返回前端的数据封装
     */
    private PageResult result;



    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
        setCurrent(pageIndex);
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        setSize(pageSize);
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    @SuppressWarnings("unchecked")
    public PageResult getResult() {
        return new PageResult(getTotal(), getRecords());
    }

    public void setResult(PageResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", orderBy='" + orderBy + '\'' +
                ", params=" + params +
                ", result=" + result +
                '}';
    }
}
