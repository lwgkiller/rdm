package com.redxun.core.query;

/**
 *  分页接口
 * @author csx
 *@Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public interface IPage {
	public final static Integer DEFAULT_PAGE_SIZE=20;
	//忽略计算总记录数
	public final static String SKIP_COUNT="_skipCount";
	//每页记录数
	public Integer getPageSize();
	//页码索引
	public Integer getPageIndex();
	//分页的开始记录的索引值
	public Integer getStartIndex();
	//总记录数
	public Integer getTotalItems();
	//是否忽略统计总记录数
	public boolean isSkipCountTotal();
}
