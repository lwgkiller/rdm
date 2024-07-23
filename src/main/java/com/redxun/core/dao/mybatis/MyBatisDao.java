package com.redxun.core.dao.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.redxun.core.dao.IDao;
import com.redxun.core.dao.mybatis.domain.PageList;
import com.redxun.core.database.datasource.DataSourceUtil;
import com.redxun.core.entity.BaseEntity;
import com.redxun.core.entity.BaseTenantEntity;
import com.redxun.core.query.*;
import com.redxun.core.seq.IdGenerator;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.WebAppUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <pre> 
 * 描述：MyBatis的DAO实现
 * 作者：csx
 * 邮箱: chshxuan@163.com
 * 日期:2014年10月29日-上午11:26:24
 * 版权：redxun Developer Team
 * </pre>
 */
public abstract class MyBatisDao<T extends Serializable,PK extends Serializable>  implements IDao<T, PK>{
	@Resource
	protected SqlSessionTemplate sqlSessionTemplate;

	@Resource
	IdGenerator idGenerator;
	
    /**
     * 按ID获取单一记录
     */
    protected final String OP_GET=".get";
    /**
     * 按业务主键获取单一记录
     */
    protected final String OP_GET_BY_SQLKEY=".getBySqlKey";    
    /*
     * 按ID删除记录
     */
    protected final String OP_DEL=".remove";
    /**
     * 按租用ID删除记录 
     */
    protected final String OP_DEL_BY_TENANTID=".delByTenantId";
    /**
     * 按ID更新记录
     */
    protected final String OP_UPD=".update";
    /**
     * 添加记录
     */
    protected final String OP_CREATE=".create";
    /**
     * 查询记录列表
     */
    protected final String OP_QUERY=".query";
    
    /**
     * 返回当前实体的命名空间字符串名称
     */
    public abstract String getNamespace();
 
    @Override
    public T get(PK id) {
    	 T entity=this.sqlSessionTemplate.selectOne(getNamespace() + OP_GET,id);
         return entity;
    }
    
    @Override
    public void create(T entity) {
    	//如果继承BaseTenantEntity 则获取当前上下文的租户ID填入。
    	if(entity instanceof BaseTenantEntity){
    		BaseTenantEntity ent=(BaseTenantEntity)entity;
    		String tenantId=ContextUtil.getCurrentTenantId();
    		String userId=ContextUtil.getCurrentUserId();
    		if(StringUtils.isBlank(ent.getTenantId())){
    			ent.setTenantId(tenantId);
    		}
    		if(StringUtils.isBlank(ent.getCreateBy())){
    			ent.setCreateBy(userId);
    		}
    		if(ent.getCreateTime()==null){
    			ent.setCreateTime(new Date());
    		}
    		
    	}
    	else if(entity instanceof BaseEntity){
    		BaseEntity ent=(BaseEntity)entity;
    		String userId=ContextUtil.getCurrentUserId();
    		if(StringUtils.isBlank(ent.getCreateBy())){
    			ent.setCreateBy(userId);
    		}
    		if(ent.getCreateTime()==null){
    			ent.setCreateTime(new Date());
    		}
    	}
    	this.sqlSessionTemplate.insert(getNamespace() + OP_CREATE, entity);
    }
    
    @Override
    public void delete(PK id) {
    	this.sqlSessionTemplate.delete(getNamespace() + OP_DEL, id);
    }
    
    @Override
    public void deleteByTenantId(String tenantId) {
    	this.sqlSessionTemplate.delete(getNamespace() + OP_DEL_BY_TENANTID,tenantId);
    }
    
    @Override
    public void detach(Object obj) {
    	//MyBatis不需要实现该方法
    	return;
    }
    
    @Override
    public void clear() {
    	//MyBatis不需要实现该方法
    	return;
    }
    
    @Override
    public void flush() {
    	// MyBatis不需要实现该方法
    	return;
    }
  
    @Override
    public List<T> getAll() {
    	 List<T> list=this.sqlSessionTemplate.selectList(getNamespace() + OP_QUERY, null);
         return list;
    }
    
    @Override
    public List<T> getAll(IPage page) {
    	PageList<T> list= (PageList)this.sqlSessionTemplate.selectList(getNamespace() + OP_QUERY, null,new RowBounds(page.getStartIndex(), page.getPageSize()));
        ((Page)page).setTotalItems(list.getPageResult().getTotalCount());
    	return list;
    }
    
    @Override
    public List<T> getAllByTenantId(String tenantId) {
    	Map<String,Object> params=new HashMap<String,Object>();
    	//构建动态条件SQL
    	if(StringUtils.isNotEmpty(tenantId)){
    		params.put("whereSql", "TENANT_ID_=#{tenantId}");
    		params.put("tenantId", tenantId);
    	}
    	List<T> list=this.sqlSessionTemplate.selectList(getNamespace() + OP_QUERY, params);
    	return list;
    }
    
    @Override
	public List<T> getAllByTenantId(String tenantId, IPage page) {
    	Map<String,Object> params=new HashMap<String,Object>();
    	//构建动态条件SQL
    	if(StringUtils.isNotEmpty(tenantId)){
    		params.put("whereSql", "TENANT_ID_=#{tenantId}");
    		params.put("tenantId", tenantId);
    	}
    	PageList<T> list= (PageList)this.sqlSessionTemplate.selectList(getNamespace() + OP_QUERY, params,
    			new RowBounds(page.getStartIndex(),page.getPageSize()));
    	((Page)page).setTotalItems(list.getPageResult().getTotalCount());
    	return list;
	}
    
    @Override
	public List<T> getAllByTenantId(String tenantId, QueryFilter queryFilter) {
		queryFilter.addParam("tenantId", tenantId);
		return getAll(queryFilter);
	}
    
    @Override
    public List<T> getAll(QueryFilter queryFilter) {
    	constructQueryFilter(queryFilter);
    	Map<String,Object> params=queryFilter.getParams();
    	int page=queryFilter.getPage().getStartIndex();
    	int pageSize=queryFilter.getPage().getPageSize();
    	@SuppressWarnings("unchecked")
		PageList<T> list= (PageList)this.sqlSessionTemplate.selectList(getNamespace() + OP_QUERY, params,
                new RowBounds(page, pageSize));
        ((Page)queryFilter.getPage()).setTotalItems(list.getPageResult().getTotalCount());
        return list;
    }
    
    private void constructQueryFilter(QueryFilter queryFilter){
    	queryFilter.setParams();
    	Map<String,Object> params=queryFilter.getParams();
    	//构建动态条件SQL
    	String dynamicWhereSql=queryFilter.getFieldLogic().getSql();
    	
    	if(StringUtils.isNotEmpty(dynamicWhereSql)){
    		params.put("whereSql", dynamicWhereSql);
    	}
    	//构建排序SQL
    	if(queryFilter.getOrderByList().size()>0){
	    	StringBuffer sb=new StringBuffer();
			for(SortParam sortParam: queryFilter.getOrderByList()){
				sb.append(sortParam.getSql()).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			
			String orderSql=sb.toString();
			orderSql= orderSql.replaceAll("[--]","").replaceAll("'", "''").replaceAll(";", "");
			
			boolean foundMatch = orderSql.matches("(?i)delete\\s+|select\\s+|update\\s+");
			if(foundMatch){
				throw new RuntimeException("发现SQL注入");
			}
			
			params.put("orderBySql", orderSql);
    	}
    }
    
    
    
    @Override
    public void update(T entity) {
    	
    	if(entity instanceof BaseTenantEntity){
    		BaseTenantEntity ent=(BaseTenantEntity)entity;
    		String tenantId=ContextUtil.getCurrentTenantId();
    		String userId=ContextUtil.getCurrentUserId();
    		if(StringUtils.isBlank(ent.getTenantId())){
    			ent.setTenantId(tenantId);
    		}
    		ent.setUpdateBy(userId);
    		ent.setUpdateTime(new Date());
    	}
    	else if(entity instanceof BaseEntity){
    		BaseEntity ent=(BaseEntity)entity;
    		String userId=ContextUtil.getCurrentUserId();
    		ent.setUpdateBy(userId);
    		ent.setUpdateTime(new Date());
    	}
    	
    	this.sqlSessionTemplate.update(getNamespace() + OP_UPD, entity);
    }
    
    @Override
    public void updateSkipUpdateTime(T entity) {
    	this.sqlSessionTemplate.update(getNamespace() + OP_UPD, entity);
    }
    
    /**
     * 根据在Map配置文件中的Sql Key及参数获取实列表
     * @param sqlKey
     * @param params
     * @return
     */
    public List<T> getBySqlKey(String sqlKey,Map<String,Object> params){
    	return this.sqlSessionTemplate.selectList(getNamespace() + "." + sqlKey , params);
    }
    
    /**
     * 根据sqlKey 获取列表。
     * @param sqlKey
     * @param params
     * @return
     */
    public List<T> getBySqlKey(String sqlKey,Object params){
    	if(params instanceof QueryFilter){
    		constructQueryFilter( (QueryFilter) params);
    		QueryFilter filter= (QueryFilter) params;
    		return this.sqlSessionTemplate.selectList(getNamespace() + "." + sqlKey , filter.getParams());
    	}
    	else{
    		return this.sqlSessionTemplate.selectList(getNamespace() + "." + sqlKey , params);
    	}
    }
    
    /**
     * 获取分页数据。
     * @param sqlKey		分页key
     * @param queryFilter	分页数据
     * @return
     */
    public List<T> getPageBySqlKey(String sqlKey,QueryFilter queryFilter){
    	
    	constructQueryFilter(queryFilter);
    	Map<String,Object> params=queryFilter.getParams();
    	int page=queryFilter.getPage().getStartIndex();
    	int pageSize=queryFilter.getPage().getPageSize();
    	
    	if(queryFilter.getPage() ==null ||  queryFilter.getPage().isSkipCountTotal()){
    		params.put(IPage.SKIP_COUNT, "true");
    	}
    	
    	PageList<T> list= (PageList)this.sqlSessionTemplate.selectList(getNamespace() + "." + sqlKey, params, new RowBounds(page, pageSize));
        ((Page)queryFilter.getPage()).setTotalItems(list.getPageResult().getTotalCount());
        
        return list;
    }
    
    public List<T> getPageBySqlKey(String sqlKey,SqlQueryFilter queryFilter){
    	Map<String,Object> params=queryFilter.getParams();
    	int page=queryFilter.getPage().getStartIndex();
    	int pageSize=queryFilter.getPage().getPageSize();
    	
    	PageList<T> list= (PageList)this.sqlSessionTemplate.selectList(getNamespace() + "." + sqlKey, params, new RowBounds(page, pageSize));
        ((Page)queryFilter.getPage()).setTotalItems(list.getPageResult().getTotalCount());
        
        return list;
    }
    
    /**
     * 根据在Map配置文件中的Sql Key及参数分页获取实列表
     * @param sqlKey
     * @param params
     * @param iPage
     * @return
     */
    public List<T> getBySqlKey(String sqlKey,Map<String,Object> params,IPage iPage){
    	//不计算分页
    	if(iPage.isSkipCountTotal()){
    		params.put(IPage.SKIP_COUNT, "true");
    	}
    	PageList<T> list= (PageList)this.sqlSessionTemplate.selectList(getNamespace() + "." + sqlKey , params,new RowBounds(iPage.getStartIndex(), iPage.getPageSize()));
    	((Page)iPage).setTotalItems(list.getPageResult().getTotalCount());
    	return list;
    }
    /**
     * 根据在Map配置文件中的Sql Key及参数更新数据
     * @param sqlKey
     * @param params
     */
    public int updateBySqlKey(String sqlKey,Map<String,Object> params){
    	int rtn=this.sqlSessionTemplate.update(getNamespace() + "." + sqlKey, params);
    	return rtn;
    }
    /**
     * 根据在Map配置文件中的Sql Key更新数据
     * @param sqlKey
     */
    public void updateBySqlKey(String sqlKey){
    	this.sqlSessionTemplate.update(getNamespace() + "." + sqlKey);
    }
    /**
     * 根据在Map配置文件中的Sql Key及参数删除数据
     * @param sqlKey
     * @param params
     */
    public void deleteBySqlKey(String sqlKey,Map<String,Object> params){
    	this.sqlSessionTemplate.delete(getNamespace() + "." + sqlKey, params);
    }
    
    public void deleteBySqlKey(String sqlKey,Object param){
    	this.sqlSessionTemplate.delete(getNamespace() + "." + sqlKey, param);
    }
    /**
     * 根据在Map配置文件中的Sql Key删除数据
     * @param sqlKey
     */
    public void deleteBySqlKey(String sqlKey){
    	this.sqlSessionTemplate.delete(getNamespace() + "." + sqlKey);
    }

    /**
     * 查询单条记录。
     * @param sqlKey
     * @param params
     * @return
     */
    public T getUnique(String sqlKey,Map<String,Object> params){
    	return this.sqlSessionTemplate.selectOne(getNamespace() + "." + sqlKey,params);
    }
    
    /**
     * 返回单个值，比如统计数量等。
     * @param sqlKey
     * @param params
     * @return
     */
    public Object getOne(String sqlKey,Object params){
    	return this.sqlSessionTemplate.selectOne(getNamespace() + "." + sqlKey,params);
    }


	/**
	 * 获取当前数据库类型。
	 * @return
	 */
	public String getDbType(){
		try{
			DruidDataSource druidDataSource=(DruidDataSource)DataSourceUtil.getDataSourceByAlias("dataSource_Default");
			String dbType=druidDataSource.getDbType();
			return dbType;
		}
		catch (Exception ex){
			return WebAppUtil.getDbType();
		}


	}
}
