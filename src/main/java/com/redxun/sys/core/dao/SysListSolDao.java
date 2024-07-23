
/**
 * 
 * <pre> 
 * 描述：系统列表方案 DAO接口
 * 作者:mansan
 * 日期:2017-05-21 12:11:18
 * 版权：广州红迅软件
 * </pre>
 */
package com.redxun.sys.core.dao;

import com.redxun.sys.core.entity.SysListSol;
import org.springframework.stereotype.Repository;
import com.redxun.core.dao.mybatis.BaseMybatisDao;

@Repository
public class SysListSolDao extends BaseMybatisDao<SysListSol> {

	@Override
	public String getNamespace() {
		return SysListSol.class.getName();
	}

}

