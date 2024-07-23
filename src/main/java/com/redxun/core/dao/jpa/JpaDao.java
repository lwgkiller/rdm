package com.redxun.core.dao.jpa;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.redxun.core.dao.IDao;
import com.redxun.core.query.FieldLogic;
import com.redxun.core.query.IPage;
import com.redxun.core.query.Page;
import com.redxun.core.query.QueryFilter;
import com.redxun.core.query.QueryParam;
import com.redxun.core.query.SortParam;
import com.redxun.core.seq.IdGenerator;
import com.redxun.org.api.model.ITenant;

/**
 * 
 * <pre> 
 * 描述：JPA 实现的数据库基本操作类
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:keith@mitom.cn
 * 日期:2014年5月11日-下午6:46:11
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre> 
 */
public abstract class JpaDao <T extends Serializable,PK extends Serializable> implements IDao<T,PK> {

    protected Logger logger=LogManager.getLogger(JpaDao.class);
    
    @Resource
    protected IdGenerator idGenerator;

    @PersistenceContext
    protected EntityManager entityManager;


    public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

    public EntityManager getEntityManager(){
    	return entityManager;
    }
    
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Override
	public void deleteByTenantId(String tenantId) {
		String ql="delete from " + getEntityClass().getName() + " where tenantId=?";
		delete(ql, new Object[]{tenantId});
	}
	
	/**
     * 类需要指定实现的实体类型
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected abstract Class getEntityClass();

    @SuppressWarnings("unchecked")
	public T get(PK id) {
        return (T) entityManager.find(getEntityClass(), id);
    }

    public void delete(PK id) {
        Object entity = get(id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
    /**
     * 删除实体
     * @param entity 
     * void
     * @exception 
     * @since  1.0.0
     */
    public void deleteObject(T entity){
    	if (entity != null) {
            entityManager.remove(entity);
        }
    }
    
    /**
     * 按JPQL语句删除属性值
     * @param jpql
     * @param params 
     * void
     * @exception 
     * @since  1.0.0
     */
    @Transactional
    public void delete(String jpql,Object... params){
    	Query query=entityManager.createQuery(jpql);
    	if (params != null && params.length != 0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i+1, params[i]);
            }
        }
    	query.executeUpdate();
    }
    
    public void update(String jpql,Object... params){
    	Query query=entityManager.createQuery(jpql);
    	if (params != null && params.length != 0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i+1, params[i]);
            }
        }
    	query.executeUpdate();
    //	entityManager.flush();
    }
    /**
     * 创建实体
     * @param entity 
     */
    @Transactional
    public void create(T entity) {
        entityManager.persist(entity);
    }
    @Transactional
    public void update(T entity) {
       entityManager.merge(entity);
    }
    
    public void updateSkipUpdateTime(T entity) {
        entityManager.merge(entity);
     }
        
    /**
     * 取得过滤条件中的总记录数
     * @param queryFilter
     * @return 
     */
    public Long getTotalItems(QueryFilter queryFilter){
        
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<Entity> root = criteriaQuery.from(getEntityClass());
        Predicate whereClause=null;
        try {
            whereClause = queryFilter.getFieldLogic().execute(criteriaBuilder, root, getEntityClass());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }     
        if (whereClause != null) {
            criteriaQuery.where(whereClause);
        }
        criteriaQuery.select(criteriaBuilder.count(root));
        TypedQuery<Long> totalQuery = entityManager.createQuery(criteriaQuery);
        Long totalItems = totalQuery.getSingleResult();
        
        return totalItems;

    }
    
 
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<T> getAllByTenantId(String tenantId,QueryFilter queryFilter){
    	  CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
          CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
          Root<Entity> root = criteriaQuery.from(getEntityClass());

          //若需要按tenantId进行过滤，则动态增加该条件
          if(StringUtils.isNotEmpty(tenantId)){
        	FieldLogic logic=new FieldLogic(FieldLogic.AND);
        	//TODO 加上
        	QueryParam param=new QueryParam("tenantId",QueryParam.OP_IN,new String[]{ITenant.PUBLIC_TENANT_ID,tenantId}); 
        	logic.getCommands().add(param);
        	logic.getCommands().add(queryFilter.getFieldLogic());
        	queryFilter.setFieldLogic(logic);
          }
          
          try {
              //构造Where查询
              Predicate whereClause = queryFilter.getFieldLogic().execute(criteriaBuilder, root, getEntityClass());
              
              if (whereClause != null) {
                  criteriaQuery.where(whereClause);
              }
              
              if(!queryFilter.getPage().isSkipCountTotal()){
                //构造查询总记录数
                criteriaQuery.select(criteriaBuilder.count(root));
                TypedQuery<Long> totalQuery = entityManager.createQuery(criteriaQuery);
                Long totalItems = totalQuery.getSingleResult();
                ((Page)queryFilter.getPage()).setTotalItems(totalItems.intValue());
              }
              //选择子属性
              if(StringUtils.isNotEmpty(queryFilter.getSelectJoinAtt())){
            	  criteriaQuery.select(root.get(queryFilter.getSelectJoinAtt()));
            	  List<SortParam> sortParams=queryFilter.getOrderByList();
            	  //检查排序，若查询这种带关联的，排序也需要带有属性前缀
            	  for(SortParam sm:sortParams){
            		  if(sm.getProperty().indexOf(".")==-1){
            			  sm.setProperty(queryFilter.getSelectJoinAtt()+"."+sm.getProperty());
            		  }
            	  }
              }else{
            	  criteriaQuery.select(root);
              }
              if (whereClause != null) {
                  criteriaQuery.where(whereClause);
              }
              //進行排序
              if (queryFilter.getOrderByList() != null && queryFilter.getOrderByList().size() > 0) {
                  List<Order> orderList = getOrderList(criteriaBuilder, root, queryFilter.getOrderByList());
                  criteriaQuery.orderBy(orderList);
              }
              TypedQuery<Entity> entityQuery = entityManager.createQuery(criteriaQuery);
              //进行分页查询
              entityQuery.setFirstResult(queryFilter.getPage().getStartIndex());
              entityQuery.setMaxResults(queryFilter.getPage().getPageSize());

              List list= (List) entityQuery.getResultList();
              return list;
          } catch (Exception ex) {
              throw new PersistenceException(ex);
          }
    }

    private List<Order> getOrderList(CriteriaBuilder criteriaBuilder, Root root, List<SortParam> sortParams) {
        List<Order> orderByList = new ArrayList<Order>();
        try {
            for (SortParam sortParam : sortParams) {
            	if(StringUtils.isNotEmpty(sortParam.getDirection()) 
            			&& StringUtils.isNotEmpty(sortParam.getProperty())){
            		Order order = sortParam.execute(criteriaBuilder, root, getEntityClass());
            		orderByList.add(order);
            	}
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
        return orderByList;
    }

    /**
     * 按JPQL查询语句查询结果
     *
     * @param queryString
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<T> getByJpql(String queryString, Object... params) {
        Query query = entityManager.createQuery(queryString);
        if (params != null && params.length != 0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i+1, params[i]);
            }
        }
        return(List<T>) query.getResultList();
    }

    
    
    @SuppressWarnings({"unchecked"})
    public List<T> getByJpql(String queryString, Object[] params,IPage page) {

        Integer totalItems = getTotalItems(queryString, params);
        ((Page)page).setTotalItems(totalItems);

        Query query = entityManager.createQuery(queryString);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i+1, params[i]);
            }
        }
        query.setFirstResult(page.getStartIndex());
        query.setMaxResults(page.getPageSize());
        return query.getResultList();

    }

    /**
     * 返回所有实体列表
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        Query query = entityManager.createQuery("from " + getEntityClass().getName(), getEntityClass());
        return query.getResultList();
    }
    
    /**
     * 按分页返回所有实体列表
     *
     * @param page
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public List<T> getAll(final IPage page) {
        final String jql = "from " + getEntityClass().getName();
        Integer totalItems = getTotalItems(jql, new Object[]{});
        ((Page)page).setTotalItems(totalItems);

        Query query = entityManager.createQuery(jql);
        query.setFirstResult(page.getStartIndex());
        query.setMaxResults(page.getPageSize());
        return query.getResultList();

    }
    
    /**
     * 构造条件过滤查询
     *
     * @param queryFilter
     * @return
     */
    public List<T> getAll(final QueryFilter queryFilter) {
        return getAllByTenantId(null,queryFilter);
    }
    
    @SuppressWarnings("unchecked")
    public List<T> getAllByTenantId(String tenantId){
    	Query query = entityManager.createQuery("from " + getEntityClass().getName()+ " where tenantId in(?,?)", getEntityClass());
    	query.setParameter(1, ITenant.PUBLIC_TENANT_ID);
    	query.setParameter(2, tenantId);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<T> getAllByTenantId(String tenantId,IPage page){
    	 final String jql = "from " + getEntityClass().getName()+ " where tenantId in(?,?)";
         Integer totalItems = getTotalItems(jql, new Object[]{tenantId,ITenant.PUBLIC_TENANT_ID});
         ((Page)page).setTotalItems(totalItems);

         Query query = entityManager.createQuery(jql);
         query.setParameter(1, ITenant.PUBLIC_TENANT_ID);
     	 query.setParameter(2, tenantId);
         query.setFirstResult(page.getStartIndex());
         query.setMaxResults(page.getPageSize());
         return query.getResultList();
    }

    /**
     *  按照JPQL查询唯一实体
     * @param jpql
     * @param params
     * @return 
     */
    public Object getUnique(String jpql,Object... params){
       Query query=entityManager.createQuery(jpql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i+1, params[i]);
            }
        }
        try{
        	return query.getSingleResult();
        }catch(Exception ex){
        	logger.warn(ex.getMessage());
        	return null;
        }
    }

    /**
     * 返回查询语句的记录数
     *
     * @param queryString
     * @param params
     * @return
     */
    public Integer getTotalItems(String queryString, Object... params) {
        //防止在SQL Server中出错 ,因为Sql Server不允许排序再查询总记录数
        int orderByIndex = queryString.toUpperCase().indexOf(" ORDER BY ");
        if (orderByIndex != -1) {
            queryString = queryString.substring(0, orderByIndex);
        }
        
        int fromIndex = queryString.toUpperCase().indexOf("FROM");
        queryString = queryString.substring(fromIndex);
        String sql = "SELECT count(*) as c " + queryString;
        
        Query query = entityManager.createQuery(sql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i+1, params[i]);
            }
        }
        Object reVal = (Object) query.getSingleResult();
        return (reVal != null) ? new Integer(reVal.toString()) : 0;

    }
    /**
     * 从实体对象库中移除对象
     * @param obj 
     * void
     * @exception 
     * @since  1.0.0
     */
    public void detach(Object obj){
    	entityManager.detach(obj);
    }
    
    /**
     * 清空缓存库并提交 
     * void
     * @exception 
     * @since  1.0.0
     */
    public void flush(){
    	entityManager.flush();
    }
    
    @Override
    public void clear() {
    	entityManager.clear();
    }
    
    @Override
    public void saveOrUpdate(T entity) {
    	throw new RuntimeException("未实现，请继承BaseJpaDao，或扩展实现！");
    }
}
