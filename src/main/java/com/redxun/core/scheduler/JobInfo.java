package com.redxun.core.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

public class JobInfo {
	//任务名
	private String name="";
	//类名
	private String className="";
	//备注
	private String description="";
	
	private List<ParameterObj> paramList=new ArrayList<ParameterObj>();
	
	public JobInfo(){
		
	}
	
	
	public JobInfo(String name, String className, String description) {
		this.name = name;
		this.className = className;
		this.description = description;
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ParameterObj> getParamList() {
		return paramList;
	}

	public void setParamList(List<ParameterObj> paramList) {
		this.paramList = paramList;
	}
	
	public String getParamJson(){
		return JSONArray.toJSONString(paramList);
	}
	
}
