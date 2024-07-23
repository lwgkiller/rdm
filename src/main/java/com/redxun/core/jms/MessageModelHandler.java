package com.redxun.core.jms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.redxun.core.util.BeanUtil;

/**
 * 处理消息 MessageModel 。
 * @author ray
 *
 */
public class MessageModelHandler implements IMessageHandler {
	
	protected Logger logger=LogManager.getLogger(MessageModelHandler.class);
	
	private Map<String,IJmsHandler> handlersMap = new HashMap<String,IJmsHandler>();
	
	public void setHandlers(List<IJmsHandler>  handlers) {
		for(IJmsHandler handler:handlers){
			handlersMap.put(handler.getType(), handler);
		}
	}

	@Override
	public void handMessage(Object model) {
		MessageModel msgModel=(MessageModel)model;
		String types=msgModel.getType();
		if(BeanUtil.isEmpty(types)) return;
		String[] aryType=types.split(",");
		for(String type:aryType){
			IJmsHandler hander=handlersMap.get(type);
			if(hander==null) continue;
			hander.handleMessage(msgModel);
		}

	}

}
