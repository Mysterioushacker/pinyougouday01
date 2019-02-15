package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    //广告数据在redis中的对应的key的名称
    private static final String CATEGORY_CONTENT = "CONTENT";

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> contentList =null;
        try {
            //查询redis中是否存在数据，如果存在则直接返回
            contentList = (List<TbContent>) redisTemplate.boundHashOps(CATEGORY_CONTENT).get(categoryId);
            if(contentList != null){
                return contentList;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId",categoryId);
        //启用状态的
        criteria.andEqualTo("status","1");

        //降序排序
        example.orderBy("sortOrder").desc();

        contentList = contentMapper.selectByExample(example);

        try {
            //将数据存入到redis中
            redisTemplate.boundHashOps(CATEGORY_CONTENT).put(categoryId,contentList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return contentList;
    }

    //新增后删除缓存
    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);

        //更新分类对应的缓存
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 更新分类对应的缓存
     * @param categoryId
     */
    private void updateContentListInRedisByCategoryId(Long categoryId) {
        redisTemplate.boundHashOps(CATEGORY_CONTENT).delete(categoryId);
    }

    //修改后删除缓存
    @Override
    public void update(TbContent tbContent) {
        //查询原来的分类
        TbContent oldContent = findOne(tbContent.getId());

        if(!oldContent.getCategoryId().equals(tbContent.getCategoryId())){
            //修改了广告分类
            updateContentListInRedisByCategoryId(oldContent.getCategoryId());
        }

        //更新分类对应的缓存
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());
        super.update(tbContent);
    }

    //删除后删除缓存
    @Override
    public void deleteByIds(Serializable[] ids) {
        //查询广告列表，然后再将每个广告对应
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));


        List<TbContent> contentList = contentMapper.selectByExample(example);
        for(TbContent tbContent:contentList){
            updateContentListInRedisByCategoryId(tbContent.getCategoryId());
        }
        super.deleteByIds(ids);
    }
}
