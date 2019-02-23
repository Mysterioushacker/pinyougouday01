package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {

    //在浏览器中购物车对应的cookie名称
    private static final String COOKIE_CART_LIST = "PYG_CART_LIST";

    //在浏览器中购物车最大的生存时间；1周
    private static final int COOKIE_CART_LIST_MAX_AGE = 60*60*24*7;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    @GetMapping("/addItemToCartList")
    public Result addItemToCartList(Long itemId,Integer num){
        Result result = Result.fail("加入购物车失败.");
        try{
            //如果没有登录则用户名为anonymousUser
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //1.获取购物车列表
            List<Cart> cartList = findCartList();
            //2.将当前要加入到购物车的商品id和数量添加到购物车列表
            cartList = cartService.addItemToCartList(cartList,itemId,num);

            //判断是否匿名登录
            if ("anonymousUser".equals(username)) {
                //未登录；购物车数据存入到cookie
                CookieUtils.setCookie(request, response, COOKIE_CART_LIST, JSON.toJSONString(cartList), COOKIE_CART_LIST_MAX_AGE, true);
            }else {
                //已登录，购物车数据存入到redis
                cartService.saveCartListByUsername(cartList,username);
            }
            return Result.ok("加入购物车成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取购物车列表数据，如果登录了则从redis中获取，如果未登录则从cookie中获取
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList(){
        try {
            //如果没有登录则用户名为anonymousUser
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //未登录；购物车数据来自cookie
            List<Cart> cookieCartList = new ArrayList<>();
            //获取cookie中的购物车json格式字符串并转换为列表
            String cartListJsonStr = CookieUtils.getCookieValue(request,COOKIE_CART_LIST,true);

            //判断购物车列表是否为空
            if (!StringUtils.isEmpty(cartListJsonStr)){
                cookieCartList = JSONArray.parseArray(cartListJsonStr,Cart.class);
            }

            //判断是否匿名登录
            if("anonymousUser".equals(username)){
                return cookieCartList;
            }else {
                //用户已经登录;购物车数据来自redis
                List<Cart> redisCartList = cartService.findCartListByUsername(username);

                if (cookieCartList.size()>0){
                    //合并列表
                    redisCartList = cartService.mergeCartList(cookieCartList,redisCartList);
                    //保存数据到redis中
                    cartService.saveCartListByUsername(redisCartList,username);
                    //删除cookie中的购物车
                    CookieUtils.deleteCookie(request,response,COOKIE_CART_LIST);
                }
                return redisCartList;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 获取当前登录用户信息显示在前端页面
     * @return
     */
    @GetMapping("/getUsername")
    public Map<String,Object> getUsername(){
        Map<String,Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果未登录，则获取到的用户名为anoymousUser
        map.put("username",username);
        return map;
    }

}
