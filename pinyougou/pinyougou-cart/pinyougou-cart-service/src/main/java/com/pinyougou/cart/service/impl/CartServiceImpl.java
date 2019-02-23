package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {

    //系统的购物车在redis中的key名称
    private static final Object REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 1、验证商品是否存在，商品的启用状态是否启用
     * 2、如果该商品对应的商家不存在在购物车列表中；则重新加商家及其对应的商品
     * 3、如果该商品对应的商家存在在购物车列表中；那么判断商品是否存在若是则购买数量叠
     加，否则新加入商品到该商家
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.当前要买的商品是否存在和是否启用状态
        //根据skuid查询sku信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null){
            throw new RuntimeException("商品不存在，购买失败");
        }

        if (!"1".equals(item.getStatus())){
            throw new RuntimeException("商品不合格，购买失败");
        }

        //2.商品对应的商家是否存在
        Cart cart = findCartInCartListBySellerId(cartList,item.getSellerId());

        if (cart == null){
            if (num > 0){
                //2.1商家Cart不存在,在购物车中添加商家并往商家的商品列表中添加商品
                cart = new Cart();
                cart.setSellerId(item.getSellerId());
                cart.setSellerName(item.getSeller());

                //创建商品列表
                List<TbOrderItem> orderItemList = new ArrayList<>();

                TbOrderItem orderItem = createOrderItemByItemAndNum(item,num);

                orderItemList.add(orderItem);
                cart.setOrderItemList(orderItemList);
                cartList.add(cart);
            }else {
                throw new RuntimeException("商品购买数量不合法，购买失败");
            }
        }else {
            //2.2商家存在
            TbOrderItem orderItem = findOrderItemInOrderItemListByItemId(cart.getOrderItemList(),itemId);

            if (orderItem == null){
                //2.2.1、商家存在，商品不存在；那么在该商家的商品列表中添加商品即可
                if (num > 0){
                    orderItem = createOrderItemByItemAndNum(item,num);
                    cart.getOrderItemList().add(orderItem);
                }else {
                    throw new RuntimeException("商品购买数量不合法，购买失败");
                }
            }else {
                //2.2.2商家存在，商品存在；从商品列表中查询当前的商品并购买数量叠加，
                orderItem.setNum(orderItem.getNum()+num);
                double totalFee = orderItem.getPrice().doubleValue() * orderItem.getNum();
                orderItem.setTotalFee(new BigDecimal(totalFee));
                //如果叠加之后商品的购买数量为0的话，将该商品从该商家的商品列表中移除
                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果商家的商品列表数量为0之后需要将商家从购物车列表cartList中将商家(Cart)移除
                if (cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Override
    public void saveCartListByUsername(List<Cart> cartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username,cartList);
    }

    @Override
    public List<Cart> findCartListByUsername(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);
        if (cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        if (cookieCartList != null && cookieCartList.size()>0){
            for (Cart cart : cookieCartList){
                for (TbOrderItem orderItem : cart.getOrderItemList()){
                    addItemToCartList(redisCartList,orderItem.getItemId(),orderItem.getNum());
                }
            }
        }
        return redisCartList;
    }

    /**
     * 根据商品sku id查询商品订单对象
     * @param orderItemList 商品订单列表
     * @param itemId 商品sku id
     * @return 商品订单对象
     */
    private TbOrderItem findOrderItemInOrderItemListByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem :orderItemList){
            if (itemId.equals(orderItem.getItemId())){
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建商品订单对象
     * @param item 商品sku
     * @param num 购买数量
     * @return 商品订单对象
     */
    private TbOrderItem createOrderItemByItemAndNum(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setTitle(item.getTitle());
        orderItem.setNum(num);
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        //商品的小计 = 单价*购买数量
        double totalFee = orderItem.getPrice().doubleValue() * orderItem.getNum();
        orderItem.setTotalFee(new BigDecimal(totalFee));
        return orderItem;
    }

    /**
     * 从购物车列表中根据商家id查询购物车对象
     * @param cartList 购物车列表
     * @param sellerId  商家id
     * @return  购物车对象
     */
    private Cart findCartInCartListBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null && cartList.size()>0){
            for (Cart cart:cartList){
                if (sellerId.equals(cart.getSellerId())){
                    return cart;
                }
            }
        }
        return null;
    }
}
