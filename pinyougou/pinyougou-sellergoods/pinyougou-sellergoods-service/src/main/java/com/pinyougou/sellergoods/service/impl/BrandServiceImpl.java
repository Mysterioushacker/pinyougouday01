package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;


import java.util.List;

@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl extends BaseServiceImpl<TbBrand> implements BrandService{
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<TbBrand> queryAll() {
        return brandMapper.queryAll();
    }

    /**
     * 分页查询
     * @param page
     * @param rows
     * @return
     */
    @Override
    public List<TbBrand> testPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        return brandMapper.selectAll();
    }

    /**
     * 根据条件模糊查询
     * @param brand
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageResult search(TbBrand brand, Integer page, Integer rows) {
        //设置分页
        PageHelper.startPage(page,rows);
        //设置模糊查询条件
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtil.isEmpty(brand.getFirstChar())){
            criteria.andEqualTo("firstChar",brand.getFirstChar());
        }
        if(!StringUtil.isEmpty(brand.getName())){
            criteria.andLike("name","%"+brand.getName()+"%");
        }
        List<TbBrand> list = brandMapper.selectByExample(example);
        PageInfo<TbBrand> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }
}
