package com.redxun.core.jms;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redxun.org.api.model.IUser;

/**
 * 消息对象。
 * @author ray
 *
 */
public  class MessageModel  implements Serializable {
	private static final long serialVersionUID = -8061958875748204361L;
	
	/**
	 * 消息类型使用逗号分割。
	 */
	private String types;
	/**
	 * 消息主题
	 */
	private String subject="";
	/**
	 * 接收人
	 */
	private List<IUser> recieverList;
	/**
	 * 抄送人
	 */
	private List<IUser> ccList;
	/**
	 * 密送人
	 */
	private List<IUser> bccList;
	/**
	 * 消息内容
	 */
	private String content="";
	/**
	 * 发送人
	 */
	private IUser sender;
	/**
	 * 发送变量。
	 */
	private Map<String, Object>  vars=new HashMap<String, Object>();
	/**
	 * 发送附件
	 */
	private File[] attachs;
	private IUser reply;
	/**
	 * 模版别名
	 */
	private String templateAlias="";
	
	private String template="";
	
	
	public MessageModel(){}
	
	
	public MessageModel(String types, String subject, List<IUser> recieverList, String content, IUser sender) {
		this.types = types;
		this.subject = subject;
		this.recieverList = recieverList;
		this.content = content;
		this.sender = sender;
	}
	
	
	
	public MessageModel(String types, String subject, List<IUser> recieverList, List<IUser> ccList, List<IUser> bccList,
			String content, IUser sender, Map<String, Object> vars) {
		this(types,subject,recieverList,content,sender);
		this.ccList = ccList;
		this.bccList = bccList;
	}


	public String getType() {
		return types;
	}
	public void setType(String type) {
		this.types = type;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public List<IUser> getRecieverList() {
		return recieverList;
	}
	public void setRecieverList(List<IUser> recieverList) {
		this.recieverList = recieverList;
	}
	public List<IUser> getCcList() {
		return ccList;
	}
	public void setCcList(List<IUser> ccList) {
		this.ccList = ccList;
	}
	public List<IUser> getBccList() {
		return bccList;
	}
	public void setBccList(List<IUser> bccList) {
		this.bccList = bccList;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public IUser getSender() {
		return sender;
	}
	public void setSender(IUser sender) {
		this.sender = sender;
	}
	public Map<String, Object> getVars() {
		return vars;
	}
	public void setVars(Map<String, Object> vars) {
		this.vars = vars;
	}
	
	public File[] getAttachs() {
		return attachs;
	}

	public void setAttachs(File[] attachs) {
		this.attachs = attachs;
	}


	public IUser getReply() {
		return reply;
	}


	public void setReply(IUser reply) {
		this.reply = reply;
	}


	public String getTemplateAlias() {
		return templateAlias;
	}


	public void setTemplateAlias(String templateAlias) {
		this.templateAlias = templateAlias;
	}


	public String getTemplate() {
		return template;
	}


	public void setTemplate(String template) {
		this.template = template;
	} 
	

}
