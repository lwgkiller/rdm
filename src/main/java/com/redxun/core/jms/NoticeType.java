package com.redxun.core.jms;
/**
 * 通知类型
 * @author csx
 * @Email: chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public enum NoticeType {
	SMS("sms","短信"),
	MAIL("mail","邮件"),
	IN_MSG("inner","内部消息");
	
	private String typeName;
	private String type="";
	
	NoticeType(String type,String typeName){
		this.typeName=typeName;
		this.type=type;
	}

	public String getTypeName() {
		return typeName;
	}
	
	public String getType() {
		return type;
	}
	
}
