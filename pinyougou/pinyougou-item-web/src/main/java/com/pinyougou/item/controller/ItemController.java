package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ItemController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    /**
     * 跳转到商品详情页面显示商品
     * @return
     */
    @GetMapping("/{goodsId}")
    public ModelAndView toItemPage(@PathVariable Long goodsId){
        ModelAndView mv = new ModelAndView("item");
        //根据商品id查询商品基本、描述、SKU列表
        Goods goods = goodsService.findGoodsByIdAndStatus(goodsId,"1");

        //商品基本信息
        mv.addObject("goods",goods.getGoods());

        //商品描述信息
        mv.addObject("goodsDesc",goods.getGoodsDesc());

        //itemList 商品sku列表(根据spu id查询获取到的sku列表)
        mv.addObject("itemList",goods.getItemList());

        //itemCat1 一级商品分类中文名称
        TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
        mv.addObject("itemCat1",itemCat1.getName());

        //itemCat2 二级商品分类中文名称
        TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
        mv.addObject("itemCat2",itemCat2.getName());

        //itemCat2 二级商品分类中文名称
        TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
        mv.addObject("itemCat3",itemCat3.getName());
        return mv;
    }
}
