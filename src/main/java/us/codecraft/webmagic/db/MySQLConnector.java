package us.codecraft.webmagic.db;

import java.sql.*;
import java.util.List;

import us.codecraft.webmagic.model.XGRemarkEntity;

public class MySQLConnector {
    private Connection conn = null;
    PreparedStatement statement = null;

    // connect to MySQL
    public MySQLConnector() {
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF8&useSSL=false&autoReconnect=true";
        String username = "root";
        String password = "p@ssw0rd"; // 加载驱动程序以连接数据库 
        try { 
            Class.forName("com.mysql.jdbc.Driver" ); 
            conn = DriverManager.getConnection( url,username, password ); 
            }
        //捕获加载驱动程序异常
         catch ( ClassNotFoundException cnfex ) {
             System.err.println(
             "装载 JDBC/ODBC 驱动程序失败。" );
             cnfex.printStackTrace(); 
         } 
         //捕获连接数据库异常
         catch ( SQLException sqlex ) {
             System.err.println( "无法连接数据库" );
             sqlex.printStackTrace(); 
         }
    }

    // disconnect to MySQL
    void deconnSQL() {
        try {
            if (conn != null)
                conn.close();
        } catch (Exception e) {
            System.out.println("关闭数据库问题 ：");
            e.printStackTrace();
        }
    }

    // execute selection language
    ResultSet selectSQL(String sql) {
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement(sql);
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    // execute insertion language
    boolean insertSQL(String sql) {
        try {
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("插入数据库时出错：");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("插入时出错：");
            e.printStackTrace();
        }
        return false;
    }
	
	
	public int batchInsertXGSQL(List<XGRemarkEntity> remarkList) {
		int row = 0;
		try {
			String sql = "insert into xg_spider_demo(title,score,model_num,high_comment_percent,image_url,create_time)values(?,?,?,?,?,now())";
			statement = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 0; i < remarkList.size(); i++) {
				XGRemarkEntity remarkEntity = remarkList.get(i);
				statement.setString(1, remarkEntity.getTitle());
				statement.setInt(2, remarkEntity.getScore());
				statement.setString(3, remarkEntity.getModelNum());
				statement.setString(4, remarkEntity.getHighCommentPercent());
				statement.setString(5, remarkEntity.getImageUrl());
				statement.addBatch();
			}
			int[] rows = statement.executeBatch();
			row = rows.length;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return row;
	}
    
    //execute delete language
    boolean deleteSQL(String sql) {
        try {
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("插入数据库时出错：");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("插入时出错：");
            e.printStackTrace();
        }
        return false;
    }
    //execute update language
    boolean updateSQL(String sql) {
        try {
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("插入数据库时出错：");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("插入时出错：");
            e.printStackTrace();
        }
        return false;
    }
}
