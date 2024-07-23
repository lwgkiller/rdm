package com.redxun.core.query;

import java.lang.reflect.Field;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;


/**
 *  查询参数
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 * 
 */
public class SortParam {

	public final static String SORT_ASC = "ASC";

	public final static String SORT_DESC = "DESC";

	/**
	 * 字段属性名称
	 */
	private String property;
	/**
	 * 字段方向排序
	 */
	private String direction;

	public SortParam() {

	}

	public SortParam(String property, String direction) {
		this.property = property;
		this.direction = direction;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	public String getSql(){
		String orderSql=this.property + " " + this.direction;
		return com.redxun.core.database.util.DbUtil.encodeSql(orderSql);
	}
	
	@Override
	public String toString() {
		return this.property + " " + this.direction;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Order execute(CriteriaBuilder builder, Root root, Class entityClass) throws Exception {
		boolean isNeedJoin = false;
		String[] fields = property.split("[.]");
		if (fields != null && fields.length > 1) {
			property = fields[0];
			isNeedJoin = true;
		}
		 Field field=null;
	        Class cls=entityClass;
	        for(; cls != Object.class ; cls = cls.getSuperclass()) {
	    		try{
	    			field=cls.getDeclaredField(property);
	    		}catch(Exception ex){
	    		}
	    		if(field!=null){
	    			break;
	    		}
			} 
	        
		EntityType entityType = root.getModel();
		SingularAttribute attribute = entityType.getSingularAttribute(property, field.getType());

		Expression expression = null;
		// 是否需要进行关联查询
		if (isNeedJoin) {
			//root.join(attribute);
			Path path = null;
			for (int i = 0; i < fields.length; i++) {
				if (path == null) {
					path = root.get(fields[i]);
				} else {
					path = path.get(fields[i]);
				}
			}
			expression = path;
		} else {
			expression = root.get(attribute);
		}

		if (SORT_ASC.equalsIgnoreCase(direction)) {
			return builder.asc(expression);
		} else {
			return builder.desc(expression);
		}
	}

}
