package com.redxun.core.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *  打印JMS的出错异常
 *  @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class JmsExceptionListener  implements ExceptionListener {
	
	protected Logger logger=LogManager.getLogger(JmsExceptionListener.class);
	
	@Override
	public void onException(JMSException ex) {
		String msg=ExceptionUtils.getFullStackTrace(ex);
		logger.error(msg);
	}
}
