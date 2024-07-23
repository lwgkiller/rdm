package com.redxun.core.manager;

import java.io.Serializable;
import java.util.List;

import com.redxun.core.dao.mybatis.BaseMybatisDao;
import com.redxun.core.query.QueryFilter;

public abstract class ExtBaseManager<T> extends BaseManager<T> {

	public abstract BaseMybatisDao getMyBatisDao();

	public List<T> getMybatisAll(QueryFilter queryFilter) {
		return getMyBatisDao().getAll(queryFilter);
	}

	public Long getMybatisTotalItems(QueryFilter queryFilter) {
		return getMyBatisDao().getTotalItems(queryFilter);
	}

	@Override
	public void delete(String id) {
		getMyBatisDao().delete(id);
	}

	@Override
	public T get(String id) {
		return (T) getMyBatisDao().get(id);
	}

	

	@Override
	public void create(T  entity) {
		getMyBatisDao().create((Serializable) entity);
	}

	@Override
	public void update(T entity) {
		getMyBatisDao().update((Serializable) entity);
	}

	

	

	
}
