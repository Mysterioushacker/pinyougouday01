package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

public interface CartService {

    /**
     * 根据商品 id 查询商品和购买数量加入到 cartList
     * @param cartList 购物车列表
     * @param itemId 商品 id
     * @param num 购买数量
     * @return 购物车列表
     */
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    void saveCartListByUsername(List<Cart> cartList, String username);

    List<Cart> findCartListByUsername(String username);

    List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList);

}
