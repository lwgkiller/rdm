package com.redxun.core.manager;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.redxun.core.dao.IDao;
import com.redxun.core.jms.MessageModelHandler;
import com.redxun.core.query.IPage;
import com.redxun.core.query.QueryFilter;

/**
 *
 * <pre>
 * 描述：业务访问基础类，实现对应的类时，请继承此类
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:keith@mitom.cn
 * 日期:2014年5月11日-下午10:08:27
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public abstract class GenericManager<T, PK extends Serializable> implements IManager<T, PK> {

	protected Logger logger=LogManager.getLogger(MessageModelHandler.class);


	/**
	 * 获取访问数据库的Dao类
	 *
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected abstract IDao getDao();

	@SuppressWarnings("unchecked")
	public T get(PK id) {
		return (T) getDao().get(id);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void delete(PK id) {
		getDao().delete(id);
	}

	@Override
	public void deleteByTenantId(String tenantId) {
		getDao().deleteByTenantId(tenantId);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void deleteByPks(PK... pks) {
		for (PK pk : pks) {
			getDao().delete(pk);
		}
	}

	/**
     * 删除实体
     * @param entity
     * void
     * @exception
     * @since  1.0.0
     */
    @SuppressWarnings("unchecked")
	public void deleteObject(T entity){
    	getDao().deleteObject(entity);
    }

    @Transactional
	@SuppressWarnings("unchecked")
	public void create(T entity) {
		getDao().create(entity);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void saveOrUpdate(T entity){
		getDao().saveOrUpdate(entity);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void update(T entity) {
		getDao().update(entity);
	}

	 /**
     * update and skip the fields of update time and update by
     * @param entity
     */
    @SuppressWarnings("unchecked")
	public void updateSkipUpdateTime(T entity){
    	getDao().updateSkipUpdateTime(entity);
    }

	@SuppressWarnings({ "unchecked" })
	public List<T> getAll() {
		return getDao().getAll();
	}

	@SuppressWarnings({ "unchecked" })
	public List<T> getAll(IPage page) {
		return getDao().getAll(page);
	}

	@SuppressWarnings({ "unchecked" })
	public List<T> getAll(QueryFilter queryFilter) {
		return getDao().getAll(queryFilter);
	}

	@SuppressWarnings({ "unchecked" })
	public List<T> getAllByTenantId(String tenantId) {
		return getDao().getAllByTenantId(tenantId);
	}
	@SuppressWarnings({ "unchecked" })
	public List<T> getAllByTenantId(String tenantId, IPage page) {
		return getDao().getAllByTenantId(tenantId, page);
	}

	@SuppressWarnings({ "unchecked" })
	public List<T> getAllByTenantId(String tenantId,QueryFilter filter){
		return getDao().getAllByTenantId(tenantId, filter);
	}

	public void detach(Object obj){
		getDao().detach(obj);
	}

	/**
     * 清空缓存库并提交
     * void
     * @exception
     * @since  1.0.0
     */
	public void flush(){
            getDao().flush();
	}

    public Long getTotalItems(QueryFilter queryFilter){
        return getDao().getTotalItems(queryFilter);
    }
}
