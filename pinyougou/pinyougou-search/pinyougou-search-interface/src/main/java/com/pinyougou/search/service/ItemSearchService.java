package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据搜索关键字搜索商品列表
     * @param search
     * @return
     */
    Map<String,Object> search(Map<String,Object> search);
}
