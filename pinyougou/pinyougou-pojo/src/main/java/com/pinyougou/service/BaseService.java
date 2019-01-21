package com.pinyougou.service;

import com.pinyougou.vo.PageResult;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T> {
    /**
     * 根据主键查询
     */
    public T findOne(Serializable id);

    /**
     * 查询全部
     */
    List<T> findAll();

    /**
     * 根据条件查询列表
     */
    List<T> findByWhere(T t);

    /**
     * 分页查询列表
     * page:当前页
     * rows:每页大小
     */
    PageResult findPage(Integer page,Integer rows);

    /**
     * 根据条件分页查询对象
     */
    PageResult findPage(Integer page, Integer rows, T t);

    /**
     * 新增
     * @param t 实体对象
     */
    void add(T t);

    /**
     * 根据主键更新
     * @param t 实体对象
     */
    void update(T t);

    /**
     * 批量删除
     */
    void deleteByIds(Serializable[] ids);
}
