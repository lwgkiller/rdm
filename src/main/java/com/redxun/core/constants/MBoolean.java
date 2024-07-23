package com.redxun.core.constants;
/**
 * 
 * <pre> 
 * 描述：Boolean 
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年9月16日-下午12:43:13
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public enum MBoolean {
	YES("YES"),
	NO("NO"),
	TRUE("TRUE"),
	FALSE("FALSE"),
	DISABLED("DISABLED"),
	ENABLED("ENABLED");
	
	private String val="";
	
	private MBoolean(String val){
		this.val=val;
	}
}