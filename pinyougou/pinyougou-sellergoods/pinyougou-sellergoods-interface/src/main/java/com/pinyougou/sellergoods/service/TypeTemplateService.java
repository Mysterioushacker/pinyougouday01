package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);
}