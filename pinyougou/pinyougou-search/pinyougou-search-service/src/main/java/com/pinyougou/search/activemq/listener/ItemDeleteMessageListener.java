package com.pinyougou.search.activemq.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Arrays;

/**
 * 商品spu id数组的点对点消息：接收到消息之后删除solr中的商品数据
 */
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener{

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //1.接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;
        //2.转换为Id数组
        Long[] goodsIds = (Long[]) objectMessage.getObject();

        //3.更新数据
        itemSearchService.deleteItemByGoodsIdList(Arrays.asList(goodsIds));
        System.out.println("同步删除solr不成功.");
    }
}
