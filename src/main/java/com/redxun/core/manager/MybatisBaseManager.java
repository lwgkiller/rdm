package com.redxun.core.manager;

import java.io.Serializable;
import java.util.List;

import com.redxun.core.dao.mybatis.BaseMybatisDao;
import com.redxun.core.query.QueryFilter;

public abstract class MybatisBaseManager<T> extends BaseManager<T> {
	

	public List<T> getMybatisAll(QueryFilter queryFilter) {
		return getDao().getAll(queryFilter);
	}

	public Long getMybatisTotalItems(QueryFilter queryFilter) {
		return getDao().getTotalItems(queryFilter);
	}

	@Override
	public void delete(String id) {
		getDao().delete(id);
	}

	@Override
	public T get(String id) {
		return (T) getDao().get(id);
	}

	

	@Override
	public void create(T  entity) {
		getDao().create((Serializable) entity);
	}

	@Override
	public void update(T entity) {
		getDao().update((Serializable) entity);
	}

	

	

	
}
