package com.redxun.core.jms;
/**
 *  消息产生接口类
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public interface IMessageProducer {
	/**
	 * 发送消息模型
	 * @param model
	 */
	void send(String topic,Object model);
	
	/**
	 * 默认发送到 defaultTopic
	 * @param model
	 */
	void send(Object model);
}
