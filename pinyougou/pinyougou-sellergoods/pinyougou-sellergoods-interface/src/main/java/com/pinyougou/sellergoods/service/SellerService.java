package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface SellerService extends BaseService<TbSeller> {

    PageResult search(Integer page, Integer rows, TbSeller seller);
}