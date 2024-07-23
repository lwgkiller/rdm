package com.redxun.core.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.support.converter.MessageConverter;

/**
 * 消息转换器。 <br>
 * 
 * <pre>
 * 	1.转换发送消息。
 * 	2.转换接收消息。
 * </pre>
 * 
 * csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class ActiveMqMessageConverter implements MessageConverter {

	/**
	 * 转换发送消息。
	 */
	@Override
	public Message toMessage(Object object, Session session)
			throws JMSException {
		if (object != null && object.getClass() != null) {
			ObjectMessage objMsg = session.createObjectMessage();
			objMsg.setObject((Serializable) object);
			return objMsg;
		} else {
			throw new JMSException("Object:[" + object
					+ "] is not legal message");
		}
	}

	/**
	 * 转换接收消息。
	 */
	@Override
	public Object fromMessage(Message msg) throws JMSException {
		if (msg instanceof ObjectMessage) {
			ObjectMessage objMsg = (ObjectMessage) msg;
			Object object = objMsg.getObject();
			if (object != null && object.getClass() != null) {
				return object;
			} else {
				throw new JMSException("Object:[" + msg
						+ "] is not legal message");
			}
		} else {
			throw new JMSException("Msg:[" + msg + "] is not ObjectMessage");
		}
	}

}
