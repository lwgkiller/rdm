package com.redxun.core.scheduler;

/**
 * 定时器对象。
 * @author ray
 *
 */
public class TriggerModel {
	

	
	private String jobName="";
	
	private String triggerName="";
	
	private String description="";
	
	private String state="";
	
	public TriggerModel(){
		
	}
	

	public TriggerModel(String jobName, String triggerName, String triggerDescription, String state) {
		this.jobName = jobName;
		this.triggerName = triggerName;
		this.description = triggerDescription;
		this.state = state;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String triggerDescription) {
		this.description = triggerDescription;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	

}
