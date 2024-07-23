package com.redxun.core.database.api;

import java.util.List;

import com.redxun.core.database.api.model.Table;

public interface IDbMeta {
	
	/**
	 * 根据名称模糊获取所有对象
	 * @param name
	 * @return
	 * @throws Exception
	 */
	List<Table> getObjectsByName(String name) throws Exception;
	
	Table getByName(String name);
	
	/**
	 * 根据名称获取表模型对象
	 * @param name
	 * @return
	 */
	Table getModelByName(String name) throws Exception;
}
