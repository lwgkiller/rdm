package com.redxun.core.jms;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 从消息队列中读取消息，并且找到对应的处理类进行处理
 * 
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class MessageConsumer {

	/**
	 * 处理消息
	 */
	private Map<String,IMessageHandler> handlersMap = new HashMap<String,IMessageHandler>();

	protected static Logger logger=LogManager.getLogger(MessageConsumer.class);
	
	public void setHandlersMap(Map<String,IMessageHandler> handlersMap) {
		this.handlersMap=handlersMap;
	}

	/**
	 * 发送消息
	 * @param model  发送的对象
	 * @throws Exception
	 */
	public void handleMessage(Object model) throws Exception {
		
		String className=model.getClass().getName();
		if(handlersMap.containsKey(className)){
			handlersMap.get(className).handMessage(model);
		}
		else{
			logger.debug("can't find name is " + className +"'s handler");
		}
	}
	
}
