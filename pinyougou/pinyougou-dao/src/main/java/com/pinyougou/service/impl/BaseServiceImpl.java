package com.pinyougou.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;

/**
 * 抽象类
 */
public abstract class BaseServiceImpl<T> implements BaseService<T>{
    @Autowired
    private Mapper<T> mapper;

    /**
     * 根据主键查询
     *
     * @param id
     */
    @Override
    public T findOne(Serializable id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 查询全部
     */
    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }

    /**
     * 根据条件查询列表
     *
     * @param t
     */
    @Override
    public List<T> findByWhere(T t) {
        return mapper.select(t);
    }

    /**
     * 分页查询列表
     * page:当前页
     * rows:每页大小
     *
     * @param page
     * @param rows
     */
    @Override
    public PageResult findPage(Integer page, Integer rows) {
        //分页
        PageHelper.startPage(page,rows);
        //查询全部信息
        List<T> list = mapper.selectAll();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 根据条件分页查询对象
     *
     * @param page
     * @param rows
     * @param t
     */
    @Override
    public PageResult findPage(Integer page, Integer rows, T t) {
        //设置分页
        PageHelper.startPage(page,rows);
        List<T> list = mapper.select(t);
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 新增
     *
     * @param t 实体对象
     */
    @Override
    public void add(T t) {
        mapper.insertSelective(t);
    }

    /**
     * 根据主键更新
     *
     * @param t 实体对象
     */
    @Override
    public void update(T t) {
        mapper.updateByPrimaryKeySelective(t);
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void deleteByIds(Serializable[] ids) {
        if(ids != null && ids.length>0){
            for(Serializable id :ids){
                mapper.deleteByPrimaryKey(id);
            }
        }
    }
}
