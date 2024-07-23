package com.redxun.core.dao.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.redxun.core.dao.mybatis.domain.DefaultPage;
import com.redxun.core.dao.mybatis.domain.PageList;
import com.redxun.core.dao.mybatis.domain.PageResult;
import com.redxun.core.database.datasource.DataSourceUtil;
import com.redxun.core.database.util.SQLConst;
import com.redxun.core.entity.SqlModel;
import com.redxun.core.query.Page;
import com.redxun.core.query.QueryFilter;
import com.redxun.core.query.SortParam;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.util.WebAppUtil;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 通用查询操作。
 *
 * @param
 */
public class CommonDao {
	@Resource
	protected SqlSessionTemplate sqlSessionTemplate;
	@Resource(name="dataSource_Default")
	DataSource dataSource;

	protected static Logger logger = LogManager.getLogger(CommonDao.class);
	/**
	 *
	 */
	private static  Map<String,SqlSessionTemplate> sessionTemplateMap=new ConcurrentHashMap<>();


	private static final String NAME_SPACE = "com.redxun.sql.common"; // mybatis命名空间

	/**
	 * 执行sql语句
	 * @param sql
	 */
	public void execute(String sql) {
		execute(sql,sqlSessionTemplate);
	}

	/**
	 * 根据别名数据源执行SQL语句。
	 * @param alias
	 * @param sql
	 */
	public void execute(String alias,String sql) {
		if(StringUtil.isEmpty(alias)){
			execute(sql);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			execute(sql,sessionTemplate);
		}
	}

	private void execute(String sql,SqlSessionTemplate sessionTemplate) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("sql", sql);
		String key = getNameSpace("execute");
		sessionTemplate.update(key, map);
	}

	private static SqlSessionTemplate buildSqlSessionTemplate(String alias){
		try{
			if(sessionTemplateMap.containsKey(alias)){
				return  sessionTemplateMap.get(alias);
			}
			DataSource dataSource= DataSourceUtil.getDataSourceByAlias(alias);
			MyBatisSessionFactoryBean factoryBean=new MyBatisSessionFactoryBean();
			org.springframework.core.io.Resource configRes=new ClassPathResource("/config/mybatis-config.xml");
			factoryBean.setConfigLocation(configRes);
			factoryBean.setDataSource(dataSource);
			org.springframework.core.io.Resource[] maps=new org.springframework.core.io.Resource[1];
			org.springframework.core.io.Resource mapRes=new ClassPathResource("/config/mybatis-mappings.xml");
			maps[0]=mapRes;
			factoryBean.setMappingAllLocation(maps);
			SqlSessionTemplate sessionTemplate=new SqlSessionTemplate(factoryBean.buildSqlSessionFactory());
			sessionTemplateMap.put(alias,sessionTemplate);
			return  sessionTemplate;
		}
		catch (Exception ex){
			logger.error(ExceptionUtil.getExceptionMessage(ex));
			return null;
		}
	}



	/**
	 * 获取数据库类型
	 * @return
	 */
	public String getDbType(String alias){
		try {
			DataSource dataSource=DataSourceUtil.getDataSourceByAlias(alias);
			DruidDataSource druidDataSource=(DruidDataSource)dataSource;
			String dbType=druidDataSource.getDbType();
			return dbType;
		}
		catch (Exception ex){
			return WebAppUtil.getDbType();
		}

	}

	public String getDefaultDbType(){

		DruidDataSource druidDataSource=(DruidDataSource)dataSource;
		String dbType=druidDataSource.getDbType();
		return dbType;
	}

	/**
	 * 执行JDBC。
	 * @param model
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public void executeNamedSql(SqlModel model) throws IllegalAccessException, NoSuchFieldException{
		String dsName=model.getDsName();

		NamedParameterJdbcTemplate namedParameterJdbcTemplate=null;
		if(StringUtil.isEmpty(dsName)){
			namedParameterJdbcTemplate=AppBeanUtil.getBean(NamedParameterJdbcTemplate.class);
		}
		else{
			DataSource dataSource= DataSourceUtil.getDataSourceByAlias(dsName);
			namedParameterJdbcTemplate=new NamedParameterJdbcTemplate(dataSource);
		}
		namedParameterJdbcTemplate.update(model.getSql(), model.getParams());
	}


	/**
	 * 执行sql语句。
	 * <pre>
	 * 调用示例代码:
	 * String sql="insert into orders (ID_,NAME_,ALIAS_) values (#{id},#{name},#{alias})";
	 *	Map<String,Object> params=new HashMap<String, Object>();
	 *	params.put("id", "2");
	 *	params.put("name", "红迅采购JSAAS合同");
	 *	params.put("alias", "redxun");
	 *	commonDao.execute(sql, params);
	 * </pre>
	 * @param sql
	 * @param params
	 */
	public void execute(String sql,Map<String,Object> params) {
		execute(sql,params,sqlSessionTemplate);
	}

	/**
	 * 执行sql
	 * @param alias	数据源别名
	 * @param sql		sql语句
	 * @param params	参数
	 */
	public void execute(String alias, String sql,Map<String,Object> params) {
		//如果别名为空直接插入当前的数据库。
		if(StringUtil.isEmpty(alias)){
			execute(sql,params);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			execute(sql,params,sessionTemplate);
		}
	}

	/**
	 * 执行sql
	 * @param sql					sql 语句
	 * @param params				参数map
	 * @param sessionTemplate	sessionTemplate
	 */
	private void execute(String sql,Map<String,Object> params,SqlSessionTemplate sessionTemplate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		if(BeanUtil.isNotEmpty(params)){
			map.putAll(params);
		}
		String key = getNameSpace("execute");
		sessionTemplate.update(key, map);
	}




	/**
	 * 根据sqlmodel 执行sql操作。
	 * @param sqlModel
	 */
	public void execute(SqlModel sqlModel) {
		execute( sqlModel, sqlSessionTemplate );
	}

	/**
	 * 执行SQL语句
	 * @param alias		数据源别名
	 * @param sqlModel	需要执行的sqlModel对象
	 */
	public void execute(String alias, SqlModel sqlModel) {
		if(StringUtil.isEmpty(alias)){
			this.execute(sqlModel);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			execute( sqlModel, sessionTemplate );
		}
	}

	/**
	 * 执行sql语句
	 * @param sqlModel
	 * @param sessionTemplate
	 */
	private void execute(SqlModel sqlModel, SqlSessionTemplate sessionTemplate ) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sqlModel.getSql());
		if(BeanUtil.isNotEmpty(sqlModel.getParams())){
			map.putAll(sqlModel.getParams());
		}
		String key = getNameSpace("execute");
		sessionTemplate.update(key, map);
	}

	/**
	 * 查询列表，返回数据为List，列表的数据结构为Map对象实例。
	 *
	 * @param sql
	 * @return List
	 */
	public List query(String sql) {
		return query(sql,sqlSessionTemplate);
	}

	/**
	 * 执行查询语句
	 * @param alias	数据源别名
	 * @param sql		需要执行的SQL
	 * @return
	 */
	public List query(String alias,String sql) {
		if(StringUtil.isEmpty(alias)){
			return this.query(sql);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			return query(sql,sessionTemplate);
		}
	}

	private List query(String sql,SqlSessionTemplate sessionTemplate) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("sql", sql);
		String key = getNameSpace("query");
		return sessionTemplate.selectList(key, map);
	}



	/**
	 * 根据sql 和参数查询数据，返回数据为List，中间的数据结构为Map。
	 * <pre>
	 * String sql="select * from orders where id_=#{id}";
	 *	Map<String,Object> params=new HashMap<String, Object>();
	 *	params.put("id", "2");
	 *	List list= commonDao.query(sql, params);
	 *	System.out.println(list);
	 * </pre>
	 * @param sql
	 * @param params
	 * @return
	 */
	public List query(String sql,Map<String,Object> params) {
		return query(sql,params,sqlSessionTemplate);
	}

	/**
	 * 执行查询
	 * @param alias		数据源别名
	 * @param sql			sql语句
	 * @param params		查询参数
	 * @return
	 */
	public List query(String alias,String sql,Map<String,Object> params) {
		if(StringUtil.isEmpty(alias)){
			return this.query(sql,params);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			return query(sql,params,sessionTemplate);
		}
	}

	private List query(String sql,Map<String,Object> params,SqlSessionTemplate sessionTemplate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		if(BeanUtil.isNotEmpty(params)){
			map.putAll(params);
		}
		String key = getNameSpace("query");
		return sessionTemplate.selectList(key, map);
	}


	/**
	 * 根据sqlModel查询列表数据。
	 * @param sqlModel
	 * @return
	 */
	public List query(SqlModel sqlModel){
		return query(sqlModel,sqlSessionTemplate);
	}

	/**
	 * 执行sql查询
	 * @param alias		数据源别名
	 * @param sqlModel	sql查询对象
	 * @return
	 */
	public List query(String  alias, SqlModel sqlModel){
		if(StringUtil.isEmpty(alias)){
			return  this.query(sqlModel);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			return query(sqlModel,sessionTemplate);
		}
	}

	private List query(SqlModel sqlModel, SqlSessionTemplate sessionTemplate){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sqlModel.getSql());
		if(BeanUtil.isNotEmpty(sqlModel.getParams())){
			map.putAll(sqlModel.getParams());
		}
		String key = getNameSpace("query");
		return sessionTemplate.selectList(key, map);
	}



	/**
	 * 根据sqlmodel 查询单条记录或者单个值。
	 * 这个值可以是map，或者 为单个值，比如汇总等。
	 * @param sqlModel
	 * @return
	 */
	public Object queryOne(SqlModel sqlModel){
		return  queryOne(sqlModel,sqlSessionTemplate);
	}

	/**
	 * 执行sql查询。
	 * @param alias		数据源别名
	 * @param sqlModel	查询对象
	 * @return
	 */
	public Object queryOne(String  alias, SqlModel sqlModel){
		if(StringUtil.isEmpty(alias)){
			return this.queryOne(sqlModel);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			return  queryOne(sqlModel,sessionTemplate);
		}

	}

	private Object queryOne(SqlModel sqlModel, SqlSessionTemplate sessionTemplate){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sqlModel.getSql());
		if(BeanUtil.isNotEmpty(sqlModel.getParams())){
			map.putAll(sqlModel.getParams());
		}
		String key = getNameSpace("queryObject");
		return sessionTemplate.selectOne(key, map);
	}

	/**
	 * 返回一行数据。
	 * @param sqlModel
	 * @return
	 */
	public Map queryForMap(SqlModel sqlModel){
		return queryForMap(sqlModel,sqlSessionTemplate);
	}

	/**
	 * 返回一行数据。
	 * @param alias		数据源别名
	 * @param sqlModel   sql查询对象
	 * @return
	 */
	public Map queryForMap(String alias,SqlModel sqlModel){
		if(StringUtil.isEmpty(alias)){
			return  this.queryForMap(sqlModel);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			return queryForMap(sqlModel,sessionTemplate);
		}
	}

	private Map queryForMap(SqlModel sqlModel, SqlSessionTemplate sessionTemplate){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sqlModel.getSql());
		if(BeanUtil.isNotEmpty(sqlModel.getParams())){
			map.putAll(sqlModel.getParams());
		}
		String key = getNameSpace("query");
		return sessionTemplate.selectOne(key, map);
	}


	/**
	 * 查询分页列表
	 * @param sql
	 * @param params
	 * @param page
	 * @return
	 */
	public PageList query(String sql, Map<String,Object> params, Page page) {
		return  query(sql,params,page, sqlSessionTemplate);
	}

	public PageList query(String alias, String sql, Map<String,Object> params, Page page) {
		if(StringUtil.isEmpty(alias)){
			return this.query(sql,params,page);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			return  query(sql,params,page, sessionTemplate);
		}
	}

	private PageList query(String sql, Map<String,Object> params, Page page, SqlSessionTemplate sessionTemplate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		if(BeanUtil.isNotEmpty(params)){
			map.putAll(params);
		}
		String key = getNameSpace("query");
		return (PageList) sessionTemplate.selectList(key, map,
				new RowBounds(page.getStartIndex(), page.getPageSize()));
	}


	/**
	 * 合并mybatis 命名空间
	 *
	 * @param sqlKey
	 * @return
	 */
	private String getNameSpace(String sqlKey) {
		return NAME_SPACE + "." + sqlKey;
	}

	/**
	 * 自定义SQL,分页由系统,可自动过滤排序grid规则，详细运用请看：com.hotent.demo.querysql.controller
	 *
	 * <pre>
	 * QueryFilter queryFilter = getQuerFilter(request);
	 * String sql = &quot;SELECT * FROM xog_user WHERE 1=#{test} order by user_id_ &quot;;
	 * Map&lt;String, Object&gt; params = new HashMap&lt;String, Object&gt;();
	 * params.put(&quot;test&quot;, 1);
	 * List list = commonDao.queryForList(sql, queryFilter, params);
	 * return new PageJson(list);
	 * </pre>
	 *
	 * @param sql
	 * @param queryFilter
	 * @param params
	 * @return
	 */
	public List queryForList(String sql, QueryFilter queryFilter, Map<String, Object> params) {
		return  queryForList(sql,queryFilter,params,sqlSessionTemplate);
	}

	public List queryForList(String alias, String sql, QueryFilter queryFilter, Map<String, Object> params) {
		if(StringUtil.isEmpty(alias)){
			return  this.queryForList(sql,queryFilter,params);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			return  queryForList(sql,queryFilter,params,sessionTemplate);
		}
	}

	private List queryForList(String sql, QueryFilter queryFilter, Map<String, Object> params, SqlSessionTemplate sessionTemplate) {
		if (CollectionUtils.isEmpty(params)) {
			params = new HashMap();
		}
		params.put("sql", sql);
		Map<String, Object> filter = parseGridFilter(queryFilter);
		params.putAll(filter);
		String dbType = getDefaultDbType();

		if (SQLConst.DB_SQLSERVER2005.equalsIgnoreCase(dbType)) {
			return sessionTemplate.selectList(getNameSpace("queryDynamic"), params);
		} else {
			return sessionTemplate.selectList(getNameSpace("queryList"), params);
		}
	}

	/**
	 * 动态查询列表
	 * @param sql
	 * @param filter
	 * @param params
	 * @return
	 */
	public PageList queryDynamicList(String sql, QueryFilter filter, Map<String,Object> params){
		return  queryDynamicList(sql,filter,params,sqlSessionTemplate);
	}

	public PageList queryDynamicList(String alias, String sql, QueryFilter filter, Map<String,Object> params){
		if(StringUtil.isEmpty(alias)){
			return queryDynamicList(sql,filter,params);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			return  queryDynamicList(sql,filter,params,sessionTemplate);
		}
	}


	private PageList queryDynamicList(String sql, QueryFilter filter, Map<String,Object> params, SqlSessionTemplate sessionTemplate){

		if (CollectionUtils.isEmpty(params)) {
			params = new HashMap<String, Object>();
		}

		Map<String, Object> newParams = this.parseGridFilter(filter);
		params.putAll(newParams);
		//构建SQL的各部分
		buildSqlClause(sql,params);

		DefaultPage p = new DefaultPage(new RowBounds(filter.getPage().getStartIndex(), filter.getPage().getPageSize()));
		return (PageList) sessionTemplate.selectList(this.getNameSpace("queryDynamic"), params, p);
	}



	/**
	 * 构建动态的SQL查询参数结构
	 * @param sql
	 * @param params
	 */
	private void buildSqlClause(String sql,Map<String,Object> params){
		sql=sql.replaceAll("(?is)\\s+", " ");
		String nSql=sql.toUpperCase();
		//nSql=nSql.replaceAll("(?is)\\s+", " ");
		int lWhereIndex=nSql.lastIndexOf(" WHERE ");
		int orderByIndex=nSql.lastIndexOf(" ORDER BY ");

		String whereClause=null;
		String orderClause=null;
		String selectClause=null;

		if(lWhereIndex!=-1 && orderByIndex!=-1){
			whereClause=sql.substring(lWhereIndex, orderByIndex);
			selectClause=sql.substring(0,lWhereIndex);
		}else if(lWhereIndex!=-1 && orderByIndex==-1){
			whereClause=sql.substring(lWhereIndex);
			selectClause=sql.substring(0,lWhereIndex);
		}else if(lWhereIndex==-1 && orderByIndex!=-1){
			whereClause=" WHERE 1=1 ";
			selectClause=sql.substring(0,orderByIndex);
		}else{
			whereClause=" WHERE 1=1 ";
			selectClause=sql;
		}

		if(params.containsKey("whereSql")){
			whereClause=whereClause + " AND (" + params.get("whereSql") + ")";
		}
		//若外部没有传入该排序参数，并且原SQL中带有order by ，则加入Order by 参数
		if(!params.containsKey("orderBySql") && orderByIndex!=-1){
			orderClause=sql.substring(orderByIndex);
			params.put("orderBySql",orderClause);
		}else if(params.containsKey("orderBySql")){
			params.put("orderBySql"," ORDER BY " + params.get("orderBySql"));
		}
		params.put("sql", selectClause);
		params.put("whereSql",whereClause);
	}

	/**
	 * 自定义SQL,分页由系统,可自动过滤排序grid规则，详细运用请看：com.hotent.demo.querysql.controller
	 *
	 * <pre>
	 * QueryFilter queryFilter = getQuerFilter(request);
	 * String sql = &quot;SELECT * FROM xog_user WHERE 1=#{test} order by user_id_ &quot;;
	 * Map&lt;String, Object&gt; params = new HashMap&lt;String, Object&gt;();
	 * params.put(&quot;test&quot;, 1);
	 * PageList list = commonDao.queryForListPage(sql, queryFilter, params);
	 * return new PageJson(list);
	 * </pre>
	 *
	 * @param sql
	 * @param queryFilter
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageList queryForListPage(String sql, QueryFilter queryFilter, Map<String, Object> params) {
		return  queryForListPage(sql,queryFilter,params,sqlSessionTemplate);
	}

	public PageList queryForListPage(String  alias, String sql, QueryFilter queryFilter, Map<String, Object> params) {
		if(StringUtil.isEmpty(alias)){
			return queryForListPage(sql,queryFilter,params);
		}
		else{
			SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
			return  queryForListPage(sql,queryFilter,params,sessionTemplate);
		}
	}

	private PageList queryForListPage(String sql, QueryFilter queryFilter, Map<String, Object> params, SqlSessionTemplate sessionTemplate) {
		//Assert.notNull(sql);
		if (CollectionUtils.isEmpty(params)) {
			params = new HashMap<String, Object>();
		}
		params.put("sql", sql);

		Map<String, Object> filter = this.parseGridFilter(queryFilter);
		params.putAll(filter);
		DefaultPage p = new DefaultPage(new RowBounds(queryFilter.getPage().getStartIndex(), queryFilter.getPage().getPageSize()));
		return (PageList) sessionTemplate.selectList(this.getNameSpace("queryList"), params, p);
	}



	/**
	 *
	 * 自定义SQL 自分页,不可自动过滤排序grid规则，需要用户自己动手构建grid过滤排序规则，
	 * </br>详细运用请看：com.hotent.demo.querysql.controller
	 * </br>因为用户自定义分页的话，会造出Grid的过滤规则和统计规则出错,即自定义分页的语句必须在过滤条件的后面 </br>例如：select *
	 * from (select * from xog_user limit 0,10 ) where sex like '%男%' 和
	 * </br>select * from (select * from xog_user where sex like '%男%' limit
	 * 0,10 )的两种不同 结果集
	 *
	 * <pre>
	 * <h1>用户自分页可以优化查询语句的执行效率，例如
	 * </br>select * from (select t.*,rownum as rn from xog_user t) a where a.rn >=1 and a.rn<=10 和
	 * </br>select * from (select t.*,rownum as rn from xog_user t where rownum <=10) a where a.rn>=1
	 * </br>后者要比前者快多
	 * </h1>
	 * <h1>mysql：</h1>
	 * QueryFilter queryFilter = getQuerFilter(request);
	 * int startIndex = queryFilter.getPage().getStartIndex();
	 * int pageSize = queryFilter.getPage().getPageSize();
	 * Map&lt;String, Object&gt; params = commonDao.parseGridFilter(queryFilter);
	 * params.put(&quot;test&quot;, 1); // 自定义的参数
	 * params.put(&quot;startIndex&quot;, startIndex); // 设置分页参数
	 * params.put(&quot;pageSize&quot;, pageSize); // 设置分页参数
	 * String whereSql = (String) params.get(&quot;whereSql&quot;); // 字段过滤规则
	 * String orderBySql = (String) params.get(&quot;orderBySql&quot;); // 字段排序规则
	 *
	 * StringBuffer sbf = new StringBuffer(&quot;SELECT * FROM xog_user&quot;);
	 * sbf.append(&quot; WHERE 1=#{test} &quot;);
	 * if (StringUtil.isNotEmpty(whereSql)) {
	 * 	sbf.append(&quot; and &quot;).append(whereSql); // grid过滤的固定写法
	 * }
	 * if (StringUtil.isNotEmpty(orderBySql)) {
	 * 	sbf.append(&quot; ORDER BY &quot;).append(orderBySql); // grid过滤的固定写法
	 * } else {
	 * 	sbf.append(&quot; ORDER BY user_id_ &quot;);
	 * }
	 * String querySql = sbf.toString() + &quot; limit #{startIndex},#{pageSize} &quot;;
	 * String countSql = sbf.toString();
	 *
	 * PageList list = commonDao.queryByCusPage(querySql, countSql, params,
	 * 		startIndex, pageSize);
	 * return new PageJson(list);
	 *
	 * <h1>oracle:</h1>
	 *
	 * QueryFilter queryFilter = getQuerFilter(request);
	 * int startIndex = queryFilter.getPage().getStartIndex();
	 * int pageSize = queryFilter.getPage().getPageSize();
	 * Map&lt;String, Object&gt; params = commonDao.parseGridFilter(queryFilter);
	 * params.put(&quot;test&quot;, 1); // 自定义的参数
	 * params.put(&quot;startIndex&quot;, startIndex); // 设置分页参数
	 * params.put(&quot;pageSize&quot;, pageSize); // 设置分页参数
	 * String whereSql = (String) params.get(&quot;whereSql&quot;); // 字段过滤规则
	 * String orderBySql = (String) params.get(&quot;orderBySql&quot;); // 字段排序规则
	 *
	 * StringBuffer sbf = new StringBuffer("SELECT * FROM ( SELECT T.*,ROWNUM AS RN FROM SYS_USER T  ");
	 * sbf.append(&quot; WHERE 1=#{test} &quot;);
	 * if (StringUtil.isNotEmpty(whereSql)) {
	 * 	sbf.append(&quot; and &quot;).append(whereSql); // grid过滤的固定写法
	 * }
	 * if (StringUtil.isNotEmpty(orderBySql)) {
	 * 	sbf.append(&quot; ORDER BY &quot;).append(orderBySql); // grid过滤的固定写法
	 * } else {
	 * 	sbf.append(&quot; ORDER BY user_id_ &quot;);
	 * }
	 * String querySql =  sbf.toString() + " ROWNUM <=10) A WHERE A.RN>=1  ";
	 * String countSql = sbf.toString() + ")";
	 *
	 * PageList list = commonDao.queryByCusPage(querySql, countSql, params,
	 * 		startIndex, pageSize);
	 * return new PageJson(list);
	 * </pre>
	 *
	 * @param sql
	 * @param countSql
	 * @param params
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageList queryByCusPage(String sql, String countSql, Map<String, Object> params, Integer startIndex, Integer pageSize) {
		List list = this.query(sql, params);
		Integer tc = this.getCount(countSql, params);
		return new PageList(list, new PageResult(startIndex, pageSize, tc));
	}


	public PageList queryByCusPage(String alias, String sql, String countSql, Map<String, Object> params, Integer startIndex, Integer pageSize) {
		if(StringUtil.isEmpty(alias)){
			return this.queryByCusPage(sql,countSql,params,startIndex,pageSize);
		}
		else{
			List list = this.query(alias, sql, params);
			Integer tc = this.getCount(alias, countSql, params);
			return new PageList(list, new PageResult(startIndex, pageSize, tc));
		}
	}


	/**
	 * 返回统计数据
	 *
	 * @param sql
	 * @param params
	 * @return
	 */
	private Integer getCount(String sql, Map<String, Object> params) {
		return  getCount(sql,params,sqlSessionTemplate);
	}

	private Integer getCount(String alias, String sql, Map<String, Object> params) {
		SqlSessionTemplate sessionTemplate=buildSqlSessionTemplate(alias);
		return  getCount(sql,params,sessionTemplate);
	}

	private Integer getCount(String sql, Map<String, Object> params,SqlSessionTemplate sessionTemplate) {
		if (CollectionUtils.isEmpty(params)) {
			params = new HashMap<String, Object>();
		}
		params.put("sql", sql);
		return sessionTemplate.selectOne(this.getNameSpace("getCount"), params);
	}



	/**
	 * 解析 grid 过滤、排序规则
	 *
	 * @param queryFilter
	 * @return
	 */
	public Map<String, Object> parseGridFilter(QueryFilter queryFilter) {
		queryFilter.setParams();
		Map<String, Object> params = queryFilter.getParams();
		// 构建动态条件SQL
		String dynamicWhereSql = queryFilter.getFieldLogic().getSql();
		if (StringUtil.isNotEmpty(dynamicWhereSql)) {
			params.put("whereSql", dynamicWhereSql);
		}

		if(queryFilter.getOrderByList().size()>0){
	    	StringBuffer sb=new StringBuffer();
			for(SortParam sortParam: queryFilter.getOrderByList()){
				sb.append(sortParam.getSql()).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			params.put("orderBySql", sb.toString());
    	}

		return params;
	}
}
