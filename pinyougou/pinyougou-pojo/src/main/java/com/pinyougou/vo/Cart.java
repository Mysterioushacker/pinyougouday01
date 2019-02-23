package com.pinyougou.vo;

import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable{
    //卖家Id
    private String sellerId;

    //卖家名称
    private String sellerName;

    //购物车明细
    private List<TbOrderItem> orderItemList;

    public Cart() {
    }

    public Cart(String sellerId, String sellerName, List<TbOrderItem> orderItemList) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.orderItemList = orderItemList;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
