package com.redxun.saweb.util;

import com.baidu.fsg.uid.UidGenerator;
import com.redxun.core.util.AppBeanUtil;

/**
 * Id产生工具类
 * @author mansan
 *  @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用
 */
public class IdUtil {
	
	/**
	 * ID产生器。
	 * @return
	 */
	public static String getId(){
		UidGenerator idGenerator=AppBeanUtil.getBean(UidGenerator.class);
		return String.valueOf( idGenerator.getUID());
	}
}
