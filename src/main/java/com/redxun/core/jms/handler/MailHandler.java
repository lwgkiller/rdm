package com.redxun.core.jms.handler;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.MessagingException;

import com.redxun.core.engine.FreemarkEngine;
import com.redxun.core.jms.IJmsHandler;
import com.redxun.core.jms.MessageModel;
import com.redxun.core.jms.MessageUtil;
import com.redxun.core.mail.MailTools;
import com.redxun.core.mail.model.Mail;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.sys.org.entity.OsUser;

import freemarker.template.TemplateException;

/**
 * 邮件处理器。
 * @author ray
 *
 */
public class MailHandler implements IJmsHandler {
	
	@Resource
	MailTools mailTools;
	@Resource
	FreemarkEngine freemarkEngine;

	@Override
	public String getType() {
		return "mail";
	}

	@Override
	public String getName() {
		return "邮件";
	}

	@Override
	public void handleMessage(MessageModel messageModel) {
		
		Mail mail=null;
		try {
			mail = constructMailMsge(messageModel);
			if(mail==null) return ;
			mailTools.send(mail);;
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * 获取邮件地址。
	 * @param userList
	 * @return
	 */
	private  String getMailAddress(List<IUser> userList){
		if(BeanUtil.isEmpty(userList)) return "";
		String str="";
		for(IUser user:userList){
			OsUser osUser=(OsUser)user;
			if(StringUtil.isEmpty(osUser.getEmail())) continue;
			str+=osUser.getEmail() +",";
		}
		if(StringUtil.isEmpty(str)) return "";
		return StringUtil.trimSuffix(str, ",");
	}
	

	
	private Mail constructMailMsge(MessageModel model) throws MessagingException, IOException, TemplateException{
		Mail mail=new Mail();
		mail.setSubject(model.getSubject());
		
		OsUser osUser=(OsUser)model.getSender();
		
		mail.setSenderAddress(osUser.getEmail());
		
		String toEmail=getMailAddress(model.getRecieverList());
		String ccEmail=getMailAddress(model.getCcList());
		String bccEmail=getMailAddress(model.getBccList());
		
		if(StringUtil.isEmpty(toEmail) 
				&& StringUtil.isEmpty(ccEmail) 
				&& StringUtil.isEmpty(bccEmail)) return null;
		
		String content=MessageUtil.getContent(model, this.getType());
		mail.setContent(content);
		mail.setReceiverAddresses(toEmail);
		mail.setCopyToAddresses(ccEmail);
		

		return mail;
	}
}
