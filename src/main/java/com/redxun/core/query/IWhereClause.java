package com.redxun.core.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public interface IWhereClause {
   /**
    * 执行Where条件构造 (用于JPA)
    * @param builder
    * @param root
    * @return 
    */
   public Predicate execute(CriteriaBuilder builder,Root root,Class entityClass) throws Exception;
   /**
    * 用于构造Where的SQL语句片段(用于Mybatis)
    * @return
    */
   public String getSql();
}
