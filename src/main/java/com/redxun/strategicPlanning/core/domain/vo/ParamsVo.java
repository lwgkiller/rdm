package com.redxun.strategicPlanning.core.domain.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.redxun.core.util.StringUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParamsVo implements Serializable {
    private static final long serialVersionUID = 5136297890600324867L;

    private Integer pageIndex;
    private Integer pageSize;
    private String sortField;
    private String sortOrder;
    private String filter;
    private List<SearchParamsData> searchConditions = new ArrayList<>();

    public List<SearchParamsData> getSearchConditions() {
        return searchConditions;
    }

    public void setSearchConditions(List<SearchParamsData> searchConditions) {
        this.searchConditions = searchConditions;
    }

    public ParamsVo() {
    }

    public ParamsVo(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, String filter) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.filter = filter;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getFilter() {
        if (StringUtil.isNotEmpty(filter)) {
            this.searchConditions = JSONArray.parseArray(filter, SearchParamsData.class);
        }
        return filter;
    }

    public void setFilter(String filter) {
        if(StringUtil.isNotEmpty(filter)) {
            this.searchConditions = JSONArray.parseArray(filter, SearchParamsData.class);
        }
        this.filter = filter;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pageIndex", pageIndex)
                .append("pageSize", pageSize)
                .append("sortField", sortField)
                .append("sortOrder", sortOrder)
                .append("filter", filter)
                .toString();
    }

}
