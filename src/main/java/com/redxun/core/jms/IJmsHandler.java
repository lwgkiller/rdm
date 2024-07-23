package com.redxun.core.jms;

/**
 *  处理消息
 * @author csx
 *@Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public interface IJmsHandler {
	
	/**
	 * 消息类型
	 * @return
	 */
	String getType();
	
	/**
	 * 消息名称
	 * @return
	 */
	String getName();
	
	/**
	 * 处理消息
	 * @param message
	 */
	void handleMessage(MessageModel messageModel);
}
