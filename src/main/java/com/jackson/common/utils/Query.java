package com.jackson.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jackson.common.xss.SQLFilter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询参数
 */
public class Query<T> {

    public IPage<T> getPage(Map<String, Object> params) {
        return this.getPage(params, null, false);
    }

    public IPage<T> getPage(Map<String, Object> params, String defaultOrderField, boolean isAsc) {
        //分页参数
        long curPage = 1;
        long limit = 10;

        if(params.get(Constant.PAGE) != null){
            String pageParam = (String)params.get(Constant.PAGE);
            if(StringUtils.isNotBlank(pageParam)) {
                curPage = Long.parseLong(pageParam);
            }
        }
        if(params.get(Constant.LIMIT) != null){
            String limitParam = (String)params.get(Constant.LIMIT);
            if(StringUtils.isNotBlank(limitParam)) {
                limit = Long.parseLong(limitParam);
            }
        }

        //分页对象
        Page<T> page = new Page<>(curPage, limit);

        //分页参数
        params.put(Constant.PAGE, page);

        //排序字段
        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String orderField = SQLFilter.sqlInject((String)params.get(Constant.ORDER_FIELD));
        String order = (String)params.get(Constant.ORDER);


        //前端字段排序
        if(StringUtils.isNotEmpty(orderField) && StringUtils.isNotEmpty(order)){
            if(Constant.ASC.equalsIgnoreCase(order)) {
                return  page.addOrder(OrderItem.asc(orderField));
            }else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }

        //没有排序字段，则不排序
        if(StringUtils.isBlank(defaultOrderField)){
            return page;
        }

        //默认排序
        if(isAsc) {
            page.addOrder(OrderItem.asc(defaultOrderField));
        }else {
            page.addOrder(OrderItem.desc(defaultOrderField));
        }

        return page;
    }

    public IPage<Map<String, Object>> getPageMap(Map<String, Object> params) {
        //分页参数
        long curPage = 1;
        long limit = 10;

        if(params.get(Constant.PAGE) != null){
            String pageParam = (String)params.get(Constant.PAGE);
            if(StringUtils.isNotBlank(pageParam)) {
                curPage = Long.parseLong(pageParam);
            }
        }
        if(params.get(Constant.LIMIT) != null){
            String limitParam = (String)params.get(Constant.LIMIT);
            if(StringUtils.isNotBlank(limitParam)) {
                limit = Long.parseLong(limitParam);
            }
        }
        return new Page<Map<String, Object>>(curPage,limit);
    }

    public  IPage listToPage(List list, int pageNum, int pageSize){
        List pageList = new ArrayList<> ();
        int curIdx = pageNum > 1 ? (pageNum - 1) * pageSize : 0;
        for (int i = 0; i < pageSize && curIdx + i < list.size(); i++) {
            pageList.add( list.get(curIdx + i));
        }
        IPage page = new Page<> (pageNum, pageSize);
        page.setRecords(pageList);
        page.setTotal(list.size());
        return page;
    }
}
