package com.pinyougou.item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;


/**
 * 接收商品spu id数组的发布与订阅消息；接收到消息之后删除指定路径下的商品详情静态html页面。
 */
public class ItemDeleteTopicMessageListener extends AbstractAdaptableMessageListener {

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
                File file = new File(ITEM_HTML_PATH + goodsId + ".html");
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

}
