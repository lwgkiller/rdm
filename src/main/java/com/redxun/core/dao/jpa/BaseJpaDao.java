package com.redxun.core.dao.jpa;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.redxun.core.entity.BaseEntity;
import com.redxun.core.entity.BaseTenantEntity;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.context.CurrentContext;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基础数据库访问实体类
 * @author csx
 * @param <T>
 *  @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public abstract class BaseJpaDao<T extends BaseEntity> extends JpaDao<T, String> {
	@Resource
	CurrentContext currentContext;

    public void setCurrentContext(CurrentContext currentContext) {
		this.currentContext = currentContext;
	}

	/*
	 * 创建实体对象 加上默认的创建人及创建时间，租用ID等
	 * (non-Javadoc)
	 * @see com.redxun.core.dao.jpa.JpaDao#create(java.io.Serializable)
	 */
    @SuppressWarnings("rawtypes")
	@Transactional
	@Override
    public void create(T entity) {
        IUser iUser = currentContext.getCurrentUser();
        //若当前用户不为空，则设置创建人及创建时间
        if (iUser != null) {
            entity.setCreateBy(iUser.getUserId());
            if(entity instanceof BaseTenantEntity){
            	BaseTenantEntity bte=(BaseTenantEntity)entity;
            	//当管理员所在的公司的域名为配置中的指定管理机构，允许通过参数更新租用Id
            	//否则只能更新为本公司租用ID,以防止把本租用的数据更新为其他机构的数据
            	if(!(StringUtils.isNotEmpty(bte.getTenantId())
            			&& WebAppUtil.isSaasMgrUser())){
            		bte.setTenantId(iUser.getTenant().getTenantId());
            	}
            } 
            //判断是否带有集合的子属性,若有，也设置其租用Id            
    		Field[]fields=entity.getClass().getDeclaredFields();
    		for(Field field:fields){
    			Object att=BeanUtil.getFieldValueFromObject(entity, field.getName());
    			if(att==null) continue;
    			if(!(att instanceof Collection)) continue;
				for(Object el:(Collection)att){
					if(!(el instanceof BaseTenantEntity)) continue;
					 BaseTenantEntity bte=(BaseTenantEntity)el;
					 if(bte.getPkId()==null){
						 bte.setPkId(IdUtil.getId());
					 }
	            	//当管理员所在的公司的域名为配置中的指定管理机构，允许通过参数更新租用Id
	            	//否则只能更新为本公司租用ID,以防止把本租用的数据更新为其他机构的数据
	            	if(!(StringUtils.isNotEmpty(bte.getTenantId())
	            			&& WebAppUtil.isSaasMgrUser())){
	            		bte.setTenantId(iUser.getTenant().getTenantId());
	            		bte.setCreateBy(iUser.getUserId());
	            		bte.setCreateTime(new Date());
	            	}
				}
    		}
        }
        entity.setCreateTime(new Date());
        //若当前实体主键为空，则动态生成一主键放置在主键上
        if (entity.getPkId() == null) {
            entity.setPkId(idGenerator.getSID());
        }
        super.create(entity);
    }
    /*
     * 更新实体对象 加上默认的更新人及更新时间
     * (non-Javadoc)
     * @see com.redxun.core.dao.jpa.JpaDao#update(java.io.Serializable)
     */
    @SuppressWarnings("rawtypes")
	public void update(T entity) {
        IUser iUser = currentContext.getCurrentUser();
        //若当前用户不为空，则设置更新人及更新时间
        if (iUser != null) {
            entity.setUpdateBy(iUser.getUserId());
        }
        if(entity instanceof BaseTenantEntity){
	        BaseTenantEntity bte=(BaseTenantEntity)entity;
	        //当管理员所在的公司的域名为配置中的指定管理机构，允许通过参数更新租用Id
	    	//否则只能更新为本公司的域名
	        /*
	    	if(StringUtils.isNotEmpty(bte.getTenantId())
	    			&& currentContext.getCurrentTenant()!=null 
	    			&& WebAppUtil.getOrgMgrDomain().equals(currentContext.getCurrentTenant().getDomain())){
	    		bte.setTenantId(iUser.getTenant().getTenantId());
	    	}else 
	    	*/
	        if(StringUtil.isEmpty(bte.getTenantId())){
	        	if(iUser!=null && iUser.getTenant()!=null){
	        		bte.setTenantId(iUser.getTenant().getTenantId());
	        	}
	    	}
	    	
	    	//判断是否带有集合的子属性,若有，也设置其租用Id            
    		Field[]fields=entity.getClass().getDeclaredFields();
    		for(Field field:fields){
    			Object att=BeanUtil.getFieldValueFromObject(entity, field.getName());
    			if(att==null) continue;
    			if(!(att instanceof Collection)) continue;
				for(Object el:(Collection)att){
					if(!(el instanceof BaseTenantEntity)) continue;
					 BaseTenantEntity subEnty=(BaseTenantEntity)el;
	            	//当管理员所在的公司的域名为配置中的指定管理机构，允许通过参数更新租用Id
	            	//否则只能更新为本公司租用ID,以防止把本租用的数据更新为其他机构的数据
	            	if(!(StringUtils.isNotEmpty(bte.getTenantId())
	            			&& WebAppUtil.isSaasMgrUser())){
	            		subEnty.setUpdateBy(iUser.getUserId());
	            		subEnty.setUpdateTime(new Date());
	            	}
	            	
	            	 if(StringUtils.isEmpty(subEnty.getCreateBy())){
						 subEnty.setCreateBy(iUser.getUserId());
						 subEnty.setCreateTime(new Date());
						 if(StringUtils.isNotBlank(subEnty.getTenantId())){
							 subEnty.setTenantId(iUser.getTenant().getTenantId());
						 }
					 }
				}
    		}
        }
        entity.setUpdateTime(new Date());
        super.update(entity);
    }
    /**
     * update and skip the fields of update time and update by 
     * @param entity
     */
    public void updateSkipUpdateTime(T entity){
    	super.update(entity);
    }
    
    @Override
    public void saveOrUpdate(T entity) {
    	if(entity.getPkId()==null
    			|| StringUtils.isEmpty(entity.getPkId().toString())){
    		create(entity);
    	}else{
    		update(entity);
    	}
    }
}
