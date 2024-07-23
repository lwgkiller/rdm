package com.redxun.core.jms;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.redxun.core.engine.FreemarkEngine;
import com.redxun.core.util.AppBeanUtil;
import com.redxun.core.util.StringUtil;

import freemarker.template.TemplateException;

public class MessageUtil {
	
	public static String MESSAGE_INNER="inner";
	public static String MESSAGE_SMS="sms";
	public static String MESSAGE_MAIL="mail";
	
	
	/**
	 * 消息处理模型。
	 * @return
	 */
	public static Map<String,String> getMapMsgType(){
		Map<String,String> maps=new LinkedHashMap<String, String>();
		List<IJmsHandler> list=(List<IJmsHandler>) AppBeanUtil.getBean("jmsHandleList");
		for(IJmsHandler handler:list){
			maps.put(handler.getType(), handler.getName());
		}
		return maps;
	}
	
	/**
	 * 获取消息类型使用逗号分割。
	 * @return
	 */
	public static String getMsgType(){
		return  getMsgType("");
	}
	
	/**
	 * 获取消息类型。
	 * @param suffix
	 * @return
	 */
	public static String getMsgType(String suffix){
		List<IJmsHandler> list=(List<IJmsHandler>) AppBeanUtil.getBean("jmsHandleList");
		String msgType="";
		for(int i=0;i<list.size();i++){
			IJmsHandler handler=list.get(i);
			if(i==0){
				msgType=handler.getType() +suffix;
			}
			else{
				msgType+="," +handler.getType()+suffix;
			}
		}
		return msgType;
	}
	
	/**
	 * 根据模版别名获取模版路径。
	 * @param alias
	 * @return
	 */
	public static String  getFlowTemplateByAlias(String alias){
		return "flow/" + alias ;
	}
	
	/**
	 * 获取消息内容。
	 * @param model
	 * @param isHmtl
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String getContent(MessageModel model,String msgType) throws IOException, TemplateException{
		FreemarkEngine freemarkEngine =AppBeanUtil.getBean(FreemarkEngine.class);
		
		String content="";
		if(StringUtil.isNotEmpty(model.getTemplate())){
			content=freemarkEngine.parseByStringTemplate(model.getVars(), model.getTemplate());
		}
		else if(StringUtil.isNotEmpty(model.getTemplateAlias())){
			content=freemarkEngine.mergeTemplateIntoString(model.getTemplateAlias() +"/"+msgType+".ftl", model.getVars());
		}
		else{
			content=model.getContent();
		}
		return content;
	}
	
	/**
	 * 获取消息产生者。
	 * @return
	 */
	public static IMessageProducer getProducer(){
		IMessageProducer producer=AppBeanUtil.getBean(IMessageProducer.class);
		return producer;
	}
	
	

}
