package com.redxun.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sql 解析工具类
 * @author chshx
 *
 */
public class SqlParseUtil {
	/**
	 * 解析SQL
	 * @param orgSql 原来SQL  如 insert into demo(t1,t2) values({t1},{t2})
	 * @param orgParams 源参数
	 * @param newParams 输出参数
	 * @return insert into demo1(t1,t2) values(?,?)
	 */
	public static String parseSql(String orgSql,Map<String,Object> orgParams,Map<String,Object> newParams){
		Map<String,Object> params=new HashMap<String,Object>();
		String regx="\\{.*?\\}";
		Pattern p=Pattern.compile(regx);
		String str=orgSql.toString();
		Matcher m=p.matcher(str);
		while(m.find()) {
			String field=m.group();
			String key=field.substring(1, field.length()-1);
			Object paramVal=orgParams.get(key);
			if(paramVal!=null){
				newParams.put(key,paramVal);
			}
		}
		return str.replaceAll(regx,"?");
	}
	
	public static void main(String[]args){
//		Map<String,Object> ps=new HashMap();
//		Map<String,Object> ps2=new HashMap();
//		ps.put("a","a1");
//		ps.put("b","b1");
//		ps.put("c","c1");
		String sql="insert into a(a,b)values(#{},#{b})";
		
		List<String> params=getSqlParams(sql);
		for(String k:params){
			System.out.println("key:"+k);
		}
	}
	
	/**
	 * 解析SQL
	 * @param orgSql 原来SQL  如 insert into demo(t1,t2) values(#{t1},#{t2})
	 * @param orgParams 源参数
	 * @param newParams 输出参数
	 * @return t1,t2的列表
	 */
	public static List<String> getSqlParams(String orgSql){
		List<String> paramList=new ArrayList<String>();
		String regx="#\\{.*?\\}";
		Pattern p=Pattern.compile(regx);
		String str=orgSql.toString();
		Matcher m=p.matcher(str);
		while(m.find()) {
			String field=m.group();
			String key=field.substring(2, field.length()-1);
			paramList.add(key);
		}
		return paramList;
	}
	
	/**
	 * 执行SQL
	 * @param sql
	 * @param orgParams
	 */
	public static void executeDynamicSql(String sql,Map<String,Object> orgParams) throws Exception{
		
	}
}
