package com.redxun.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理类
 * @author lenovo
 *
 */
public class MsgUtil {
	private static ThreadLocal<List<String>>  messages = new ThreadLocal<List<String>>() ;
	
	public static String getMessage() {
		return getMessage("<br>");
	}
	
	public static String getMessage(String newLine) {
		List<String> list= messages.get();
		if(BeanUtil.isEmpty(list)) return "";
		StringBuffer sb = new StringBuffer();
		for(String message : messages.get()) {
			sb.append(message+newLine);
		}
		String msg = sb.toString();
		clear();
		return msg;
	}
	
	public static void setMessage(String message) {
		List<String> list= messages.get();
		if(list==null){
			list=new ArrayList<String>();
			messages.set(list);
		}
		list.add(message);
		
	}
	
	public static void clear() {
		messages.remove();
	}
}
