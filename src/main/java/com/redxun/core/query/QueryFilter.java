/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redxun.core.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.JoinType;

import com.redxun.core.util.BeanUtil;
import com.redxun.org.api.model.ITenant;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.WebAppUtil;

/**
 *
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class QueryFilter {

    //分页组件
    private IPage page = new Page();
    //逻辑组件查询
    private FieldLogic fieldLogic = new FieldLogic();
    //排序字段列表
    private List<SortParam> orderByList = new ArrayList<SortParam>();
    /**
     * 字段参数构建列表
     */
    private Map<String, Object> params = new LinkedHashMap<String, Object>();
    /**
     * 选择关联的属性
     */
    private String selectJoinAtt = null;
    /**
     * 返回选择的属性列表，通过','分隔
     */
    private String selectAtts=null;

    public QueryFilter() {

    }
    
    
    private Map<String,QueryParam> getQueryParams(FieldLogic logic){
    	Map<String,QueryParam> queryParams=new HashMap<String,QueryParam>();
    	for(int i=0;i<fieldLogic.getCommands().size();i++){
    		IWhereClause param=logic.getCommands().get(i);
    		if(param instanceof QueryParam){
    			QueryParam p=(QueryParam)param;
    			queryParams.put(p.getFieldName(), p);
    		}else if(param instanceof FieldLogic){
    			queryParams.putAll(getQueryParams((FieldLogic)param));
    		}
    	}
    	return queryParams;
    }
    
    
    public Map<String, QueryParam> getQueryParams() {
    	return getQueryParams(fieldLogic);
	}

	/**
     * 添加排序参数
     * @param fieldName
     * @param orderBy
     */
    public void addSortParam(String fieldName,String orderBy){
    	this.orderByList.add(new SortParam(fieldName,orderBy));
    }
    
    /**
     * 添加查询过滤器
     * @param clause
     */
    public void addWhereClause(IWhereClause clause){
    	fieldLogic.getCommands().add(clause);
    }
    
    /**
     * 增加字段查询
     * @param fieldName 文件名
     * @param val 值
     */
    public void addFieldParam(String fieldName,Object val){
       fieldLogic.getCommands().add(new QueryParam(fieldName,QueryParam.OP_EQUAL,val));
    }
    
    /**
     * 增加左模糊查询的字段查询
     * @param fieldName
     * @param val
     */
    public void addLeftLikeFieldParam(String fieldName,Object val){
    	fieldLogic.getCommands().add(new QueryParam(fieldName, QueryParam.OP_LEFT_LIKE, val));
    }

    /**
     * 增加右模糊查询的字段查询
     * @param fieldName
     * @param val
     */
    public void addRightLikeFieldParam(String fieldName,Object val){
        fieldLogic.getCommands().add(new QueryParam(fieldName, QueryParam.OP_RIGHT_LIKE, val));
    }

    /**
     * 增加左模糊查询的字段查询
     * @param fieldName
     * @param val
     */
    public void addLikeFieldParam(String fieldName,Object val){
    	fieldLogic.getCommands().add(new QueryParam(fieldName, QueryParam.OP_LIKE, val));
    }
    
    /**
     * 增加字段查询
     * @param fieldName 文件名
     * @param op 来自QueryParam.OP开头的属性值
     * @param val 值
     */
    public void addFieldParam(String fieldName,String op,Object val){
       fieldLogic.getCommands().add(new QueryParam(fieldName,op,val));
    }
    /**
     * 增加字段查询
     * @param fieldName 文件名
     * @param joinType 关联方式
     * @param op 来自QueryParam.OP开头的属性值
     * @param val 值
     */
    public void addFieldParam(String fieldName,JoinType joinType, String op,Object val){
       fieldLogic.getCommands().add(new QueryParam(fieldName,joinType,op,val));
    }
    
    /**
     * 加上租户的数据过滤，当为超级管理员时，可以查看公共的租户数据
     * @param tanentId
     */
    public void addTenantParam(String tanentId){
    	//若为当前租户为管理机构,则允许管理公共租户下的数据
		String curDomain=ContextUtil.getTenant().getDomain();
		if(WebAppUtil.getOrgMgrDomain().equals(curDomain)){
			FieldLogic orLogic=new FieldLogic(FieldLogic.OR);
			orLogic.getCommands().add(new QueryParam("tenantId", QueryParam.OP_EQUAL, tanentId));
			orLogic.getCommands().add(new QueryParam("tenantId", QueryParam.OP_EQUAL, ITenant.PUBLIC_TENANT_ID));
			 fieldLogic.getCommands().add(orLogic);
		}else{
			//加上TenantId租用ID
			addFieldParam("tenantId", tanentId);
		}	
    }

    public String getSelectAtts() {
		return selectAtts;
	}

	public void setSelectAtts(String selectAtts) {
		this.selectAtts = selectAtts;
	}
	
	public String getOrderBySql(){
		StringBuffer sb=new StringBuffer();
	 	//构建排序SQL
    	if(orderByList.size()>0){
			for(SortParam sortParam: orderByList){
				sb.append(sortParam.getSql()).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
    	}
    	return sb.toString();
	}

	/**
     * 返回分页组件
     *
     * @return
     */
    public IPage getPage() {
        return page;
    }

    public void setPage(IPage page) {
        this.page = page;
    }

    public FieldLogic getFieldLogic() {
        return fieldLogic;
    }

    public void setFieldLogic(FieldLogic fieldLogic) {
        this.fieldLogic = fieldLogic;
    }
    
    /**
     * 设置参数。
     */
    public void setParams(){
    	List<IWhereClause> list= fieldLogic.getCommands();
    	if(BeanUtil.isEmpty(list)) return ;
		for(IWhereClause tmp:list){
			if(!(tmp instanceof QueryParam)) continue;  
			
			QueryParam param=(QueryParam) tmp;
    		addParam(param.getFieldParam(),param.getValue());
    	}
    }

    public List<SortParam> getOrderByList() {
        return orderByList;
    }

    public void setOrderByList(List<SortParam> orderByList) {
        this.orderByList = orderByList;
    }

    public Map<String, Object> getParams() {
    	Map<String,QueryParam>  paramMap=getQueryParams();
    	for(QueryParam p:paramMap.values()){
    		params.put(p.getFieldName(), p.getValue());
    	}
        return params;
    }

    public void addParam(String name,Object val) {
        params.put(name, val);
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getSelectJoinAtt() {
        return selectJoinAtt;
    }

    public void setSelectJoinAtt(String selectJoinAtt) {
        this.selectJoinAtt = selectJoinAtt;
    }

}
