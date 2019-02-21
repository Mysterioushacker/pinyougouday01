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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Transactional
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

        //不查询删除商品,参数为isDelete值为1的商品
        criteria.andNotEqualTo("isDelete","1");


        //商家限定
        //模糊查询，商家账号id
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andLike("sellerId", "%" + goods.getSellerId() + "%");
        }

        //审核状态
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
        }

        //商品名称
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

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
     * 根据商品id查询sku信息
     * @param id
     * @return
     */
    @Override
    public Goods findGoodsById(Long id) {
        return findGoodsByIdAndStatus(id,null);
    }

    @Override
    public void updateGoods(Goods goods) {
        //更新商品基本信息
        goods.getGoods().setAuditStatus("0");//修改过则重新设置为未审核
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());

        //更新商品描述信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //删除原有的sku列表
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);

        //保存商品sku列表
        saveItemList(goods);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        //update tb_goods set audit_status = ? where id in (?,?,?);
        //spu
        TbGoods goods = new TbGoods();
        goods.setAuditStatus(status);

        //更新条件
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(goods,example);

        //如果是，审核通过，则需要将spu对应的所有sku的状态修改为已启用(1)
        if("2".equals(status)){
            //update tb_item set status = ? where goods_id in (?,?,?)
            TbItem item = new TbItem();
            //已启用
            item.setStatus("1");

            Example itemExample = new Example(TbItem.class);
            itemExample.createCriteria().andIn("goodsId",Arrays.asList(ids));
            itemMapper.updateByExampleSelective(item,itemExample);
        }
    }

    @Override
    public void deleteGoodsByIds(Long[] ids) {
        TbGoods goods = new TbGoods();
        goods.setIsDelete("1");

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));

        //批量更新商品的删除状态为删除
        goodsMapper.updateByExampleSelective(goods,example);
    }

    @Override
    public void upGoods(Long[] ids) {
        TbGoods goods = new TbGoods();
        goods.setIsMarketable("1");

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));

        //批量上架商品
        goodsMapper.updateByExampleSelective(goods,example);
    }

    @Override
    public void downGoods(Long[] ids) {
        TbGoods goods = new TbGoods();
        goods.setIsMarketable("0");

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));

        //批量下架商品
        goodsMapper.updateByExampleSelective(goods,example);
    }

    @Override
    public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String s) {
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("status",s).andIn("goodsId",Arrays.asList(ids));
        return itemMapper.selectByExample(example);
    }

    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String s) {
        Goods goods = new Goods();
        //查询商品spu
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        goods.setGoods(tbGoods);

        //查询商品描述
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        goods.setGoodsDesc(tbGoodsDesc);

        //查询商品sku列表
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId",goodsId);
        if(!StringUtils.isEmpty(s)){
            criteria.andEqualTo("status",s);
        }
        //按照是否默认值降序排序，默认值为1，否则为0
        example.orderBy("isDefault").desc();

        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
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
