
package com.redxun.xcmgProjectManager.report.dao;

import com.redxun.core.dao.mybatis.BaseMybatisDao;
import com.redxun.xcmgProjectManager.report.entity.AllProjectCount;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllProjectCountDao extends BaseMybatisDao<AllProjectCount> {

	@Override
	public String getNamespace() {
		return AllProjectCount.class.getName();
	}

    public List<AllProjectCount> selectList(AllProjectCount allProjectCount){
        List<AllProjectCount> list=this.getBySqlKey("selectList", allProjectCount);
        return list;
    }
}