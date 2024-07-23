package com.redxun.core.json;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * 返回的Json格式字符串,包括是否成功的状态及返回的格式字符串
 * @author csx
 *@Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class JsonResult<T> implements Serializable{
	//是否成功
	private boolean success=false;
	//返回消息
	private String message="";
	//数据
	private T data=null;
	//扩展属性
	private String extPros=null;
	
	
	public JsonResult(){
	}
	
	public JsonResult(boolean success){
		this.success=success;
	}
	
	public JsonResult(boolean success, String message) {
		this.success = success;
		this.message=message;
	}
	

	
	public JsonResult(boolean success,String message,T data){
		this.success=success;
		this.message=message;
		this.data=data;
	}

	public boolean isSuccess() {
		return success;
	}
	
	public boolean getSuccess() {
		return success;
	}
	
	
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getExtPros() {
		return extPros;
	}

	public void setExtPros(String extPros) {
		this.extPros = extPros;
	}

	@Override
	public String toString() {
		
		
		return JSONObject.toJSONString(this);
	}

	

	
}