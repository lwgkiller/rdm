package com.redxun.core.manager;
import java.io.Serializable;
import java.util.List;

import com.redxun.core.query.IPage;
import com.redxun.core.query.QueryFilter;

/**
 * 基础服务类
 * @author mansan
 *@Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 * @param <T>
 * @param <PK>
 */
public interface IManager<T,PK extends Serializable>{

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
     * 按租用Id删除
     * @param tenantId
     */
    public void deleteByTenantId(String tenantId);
    
    /**
     * 按主键ID删除列表
     * @param pks
     */
    public void deleteByPks(PK... pks);
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
     * 創建或更新
     * @param entity
     */
    public void saveOrUpdate(T entity); 
    /**
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
     * 从对象缓存中移除关联的对象
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
}
