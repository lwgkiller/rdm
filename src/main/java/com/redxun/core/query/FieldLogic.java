/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redxun.core.query;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 字段间的逻辑运算
 *
 * @author admin
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class FieldLogic implements IWhereClause {

    public static final String AND = "AND";
    public static final String OR = "OR";
    
    public static final String NOT="NOT";
    //条件关系
    private String logicOp = AND;

    public FieldLogic() {
    	
    }
    
    public FieldLogic(String logicOp){
        this.logicOp=logicOp;
    }
    
    private List<IWhereClause> commands = new ArrayList<IWhereClause>();

    public Predicate execute(CriteriaBuilder builder, Root root,Class entityClass) throws Exception {
        Predicate condition=null;
        for(IWhereClause cause:commands){
            if(condition==null){
                condition=cause.execute(builder, root,entityClass);
                if(NOT.equals(logicOp)){
                    condition=builder.not(condition);
                }
            }else{
                if(AND.equals(logicOp)){
                    condition=builder.and(condition,cause.execute(builder, root,entityClass));
                }else if(OR.equals(logicOp)){
                    condition=builder.or(condition,cause.execute(builder, root,entityClass));
                }else{
                    condition=builder.not(condition);
                }
            }
        }
        return condition;
    }
    
    public String getLogicOp() {
        return logicOp;
    }

    public void setLogicOp(String logicOp) {
        this.logicOp = logicOp;
    }

    public List<IWhereClause> getCommands() {
        return commands;
    }

    public void setCommands(List<IWhereClause> commands) {
        this.commands = commands;
    }

	public String getSql() {
		if(commands.size()==0) return "";
    	if(commands.size()==1 && !NOT.equals(logicOp)) return  commands.get(0).getSql();
    	 
    	StringBuffer sqlBuf=new StringBuffer("(");
    	int i=0;
    	if(commands.size()>0 && NOT.equals(logicOp)){
    		sqlBuf.append(" NOT (");
    		for(IWhereClause clause:commands){
    			if(i++>0){
            		sqlBuf.append(" ").append(AND).append(" ");
            	}
            	sqlBuf.append(clause.getSql());
    		}
    		sqlBuf.append(")");
    		
    		return sqlBuf.toString();
    	}
    	
        for(IWhereClause clause:commands){
        	if(i++>0){
        		sqlBuf.append(" ").append(AND).append(" ");
        	}
        	sqlBuf.append(clause.getSql());
        }
        sqlBuf.append(")");
        
        return sqlBuf.toString();
	}
	
	
    
}
