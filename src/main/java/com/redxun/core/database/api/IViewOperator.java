package com.redxun.core.database.api;

import java.sql.SQLException;
import java.util.List;

import com.redxun.core.database.api.model.Table;

/**
 * 视图接口定义类。
 * <p>
 * 1.获取数据库视图列表数据。
 * <p>
 * 2.获取某个视图的具体信息，数据保存到Table中。
 * 
 * <pre>
 * 作者：redxun
 * 版权：广州红迅软件有限公司版权所有
 * </pre>
 * 
 */
public interface IViewOperator extends IDbType{
	

	/**
	 * 创建或者替换视图
	 * 
	 * @param viewName
	 * @param sql
	 * @throws Exception
	 */
	void createOrRep(String viewName, String sql) throws Exception;

	/**
	 * 使用模糊匹配，获取系统视图名称。
	 * 
	 * @return
	 * @throws Exception 
	 */
	List<String> getViews(String viewName) throws  Exception;

	

	/**
	 * 根据视图名称，使用精确匹配，获取视图详细信息。
	 * @param viewName
	 * @return
	 */
	Table getModelByViewName(String viewName) throws SQLException;

	/**
	 * 根据视图名，使用模糊匹配，称获取视图详细信息。
	 * 
	 * @param viewName		视图名称
	 * @return
	 */
	List<Table> getViewsByName(String viewName)	throws Exception;

}
