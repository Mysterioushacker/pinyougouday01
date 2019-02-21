package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/test")
@RestController
public class PageTestController {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * 审核商品后生成商品html页面到指定路径
     * @param goodsIds 商品id集合
     * @return
     */
    @GetMapping("/audit")
    public String audit(Long[] goodsIds){
        if(goodsIds != null && goodsIds.length >0){
            for (Long goodsId : goodsIds){
                genItemhtml(goodsId);
            }
        }
        return "success";
    }

    /**
     * 生成SPU id对应的商品详情页面
     * @param goodsId
     */
    private void genItemhtml(Long goodsId) {
        try {
            //获取模板
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            //获取模板需要的数据
            Map<String,Object> dataModel = new HashMap<>();
            //根据商品id查询商品基本信息、描述、启用的SKU列表
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");
            //商品基本信息
            dataModel.put("goods",goods.getGoods());
            //商品描述信息
            dataModel.put("goodsDesc",goods.getGoodsDesc());

            //查询三级商品分类
            TbItemCat itemCat1 =  itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1",itemCat1.getName());
            TbItemCat itemCat2 =  itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2",itemCat2.getName());
            TbItemCat itemCat3 =  itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3",itemCat3.getName());

            //查询SKU商品列表
            dataModel.put("itemList",goods.getItemList());
            //输出到指定路径
            String fileName = ITEM_HTML_PATH+goodsId+".html";
            FileWriter fileWriter = new FileWriter(fileName);
            template.process(dataModel,fileWriter);
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除商品后删除指定路径下的商品html页面
     * @param goodsIds
     * @return
     */
    @GetMapping("/delete")
    public String delete(Long[] goodsIds){
        if(goodsIds != null && goodsIds.length>0){
            for (Long goodsId: goodsIds){
                String filename = ITEM_HTML_PATH+goodsId+".html";
                File file = new File(filename);
                if(file.exists()){
                    file.delete();
                }
            }
        }

        return "success";
    }
}
