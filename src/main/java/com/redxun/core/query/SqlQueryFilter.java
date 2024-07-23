package com.redxun.core.query;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过SQL拼接产生的查询过滤参数
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 *
 */
public class SqlQueryFilter {
	//分页信息
	private IPage page=new Page();
	//参数列表
	private Map<String,Object> params=new HashMap<String,Object>();
	//排序
	private SortParam sortParam=null;
	
	public SqlQueryFilter() {
		
	}

	public IPage getPage() {
		return page;
	}

	public void setPage(IPage page) {
		this.page = page;
	}

	public Map<String, Object> getParams() {
		if(getSortParam()!=null){
			params.put("orderByClause", getSortParam().toString());
		}
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public SortParam getSortParam() {
		return sortParam;
	}

	public void setSortParam(SortParam sortParam) {
		this.sortParam = sortParam;
	}
	
	/**
	 * 添加字段参数
	 * @param fieldName 字段名
	 * @param value 字段值
	 */
	public void addFieldParam(String fieldName,Object value){
		params.put(fieldName,value);
	}
	
	public void addSortParam(String fieldName,String ascDesc){
		sortParam=new SortParam(fieldName, ascDesc);
	}
	
}
