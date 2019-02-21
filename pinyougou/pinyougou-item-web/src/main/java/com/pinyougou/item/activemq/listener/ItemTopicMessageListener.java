package com.pinyougou.item.activemq.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 接收商品spu id数组的发布与订阅消息；接收到消息之后更新指定路径下的商品详情静态html页面。
 */
public class ItemTopicMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    //注入配置文件中的配置项
    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;



    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        //1、获取消息
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        //2、遍历spu id数组，生成静态页面
        if (goodsIds != null && goodsIds.length > 0) {
            for (Long goodsId : goodsIds) {
                genItmeHtml(goodsId);
            }
        }
    }


    /**
     * 生成spu id对应的商品详情页面
     * @param goodsId 商品spu id
     */
    private void genItmeHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();

            //模版
            Template template = configuration.getTemplate("item.ftl");

            //数据
            Map<String, Object> map = new HashMap<>();
            //查询数据
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");

            //goods 商品基本
            map.put("goods", goods.getGoods());
            //goodsDesc 商品描述
            map.put("goodsDesc", goods.getGoodsDesc());
            //itemList  商品sku列表（根据spu id查询获取到的sku列表）
            map.put("itemList", goods.getItemList());
            //itemCat1 一级商品分类中文名称
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            map.put("itemCat1", itemCat1.getName());
            //itemCat2 二级商品分类中文名称
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            map.put("itemCat2", itemCat2.getName());
            //itemCat3 三级商品分类中文名称
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            map.put("itemCat3", itemCat3.getName());

            //输出
            String filename = ITEM_HTML_PATH + goodsId + ".html";
            FileWriter fileWriter = new FileWriter(filename);
            template.process(map, fileWriter);
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
