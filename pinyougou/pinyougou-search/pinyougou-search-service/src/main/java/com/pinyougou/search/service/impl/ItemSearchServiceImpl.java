package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.util.StringUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(interfaceClass = ItemSearchService.class)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    /**
     * 根据搜索关键字搜索商品列表
     *
     * @param search
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        Map<String,Object> resultMap = new HashMap<>();

        //关键字之间的空格问题
        if(!StringUtils.isEmpty(searchMap.get("keywords"))){
            searchMap.put("keywords",searchMap.get("keywords").toString().replaceAll(" ",""));
        }

        //创建高亮搜索对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        //设置查询条件
        Criteria criteria  = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //按照分类过滤查询
        if(!StringUtils.isEmpty(searchMap.get("category"))){
            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery categoryFilterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(categoryFilterQuery);
        }

        //按照品牌过滤
        if(!StringUtils.isEmpty(searchMap.get("brand"))){
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery brandFilterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(brandFilterQuery);
        }

        //按照规格过滤
        if(searchMap.get("spec") != null){
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            Set<Map.Entry<String, String>> entrySet = specMap.entrySet();
            for (Map.Entry<String,String> entry :entrySet){
                Criteria specCriteria = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                SimpleFilterQuery specFilterQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(specFilterQuery);
            }
        }

        //按照价格区间过滤
        if(!StringUtils.isEmpty(searchMap.get("price"))){
            //获取起始。结束价格
            String[] prices = searchMap.get("price").toString().split("-");

            //价格大于等于起始价格
            Criteria startPriceCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
            SimpleFilterQuery startPriceFilterQuery = new SimpleFilterQuery(startPriceCriteria);
            query.addFilterQuery(startPriceFilterQuery);

            //价格小于等于结束价格
            if(!"*".equals(prices[1])){
                Criteria endPriceCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                SimpleFilterQuery endPriceFilterQuery = new SimpleFilterQuery(endPriceCriteria);
                query.addFilterQuery(endPriceFilterQuery);
            }
        }

        //设置分页信息
        Integer pageNo = 1;
        Integer pageSize = 40;
        if(searchMap.get("pageNo") != null){
            pageNo = Integer.parseInt(searchMap.get("pageNo").toString());
        }
        if(searchMap.get("pageSize") != null){
            pageSize = Integer.parseInt(searchMap.get("pageSize").toString());
        }
        query.setOffset((pageNo-1)*pageSize);//起始索引号
        query.setRows(pageSize);//页大小

        //设置排序
        if(!StringUtils.isEmpty(searchMap.get("sortField")) && !StringUtils.isEmpty(searchMap.get("sort"))){
            String  sortOrder = searchMap.get("sort").toString();
            Sort sort = new Sort(sortOrder.equals("DESC")?Sort.Direction.DESC :Sort.Direction.ASC,"item_"+searchMap.get("sortField").toString());
            query.addSort(sort);
        }
        //设置高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);


        //查询
        HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //处理高亮标题
        List<HighlightEntry<TbItem>> highlightEntries = itemHighlightPage.getHighlighted();
        if(highlightEntries != null && highlightEntries.size()>0){
            for (HighlightEntry<TbItem> entry:highlightEntries){
                List<HighlightEntry.Highlight> highlights = entry.getHighlights();
                if(highlights != null && highlights.size() >0 && highlights.get(0).getSnipplets() != null){
                    //设置高亮标题
                    entry.getEntity().setTitle(highlights.get(0).getSnipplets().get(0));
                }
            }
        }

        //设置返回的 商品列表
        resultMap.put("rows",itemHighlightPage.getContent());
        //返回总页数
        resultMap.put("totalPages",itemHighlightPage.getTotalPages());
        //返回总记录数(总条数)
        resultMap.put("total",itemHighlightPage.getTotalElements());
        return resultMap;
    }

    @Override
    public void importItemList(List<TbItem> itemList) {
        if(itemList != null && itemList.size() > 0) {
            for (TbItem tbItem : itemList) {
                Map specMap = JSONObject.parseObject(tbItem.getSpec(), Map.class);
                tbItem.setSpecMap(specMap);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }
    }

    @Override
    public void deleteItemByGoodsIdList(List<Long> goodsIdList) {
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        SimpleFilterQuery query = new SimpleFilterQuery(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
