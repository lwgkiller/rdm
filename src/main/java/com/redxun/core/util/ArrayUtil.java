package com.redxun.core.util;

/**
 * 数组工具类
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class ArrayUtil {
	
	/**
	 * 实现在类似Javascript中的join方法
	 * @param arr 数组
	 * @param flag
	 * @return
	 */
	public static String join(Object[] arr, String flag) {
		
		StringBuffer strBuffer = new StringBuffer();

		for (int i = 0, len = arr.length; i < len; i++) {
			strBuffer.append(String.valueOf(arr[i]));
			if (i < len - 1)
				strBuffer.append(flag);
		}

		return strBuffer.toString();
	}
}
