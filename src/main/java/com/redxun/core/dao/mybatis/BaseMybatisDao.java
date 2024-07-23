package com.redxun.core.dao.mybatis;

import java.util.Date;

import com.redxun.core.entity.BaseEntity;
import com.redxun.core.query.QueryFilter;
import com.redxun.saweb.context.ContextUtil;
/**
 * 
 * @author mansan
 *
 * @param <T>
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public abstract class BaseMybatisDao<T extends BaseEntity> extends MyBatisDao<T,String> {
	
	@Override
	public void deleteObject(T entity) {
		delete(entity.getPkId().toString());
	}
	
	@Override
	public void deleteByTenantId(String tenantId) {
		
	}

	@Override
	public Long getTotalItems(QueryFilter queryFilter) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void saveOrUpdate(T entity) {
		if(entity.getPkId()==null){
			entity.setPkId(idGenerator.getSID());
			entity.setCreateTime(new Date());
			entity.setCreateBy(ContextUtil.getCurrentUserId());
			create(entity);
		}else{
			entity.setUpdateTime(new Date());
			entity.setUpdateBy(ContextUtil.getCurrentUserId());
			update(entity);
		}
	}

}
