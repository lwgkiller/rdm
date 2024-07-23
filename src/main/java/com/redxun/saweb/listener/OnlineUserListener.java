package com.redxun.saweb.listener;


import com.redxun.core.util.AppBeanUtil;
import com.redxun.sys.org.entity.LoginSessionUser;
import com.redxun.sys.org.manager.LoginSessionUserManager;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 在线人数监控
 * @author cmc
 *
 */
public class OnlineUserListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent event) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void sessionDestroyed(HttpSessionEvent se) {
        LoginSessionUserManager loginSessionUserManager = AppBeanUtil.getBean(LoginSessionUserManager.class);;
        String sessionType =(String)se.getSession().getAttribute(LoginSessionUser.USERNO_LOGIN);
        if(LoginSessionUser.USERNO_LOGIN.equals(sessionType)){
            loginSessionUserManager.removeUser(se.getSession().getId());
        }
    }
}
