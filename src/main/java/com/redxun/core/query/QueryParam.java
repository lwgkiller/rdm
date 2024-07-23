package com.redxun.core.query;

import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import com.redxun.core.util.StringUtil;

/**
 * 
 * <pre> 
 * 描述：动态查询组合参数
 * 构建组：mibase
 * 作者：keith
 * 邮箱:keith@mitom.cn
 * 日期:2014-2-9-下午2:20:36
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public class QueryParam implements IWhereClause{
	//=========字段比较运算===============
    /**
     * 等于
     */
    public static final String OP_EQUAL="EQ";
    /**
     * 不等于
     */
    public static final String OP_NOT_EQUAL="NEQ";
    /**
     * 大于
     */
    public static final String OP_GREAT="GT";
    
    /**
     * 大于等于
     */
    public static final String OP_GREAT_EQUAL="GE";
    
    /**
     * 小于
     */
    public static final String OP_LESS="LT";
    /**
     * 小于等于
     */
    public static final String OP_LESS_EQUAL="LE";
    /**
     * 全模糊
     */
    public static final String OP_LIKE="LK";
    /**
     * 左模糊
     */
    public static final String OP_LEFT_LIKE="LEK";
    /**
     * 右模糊
     */
    public static final String OP_RIGHT_LIKE="RIK";
    
  
    
    //IS NULL
    public final static String OP_IS_NULL = "ISNULL";
    
    //IS NOT NULL
    public final static String OP_NOTNULL = "NOTNULL";
    //IN
    public final static String OP_IN="IN";
    
    
    //===========字段的类型=================
    /**
     * String Type = S
     */
    public static final String FIELD_TYPE_STRING="S";
    /**
     * Long Type=L
     */
    public static final String FIELD_TYPE_LONG="L";
    /**
     * Integer Type=I
     */
    public static final String FIELD_TYPE_INTEGER="I";
    
    /**
     * Short Type=I
     */
    public static final String FIELD_TYPE_SHORT="SN";
  
    /**
     * Float Type=F
     */
    public static final String FIELD_TYPE_FLOAT="F";
    /**
     * Double Type=DL
     */
    public static final String FIELD_TYPE_DOUBLE="DB";
    
    /**
     * Date Type = D
     */
    public static final String FIELD_TYPE_DATE="D";
    
    /**
     * BigDecimal=BD 
     */
    public static final String FIELD_TYPE_BIGDECIMAL="BD";
    
    /**
     * INNER JOIN=I
     */
    public static final String JOIN_TYPE_INNER="I";
    /**
     * LEFT JOIN=L
     */
    public static final String JOIN_TYPE_LEFT="L";
    /**
     * RIGHT JOIN=R
     */
    public static final String JOIN_TYPE_RIGHT="R";
    
    public  javax.persistence.criteria.JoinType joinType=javax.persistence.criteria.JoinType.INNER;
    
    //============属性=====================
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 比较操作类型 如小于或大于或等于
     */
    private String opType;
    /**
     * 值
     */
    private Object value;
    /**
     * 字段数据类型
     */
    private String fieldType;

    public QueryParam() {
    }

    public QueryParam(String fieldName, String opType, Object value) {
        this.fieldName = fieldName;
        this.opType = opType;
        this.value = value;
    }
    
    public QueryParam(String fieldName,javax.persistence.criteria.JoinType joinType, String opType,Object value){
    	this.fieldName = fieldName;
        this.joinType=joinType;
    	this.opType = opType;
        this.value = value;
    }
    
    public QueryParam(String fieldName,String fieldType,String opType,Object value){
    	this.fieldName=fieldName;
    	this.fieldType=fieldType;
    	this.opType=opType;
    	this.value=value;
    }
   
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Predicate execute(CriteriaBuilder builder,Root root,Class entityClass)throws Exception{
    	boolean isNeedJoin=false;
    	int joinLabelIndex=fieldName.indexOf("#");
    	if(joinLabelIndex!=-1){
    		String joinLabel=fieldName.substring(joinLabelIndex+1);
    		fieldName=fieldName.substring(0,joinLabelIndex);
    		joinType=getJoinType(joinLabel);
    	}
    	String[]fields=fieldName.split("[.]");
    	if(fields!=null && fields.length>1){
    		fieldName=fields[0];
    		isNeedJoin=true;
    	}
        Field field=null;
        Class cls=entityClass;
        for(; cls != Object.class ; cls = cls.getSuperclass()) {
    		try{
    			field=cls.getDeclaredField(fieldName);
    		}catch(Exception ex){
    		}
    		if(field!=null){
    			break;
    		}
		} 
        Expression valueExpression=builder.literal(value);
        EntityType entityType= root.getModel();

        Expression expression= null; 
        //是否需要进行关联查询
        if(isNeedJoin){
        	Path<Object> path=root.join(fieldName,joinType).get(fields[1]);
        	expression=path;
        	//alias
        	expression.alias(StringUtil.makeFirstLetterLowerCase(fieldName));
        }else{
        	 SingularAttribute attribute= entityType.getSingularAttribute(fieldName, field.getType());
        	expression=root.get(attribute);
        }        

        if(OP_EQUAL.equals(opType)){
           return builder.equal(expression,valueExpression);
        }        
        if(OP_NOT_EQUAL.equals(opType)){
            return builder.notEqual(expression,valueExpression);
         }
        
        if(OP_GREAT.equals(opType)) {
            return builder.greaterThan(expression, valueExpression);
        }
        
        if(OP_GREAT_EQUAL.equals(opType)){
            return builder.greaterThanOrEqualTo(expression, valueExpression);
        }
        
        if(OP_LESS.equals(opType)){
            return builder.lessThan(expression, valueExpression);
        }
        
        if(OP_LESS_EQUAL.equals(opType)){
            return builder.lessThanOrEqualTo(expression, valueExpression);
        }
        
        if(OP_LIKE.equals(opType)){
            return builder.like(expression, builder.literal(  value + ""));
        }
        
        if(OP_LEFT_LIKE.equals(opType)){
            return builder.like(expression, builder.literal(value + ""));
        }
        
        if(OP_RIGHT_LIKE.equals(opType)){
            return builder.like(expression, builder.literal(value +""));
        }
        
        if(OP_IN.equals(opType)){
        	return expression.in((Object[])value);
        }
        
        
        return builder.equal(expression,valueExpression);
    }
    
    
    
    
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public SingularAttribute getFieldAttribute(EntityType entityType,String fileName,Class entityClass) throws NoSuchFieldException{
        Field field=entityClass.getDeclaredField(fileName);
       // System.out.println("fieldName:" + fileName + " fieldClass:" + field.getType());
        SingularAttribute attribute= entityType.getSingularAttribute(fieldName, field.getType());
        return attribute;
    }
    
    /**
     * 返回操作字段的数据类型
     * @return 
     */
    @SuppressWarnings("rawtypes")
	public Class getFieldTypeClass(){
        if(FIELD_TYPE_STRING.equals(fieldType)){
            return String.class;
        }
        if(FIELD_TYPE_LONG.equals(fieldType)){
            return Long.class;
        }
        
        if(FIELD_TYPE_SHORT.equals(fieldType)){
        	return Short.class;
        }
        
        if(FIELD_TYPE_FLOAT.equals(fieldType)){
            return Float.class;
        }
        if(FIELD_TYPE_INTEGER.equals(fieldType)){
            return Integer.class;
        }
        if(FIELD_TYPE_DOUBLE.equals(fieldType)){
            return Double.class;
        }
        
        if(FIELD_TYPE_DATE.equals(fieldType)){
            return Date.class;
        }
        
        return String.class;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public Object getValue() {
    	if(OP_LIKE.equals(opType)){
    		if(!this.value.toString().startsWith("%")){
    			value="%" + value + "%";
    		}
    	}
    	
    	if(OP_LEFT_LIKE.equals(opType)){
    		if(!this.value.toString().startsWith("%")){
    			value= "%" + value;
    		}
    	}
    	
    	if(OP_RIGHT_LIKE.equals(opType)){
    		if(!this.value.toString().endsWith("%")){
    			value= value + "%";
    		}
    	}
    	
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	/**
	 * 返回关联的类型
	 * @param shortJoinName
	 * @return 
	 * javax.persistence.criteria.JoinType
	 * @exception 
	 * @since  1.0.0
	 */
	public  javax.persistence.criteria.JoinType getJoinType(String shortJoinName){
		if(JOIN_TYPE_LEFT.equals(shortJoinName)){
			return javax.persistence.criteria.JoinType.LEFT;
		}else if(JOIN_TYPE_RIGHT.equals(shortJoinName)){
			return javax.persistence.criteria.JoinType.RIGHT;
		}else{
			return javax.persistence.criteria.JoinType.INNER;
		}
	}
	
	public String getFieldParam(){
		String name=fieldName.replace(".", "");
		 if (opType == null) {
             opType = OP_EQUAL;
         } else {
             opType = opType.toUpperCase();
         }
		 //日期类型
		//if(!FIELD_TYPE_STRING.equals(fieldType)){
			if(OP_LESS_EQUAL.equals(opType) || OP_LESS.equals(opType)){
				return name +"_END";
			}
			else if(OP_GREAT.equals(opType) || OP_GREAT_EQUAL.equals(opType) ){
				return name +"_START";
			}
		//}
		return name;
	}
	


    public String getSql() {
    	 if (opType == null) {
             opType = OP_EQUAL;
         } else {
             opType = opType.toUpperCase();
         }
         String fieldParam = "#{" + getFieldParam() + "}";
        
         String sql = fieldName;
         if (OP_EQUAL.equals(opType)) {
             sql += " = " + fieldParam;
         }
         else if (OP_NOT_EQUAL.equals(opType)) {
             sql += " != " + fieldParam;
         }
         else if (OP_LESS.equals(opType)) {
             sql += " < " + fieldParam;
         } else if (OP_LESS_EQUAL.equals(opType)) {
             sql += " <= " + fieldParam;
         } else if (OP_GREAT.equals(opType)) {
             sql += " > " + fieldParam;
         } else if (OP_GREAT_EQUAL.equals(opType)) {
             sql += " >= " + fieldParam;
         } else if (OP_LEFT_LIKE.equals(opType)) {
             sql += " like " + fieldParam;
         } else if (OP_RIGHT_LIKE.equals(opType)) {
             sql += " like  " + fieldParam;
         } else if (OP_LIKE.equals(opType)) {
             sql += " like  " + fieldParam;
         } else if (OP_IS_NULL.equals(opType)) {
             sql += " is null " ;
         } else if (OP_NOTNULL.equals(opType)) {
             sql += " is not null";
         } 
         else if (OP_IN.equals(opType)) {
        	 String tmp=getInSql();
             if(StringUtil.isEmpty(tmp)){
            	 return "";
             }
             sql+=tmp;
         } 
         else {
             sql += " =  " + fieldParam;
         }
         return sql;
    }
    
    private String getInSql(){
    	if( this.value==null) return "";
    	if(this.value instanceof String){
    		return " IN (" +this.value +")";
    	}
    	if(this.value.getClass().isArray()){
    		Object[] ary=(Object[]) this.value;
    		String str="";
    		for(int i=0;i<ary.length;i++){
    			Object o=ary[i];
    			if(FIELD_TYPE_STRING.equals(fieldType)){
    				str+=((i==0)?"":",") +"'" +o.toString() +"'";  
    			}
    			else{
    				str+=((i==0)?"":",") +o.toString() ;
    			}
    		}
    		return " IN (" +str +")";
    	}
    	return "";
    }

}
