
package com.redxun.sys.core.manager;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.redxun.core.dao.IDao;
import com.redxun.core.dao.mybatis.BaseMybatisDao;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.sys.core.dao.SysListSolDao;
import com.redxun.sys.core.entity.SysListSol;

/**
 * 
 * <pre> 
 * 描述：系统列表方案 处理接口
 * 作者:mansan
 * 日期:2017-05-21 12:11:18
 * 版权：广州红迅软件
 * </pre>
 */
@Service
public class SysListSolManager extends MybatisBaseManager<SysListSol>{
	@Resource
	private SysListSolDao sysListSolDao;

	
	@SuppressWarnings("rawtypes")
	@Override
	protected IDao getDao() {
		return sysListSolDao;
	}
	
	
}
