package com.redxun.core.dao;

import java.io.Serializable;
import java.util.List;

import com.redxun.core.query.IPage;
import com.redxun.core.query.QueryFilter;

/**
 * 对数据库进行基本操作接口<br/>
 * 如CRUD的操作以及组合查询等
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public interface IDao<T,PK extends Serializable> {
     /**
     *  按ID获取实体
     * @param id
     * @return 
     */
    public T get(PK id);
    /**
     * 按ID删除实体
     * @param id 
     */
    public void delete(PK id);
    
    /**
     * 删除实体
     * @param entity 
     * void
     * @exception 
     * @since  1.0.0
     */
    public void deleteObject(T entity);
    /**
     * 按租户ID删除实体信息
     * @param tenantId
     */
    public void deleteByTenantId(String tenantId);
    /**
     * 按ID创建实体
     * @param entity 
     */
    public void create(T entity);
    
    
    /**
     * 更新实体
     * @param entity 
     */
    public void update(T entity);
    
    /**
     * update and skip the fields of update time and update by 
     * @param entity
     */
    public void updateSkipUpdateTime(T entity);
    /**
     * 保存或更新對象
     * @param entity
     */
    public void saveOrUpdate(T entity);
    
    /**
     * 查询所有实体列表
     * @return 
     */
    public List<T> getAll();
    
    /**
     * 按分页查询所有实体
     * @param page
     * @return 
     */
    public List<T> getAll(IPage page);

    
    /**
     * 查询列表
     * @param queryFilter
     * @return 
     */
    public List<T> getAll(QueryFilter queryFilter);
    
    /**
     * 按租用ID获取所有记录
     * @param tenantId
     * @return 
     * List<T>
     * @exception 
     * @since  1.0.0
     */
    public List<T> getAllByTenantId(String tenantId);
    
    /**
     * 按租用ID获取所有记录
     * @param tenantId
     * @param page
     * @return 
     * List<T>
     * @exception 
     * @since  1.0.0
     */
    public List<T> getAllByTenantId(String tenantId,IPage page);
    
    /**
     * 按租用ID、组合查询条件获取所有记录
     * @param tenantId
     * @param queryFilter
     * @return 
     * List<T>
     * @exception 
     * @since  1.0.0
     */
    public List<T> getAllByTenantId(String tenantId,QueryFilter queryFilter);
    
    /**
     * 获得查询中的过滤的总记录数
     * @param queryFilter
     * @return 
     */
    public Long getTotalItems(QueryFilter queryFilter);
    /**
     * 从缓存中移除关联的实体对象
     * @param obj 
     * void
     * @exception 
     * @since  1.0.0
     */
    public void detach(Object obj);
    
    /**
     * 清空缓存库并提交 
     * void
     * @exception 
     * @since  1.0.0
     */
    public void flush();
    /**
     * 清空缓存中的对象
     */
    public void clear();
}
