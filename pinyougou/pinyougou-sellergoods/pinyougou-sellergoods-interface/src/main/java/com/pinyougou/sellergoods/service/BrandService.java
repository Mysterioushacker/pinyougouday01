package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface BrandService extends BaseService<TbBrand> {
    List<TbBrand> queryAll();

    List<TbBrand> testPage(Integer page, Integer rows);

    PageResult search(TbBrand brand, Integer page, Integer rows);
}
