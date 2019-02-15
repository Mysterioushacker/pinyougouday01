package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentCategoryMapper;
import com.pinyougou.pojo.TbContentCategory;
import com.pinyougou.content.service.ContentCategoryService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service(interfaceClass = ContentCategoryService.class)
public class ContentCategoryServiceImpl extends BaseServiceImpl<TbContentCategory> implements ContentCategoryService {

    @Autowired
    private ContentCategoryMapper contentCategoryMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbContentCategory contentCategory) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContentCategory.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(contentCategory.getName())){
            criteria.andLike("name", "%" + contentCategory.getName() + "%");
        }

        List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
        PageInfo<TbContentCategory> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
