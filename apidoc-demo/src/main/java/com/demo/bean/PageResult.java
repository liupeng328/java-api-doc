package com.demo.bean;

import com.apidoc.utis.JsonUtil;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Objects;

/**
 * 分页的响应数据bean
 * <pre>
 *     封装分页时返回前端的数据对象
 *     响应数据：
 *          数据总条数     total
 *          当前页的数据列表  list
 * </pre>
 */
public class PageResult<E> implements Serializable {

    /**
     * 数据总条数
     */
    private Integer total;
    /**
     * 当前页的数据列表 泛型T
     */
    private List<E> list;

    /**
     * 无参构造
     */
    public PageResult() {

    }

    /**
     * 全参数构造
     *
     * @param total 总数
     * @param list  当页数据
     */
    public PageResult(Integer total, List<E> list) {
        this.total = total;
        this.list = list;
    }

    //--------get/set方法------
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    //equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageResult<?> that = (PageResult<?>) o;
        return Objects.equals(total, that.total) &&
                Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {

        return Objects.hash(total, list);
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", list=" + list +
                '}';
    }

}
