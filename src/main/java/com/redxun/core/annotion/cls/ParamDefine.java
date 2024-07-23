package com.redxun.core.annotion.cls;
/**
 * 变量定义
 * @author mansan
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public @interface ParamDefine {
	/**
	 * 变量标题
	 * @return
	 */
	public String title();
	/**
	 * 变量名
	 * @return
	 */
	public String varName();
}
