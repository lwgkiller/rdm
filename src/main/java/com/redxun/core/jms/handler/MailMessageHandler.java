package com.redxun.core.jms.handler;

import javax.annotation.Resource;

import com.redxun.core.engine.FreemarkEngine;
import com.redxun.core.jms.IMessageHandler;
import com.redxun.core.mail.MailTools;
import com.redxun.core.mail.model.Mail;
import com.redxun.core.util.StringUtil;

public class MailMessageHandler implements IMessageHandler {
	
	@Resource
	MailTools mailTools;
	@Resource
	FreemarkEngine freemarkEngine;

	@Override
	public void handMessage(Object obj) {
		if(!(obj instanceof Mail)) return ;
		Mail mail=(Mail)obj;
		try {
			String template=mail.getTemplate();
			if(StringUtil.isNotEmpty(template)){
				String content=freemarkEngine.mergeTemplateIntoString(template,mail.getVars());
				mail.setContent(content);
			}
			mailTools.send(mail);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

}
