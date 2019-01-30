package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private ItemMapper itemMapper;
    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(goods.get***())){
            criteria.andLike("***", "%" + goods.get***() + "%");
        }*/

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {
        //新增商品基本信息
        goodsMapper.insertSelective(goods.getGoods());
        //新增商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        //保存商品SKU列表
        saveItemList(goods);
    }

    /**
     * 保存商品sku列表
     * @param goods 商品信息（基本，描述，sku列表）
     */
    private void saveItemList(Goods goods) {
        if(goods.getItemList() != null && goods.getItemList().size()>0){
            for(TbItem item : goods.getItemList()){
                //sku标题：spu的名称+所有当前这个sku对应的规格值拼接
                String  title = goods.getGoods().getGoodsName();
                //获取规格内容
                Map<String,String> specMap = JSONObject.parseObject(item.getSpec(),Map.class);
                //获取规格内容集合中的键
                Set<Map.Entry<String, String>> entries = specMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title += " "+entry.getValue();
                }
                item.setTitle(title);

                //spu的第三级商品分类id
                item.setCategoryid(goods.getGoods().getCategory3Id());
                TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
                //根据第三级商品id查询商品名字
                item.setCategory(itemCat.getName());

                //品牌中文名称
                //根据品牌id查询品牌名称
                TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
                item.setBrand(brand.getName());

                //商品图片可以获取spu描述信息中的图片列表的第一张图片
                //判断spu信息中的体图片列表是否为空
                if(!StringUtils.isEmpty(goods.getGoodsDesc().getItemImages())){
                    List<Map> imageList = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
                    if(imageList != null && imageList.size()>0){
                        item.setImage(imageList.get(0).get("url").toString());
                    }

                    //商家
                    item.setGoodsId(goods.getGoods().getId());
                    item.setSellerId(goods.getGoods().getSellerId());
                    TbSeller seller = sellerMapper.selectByPrimaryKey(item.getSellerId());
                    item.setSeller(seller.getName());

                    item.setCreateTime(new Date());
                    item.setUpdateTime(item.getCreateTime());

                    //保存sku
                    itemMapper.insertSelective(item);
                }
            }
        }
    }
}
