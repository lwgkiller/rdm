package com.redxun.sys.org.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redxun.core.util.StringUtil;
import org.springframework.stereotype.Repository;

import com.redxun.core.dao.mybatis.BaseMybatisDao;
import com.redxun.core.query.IPage;
import com.redxun.core.query.QueryFilter;
import com.redxun.core.query.SqlQueryFilter;
import com.redxun.core.util.BeanUtil;
import com.redxun.sys.org.entity.OsUser;
/**
 * 用户实例查询
 * @author mansan
 *
 */
@Repository
public class OsUserDao extends BaseMybatisDao<OsUser>{

	@Override
	public String getNamespace() {
		return OsUser.class.getName();
	}
	
	public List<OsUser> getByDepIdGroupId(String depId,String groupId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("depId", depId);
		params.put("groupId", groupId);
		return this.getBySqlKey("getByDepIdGroupId", params);
	}




	/**
	 *根据	SQL查询用户列表
	 */
	public List<OsUser> getBySql(String sql){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("userId", sql);
		return this.getBySqlKey("getBySql", params);
	}

	/**
	 * 查找某个用户组下的用户
	 * @param groupId
	 * @param relTypeId
	 * @param userNo 用户编号
	 * @param fullname 用户姓名
	 * @param page
	 * @return
	 */
	public List<OsUser> getByGroupIdRelTypeId(String groupId,String relTypeId,String userNo,String fullname,IPage page){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("relTypeId", relTypeId);
		params.put("groupId", groupId);
		params.put("userNo", userNo);
		params.put("fullname", fullname);
		params.put("status", OsUser.STATUS_IN_JOB);
		return this.getBySqlKey("getByGroupIdRelTypeId", params,page);
	}
	
	/**
	 * 查找某个用户组下的用户
	 * @param groupId
	 * @param relTypeId
	 * @return
	 */
	public List<OsUser> getByGroupIdRelTypeId(String groupId,String relTypeId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("relTypeId", relTypeId);
		params.put("groupId", groupId);
		params.put("status", OsUser.STATUS_IN_JOB);
		return this.getBySqlKey("getByGroupIdRelTypeId", params);
	}
	

	
	/**
	 * 查找某个用户组下的用户,并且按条件过滤
	 * @param filter
	 * @return
	 */
	public List<OsUser> getByGroupIdRelTypeId(QueryFilter filter){
		Map<String,Object> params=filter.getParams();
		params.put("status", OsUser.STATUS_IN_JOB);
		return this.getBySqlKey("getByGroupIdRelTypeId", params,filter.getPage());
	}
	
	public List<OsUser> getByGroupPathRelTypeId(QueryFilter filter){
		Map<String,Object> params=filter.getParams();
		params.put("STATUS_", OsUser.STATUS_IN_JOB);
		return this.getBySqlKey("getByGroupPathRelTypeId", params,filter.getPage());
	}
	
	public List<OsUser> getByIsAdmin(QueryFilter filter){
		Map<String,Object> params=filter.getParams();
		params.put("STATUS_", OsUser.STATUS_IN_JOB);
		return this.getBySqlKey("getByGroupPathRelTypeId", params,filter.getPage());
	}

	/**
	 * 根据组织ID和关系名称查找相关人员。
	 * @param groupId
	 * @param relationName
	 * @return
	 */
	public OsUser getByRelationName(String groupId,String relationName){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("groupId", groupId);
		params.put("relationName", relationName);
		
		OsUser user=this.getUnique("getByRelationGroup", params);
		return user;
	}
	
	/**
	 * 根据汇报关系和用户ID查找上级。
	 * @param reportName
	 * @param userId
	 * @return
	 */
	public OsUser getByReportLine(String reportName,String userId){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("reportName", reportName);
		OsUser user=this.getUnique("getByReportLine", params);
		return user;
	}
	
	public List<OsUser> getSyncWx(String tenantId){
		return this.getBySqlKey("getSyncWx", tenantId);
	}
	
	/**
	 * 更新用户标志为已经更新。
	 * @param userId
	 */
	public void updWxFlag(String userId){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("userId", userId);
		this.updateBySqlKey("updWxFlag", params);
	}
	
	
	
	
	/**
	 * 获取用户列表。
	 * @param filter
	 * @return
	 */
	public List<OsUser> getByFilter(QueryFilter filter){
		List<OsUser> list=this.getPageBySqlKey("getByFilter", filter);
		return list;
	}

	/**
	 * 获取部门下用户。
	 * @param groupId
	 * @return
	 */
	public List<OsUser> getByGroupId(String groupId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("groupId", groupId);
		List<OsUser> list=this.getBySqlKey("getByGroupId", params);
		return list;
	}
	
	/**
	 * 根据两方获取用户交集。
	 * @param part1
	 * @param part2
	 * @return
	 */
	public List<OsUser> getByPart(String part1,String part2,String tenantId){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("part1", part1);
		params.put("part2", part2);
		params.put("tenantId", tenantId);
		
		List<OsUser> list=this.getBySqlKey("getByPart", params);
		return list;
	}

	public OsUser getByEmail(String email) {
		List<OsUser> user=this.getBySqlKey("getByEmail", email);
		if(BeanUtil.isNotEmpty(user)) {
			return user.get(0);
		}
		return null;
	}
	

	
    public void insertOrUpdate(OsUser user) {
    	this.saveOrUpdate(user);
    }
    
    /**
     * 根据用户ID和租户ID获取数据。
     * @param userId
     * @param tenantId
     * @return
     */
    public OsUser getByTenantId(String userId,String tenantId){
    	Map<String,Object> params=new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("tenantId", tenantId);
		OsUser user=this.getUnique("getByTenantId", params);
		return user;
    }
	
    /**
     * 根据用户帐号和租户ID获取用户数据。
     * @param userNo
     * @param tenantId
     * @return
     */
    public OsUser getByUserName(String userNo,String tenantId){
    	Map<String,Object> params=new HashMap<String,Object>();
		params.put("userNo", userNo);
		params.put("tenantId", tenantId);
		OsUser user=this.getUnique("getByUserName", params);
		return user;
    }
    
    public void updPassword(String userId,String pwd){
    	Map<String,Object> params=new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("pwd", pwd);
		params.put("pwdStatus","");
		this.updateBySqlKey("updPassword", params);
		
    }

	/**
	 * 更新默认登陆机构
	 */
	public void updateTenantIdFromDomain(String userId,String defaultDomain){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("userId", userId);
		if(StringUtil.isNotEmpty(defaultDomain)){
			params.put("defaultDomain", defaultDomain);
		}
		this.updateBySqlKey("updateTenantIdFromDomain", params);
	}

	/**
	 * 根据用户帐号获取用户数据。
	 * @param userNo
	 * @return
	 */
	public List<OsUser> getByUserNo(String userNo){
		List<OsUser> list= this.getBySqlKey("getByUserNo", userNo);
		return list;
	}
}
