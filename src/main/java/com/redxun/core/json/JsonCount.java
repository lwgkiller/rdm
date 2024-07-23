package com.redxun.core.json;

import java.io.Serializable;
/**
 * 返回json总数
 * @author think
 *
 */
public class JsonCount implements Serializable{
	private boolean success=false;
	private Long totals=0L;
	private String msg="";

	public JsonCount() {
		
	}
	
	public JsonCount(boolean success,Long totals,String msg){
		this.success=success;
		this.totals=totals;
		this.msg=msg;
	}
	
	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Long getTotals() {
		return totals;
	}

	public void setTotals(Long totals) {
		this.totals = totals;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
