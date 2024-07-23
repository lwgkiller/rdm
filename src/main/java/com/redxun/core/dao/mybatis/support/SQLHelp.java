package com.redxun.core.dao.mybatis.support;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.redxun.core.dao.mybatis.dialect.Dialect;
import com.redxun.core.query.Page;
import org.springframework.jdbc.support.JdbcUtils;


/**
 * 统计总数，分页插件中使用。
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class SQLHelp {
    
    protected static Logger logger=LogManager.getLogger(SQLHelp.class);

    /**
     * 查询总纪录数
     *
     * @param sql             SQL语句
     * @param mappedStatement mapped
     * @param parameterObject 参数
     * @param boundSql        boundSql
     * @param dialect         database dialect
     * @return 总记录数
     * @throws java.sql.SQLException sql查询错误
     */
    public static int getCount(final String sql,
                               final MappedStatement mappedStatement, final Object parameterObject,
                               final BoundSql boundSql, Dialect dialect) throws SQLException {
    	
    	if(parameterObject!=null && parameterObject instanceof Map){
    		Map<String,Object> params=(Map<String,Object>)parameterObject;
    		
    		if(params.containsKey(Page.SKIP_COUNT)){
    			return 0;
    		}
    	}
    	
        final String count_sql = dialect.getCountString(sql);
        logger.debug("Total count SQL [{}] ", count_sql);
        logger.debug("Total count Parameters: {} ", parameterObject);
       

        PreparedStatement countStmt = null;
        ResultSet rs = null;
        Connection connection=null;
        DataSource dataSource=mappedStatement.getConfiguration().getEnvironment().getDataSource();
        try {
            connection=  DataSourceUtils.getConnection(dataSource);
            countStmt = connection.prepareStatement(count_sql);
            //Page SQL和Count SQL的参数是一样的，在绑定参数时可以使用一样的boundSql
            DefaultParameterHandler handler = new DefaultParameterHandler(mappedStatement,parameterObject,boundSql);
            handler.setParameters(countStmt);

            rs = countStmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            logger.debug("Total count: {}", count);
            return count;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (countStmt != null) {
                countStmt.close();
            }
            DataSourceUtils.releaseConnection(connection,dataSource);
        }
    }

}