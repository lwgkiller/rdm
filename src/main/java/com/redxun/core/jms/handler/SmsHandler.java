package com.redxun.core.jms.handler;

import com.redxun.core.jms.IJmsHandler;
import com.redxun.core.jms.MessageModel;

/**
 * 短消息。
 * @author ray
 */
public class SmsHandler implements IJmsHandler {

	@Override
	public String getType() {
		return "sms";
	}

	@Override
	public String getName() {
		return "短消息";
	}

	@Override
	public void handleMessage(MessageModel messageModel) {
		System.out.println("短消息暂未实现!");
	}

}
