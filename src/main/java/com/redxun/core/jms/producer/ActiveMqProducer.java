package com.redxun.core.jms.producer;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import com.redxun.core.jms.IMessageProducer;

/**
 *  消息生产者
 * @author ray
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class ActiveMqProducer implements IMessageProducer{
	
    protected Logger logger=LogManager.getLogger(ActiveMqProducer.class);
	
	
	@Resource
	private JmsTemplate jmsTemplate;

	/**
	 * 向指定的队列发送消息。
	 * @param destName	队列名称
	 * @param model		消息model。
	 */
	public void send(String destName,Object model){
		jmsTemplate.convertAndSend(destName, model);	
	}

	@Override
	public void send(Object model) {
		jmsTemplate.convertAndSend("defaultQueue", model);
		
	}
	
}
