
package com.redxun.sys.org.manager;

import com.redxun.core.cache.CacheUtil;
import com.redxun.core.dao.IDao;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.query.QueryFilter;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.org.dao.LoginSessionUserDao;
import com.redxun.sys.org.entity.LoginSessionUser;
import com.redxun.sys.org.entity.OsUser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <pre> 
 * 描述：LOGIN_SESSION_USER 处理接口
 * 作者:szw
 * 日期:2019-06-11 11:30:37
 * 版权：广州红迅软件
 * </pre>
 */
@Service
public class LoginSessionUserManager extends MybatisBaseManager<LoginSessionUser> {
	protected Logger LOG= LogManager.getLogger(LoginSessionUserManager.class);
	@Resource
	private LoginSessionUserDao loginSessionUserDao;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected IDao getDao() {
		return loginSessionUserDao;
	}


	/**
	 * 登录授权数量拦截
	 * @param session
	 * @param user
	 * @return
	 */
	public String interceptLogin(HttpSession session, OsUser user){
		HashMap mymap =(HashMap) CacheUtil.getCache(LoginSessionUser.USERNO_MAP_SESSION);
		if(mymap!=null && mymap.size()>0 && LoginSessionUser.OULOGIN_NUM>0){
			int loginSize=mymap.size();
			if(loginSize> LoginSessionUser.OULOGIN_NUM){
				return "当前登录数量大于并发数量，不允许登录！";
			}else if(loginSize== LoginSessionUser.OULOGIN_NUM){
				LoginSessionUser loginUser = getAllByUserId(user.getUserId(), LoginSessionUser.LOGIN_TYPE);
				if(loginUser==null){
					return "当前登录数量等于并发数量，不允许登录！";
				}
			}
		}
		return null;
	}

	public Integer getLoginCount(){
		return (Integer) loginSessionUserDao.getOne("getLoginCount", null);
	}

	public List<LoginSessionUser> getLoginList(QueryFilter filter){
		Map<String,Object> params=filter.getParams();
		return loginSessionUserDao.getBySqlKey("getLoginList", params,filter.getPage());
	}

	public LoginSessionUser getLoginSessionUser(String uId){
		LoginSessionUser loginSessionUser = get(uId);
		return loginSessionUser;
	}

	@Override
	public void create(LoginSessionUser entity) {
		super.create(entity);
	}

	@Override
	public void update(LoginSessionUser entity) {
		super.update(entity);
	}


	public List<LoginSessionUser> getAllByLoginType(String sessionType){
		return loginSessionUserDao.getBySqlKey("getAllByLoginType",sessionType);
	}

	public LoginSessionUser getAllByUserId(String userId, String sessionType){
		Map<String, Object> params =new HashMap<>();
		params.put("userId",userId);
		params.put("sessionType",sessionType);
		return loginSessionUserDao.getUnique("getAllByUserId",params);
	}

	public LoginSessionUser getAllBySessionId(String sessionId){
		Map<String, Object> params =new HashMap<>();
		params.put("sessionId",sessionId);
		return loginSessionUserDao.getUnique("getAllBySessionId",params);
	}

	public void removeUser(String sessionId){
		LoginSessionUser user =getAllBySessionId(sessionId);
		if(user!=null){
			//变更登陆状态为  下线
			user.setSessionType(LoginSessionUser.OULOGIN_TYPE);
			user.setOuLoginFirstTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
			update(user);
		}

		HashMap mymap =(HashMap) CacheUtil.getCache(LoginSessionUser.USERNO_MAP_SESSION);
		if(mymap!=null){
			try {
				mymap.remove(sessionId);
				CacheUtil.addCache(LoginSessionUser.USERNO_MAP_SESSION,mymap);
			}catch (Exception e){
				LOG.error("---LoginSessionUserManager.removeUser is error: "+e.getMessage());
			}
		}
	}

	public void addOrUpdateSessionUser(HttpSession session, String ruquestIp, OsUser user){
		String sessionId =session.getId();
		List<LoginSessionUser> sessionUserList=getAllByLoginType(LoginSessionUser.LOGIN_TYPE);

		HashMap mymap =(HashMap) CacheUtil.getCache(LoginSessionUser.USERNO_MAP_SESSION);
		if(mymap==null){
			mymap =new HashMap();
		}
		mymap.put(sessionId,session);

		LoginSessionUser newSessionUser =new LoginSessionUser();
		creatSessionUser(newSessionUser,user.getUserId(),sessionId,ruquestIp);

		if(sessionUserList==null || sessionUserList.size()==0){
			updataCache(mymap);
		}else {
			Boolean isContain=false;
			for (LoginSessionUser sessionUsr:sessionUserList) {
				String oldSessionId =sessionUsr.getSessionId();
				if(user.getUserId().equals(sessionUsr.getUserId()) && !sessionId.equals(oldSessionId)){
					isContain=true;
					//变更登陆状态为  下线8
					sessionUsr.setSessionType(LoginSessionUser.OULOGIN_TYPE);
					sessionUsr.setOuLoginFirstTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
					update(sessionUsr);
					updataCache(mymap);

					//HttpSession oldSession =(HttpSession)mymap.get(oldSessionId);
					//已经存在登陆，旧session失效
					/*if(oldSession!=null){
						try {
							oldSession.invalidate();
						}catch (Exception e){
							LOG.error("---LoginSessionUserManager.addOrUpdateSessionUser is error: "+e.getMessage());
						}
					}*/
					break;
				}
			}
			if(!isContain) {
				updataCache(mymap);
			}
		}
		session.setAttribute(LoginSessionUser.USERNO_LOGIN, LoginSessionUser.USERNO_LOGIN);
	}

	private  void  updataCache(HashMap mymap){
		CacheUtil.addCache(LoginSessionUser.USERNO_MAP_SESSION,mymap);
	}

	private void creatSessionUser(LoginSessionUser newSessionUser, String userId, String sessionId, String ip){
		newSessionUser.setId(IdUtil.getId());
		newSessionUser.setUserId(userId);
		newSessionUser.setSessionId(sessionId);
		newSessionUser.setSessionIp(ip);
		String ddd =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		newSessionUser.setLoginFirstTime(ddd);
		newSessionUser.setSessionType(LoginSessionUser.LOGIN_TYPE);
		create(newSessionUser);
	}
}
