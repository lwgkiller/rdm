
/**
 * 
 * <pre> 
 * 描述：LOGIN_SESSION_USER DAO接口
 * 作者:szw
 * 日期:2019-06-11 11:30:37
 * 版权：广州红迅软件
 * </pre>
 */
package com.redxun.sys.org.dao;

import com.redxun.core.dao.mybatis.BaseMybatisDao;
import com.redxun.sys.org.entity.LoginSessionUser;
import org.springframework.stereotype.Repository;

@Repository
public class LoginSessionUserDao extends BaseMybatisDao<LoginSessionUser> {

	@Override
	public String getNamespace() {
		return LoginSessionUser.class.getName();
	}


}

