package com.redxun.core.scheduler;

/**
 * 计划对象
 * 
 * @author ray
 * 
 */
public class PlanObject {
	/** 启动一次 */
	public static final int TYPE_FIRST = 1;
	/** 每分钟执行 */
	public static final int TYPE_PER_MINUTE = 2;
	/** 每天时间点执行 */
	public static final int TYPE_PER_DAY = 3;
	/** 每周时间点执行 */
	public static final int TYPE_PER_WEEK = 4;
	/** 每月执行 */
	public static final int TYPE_PER_MONTH = 5;
	/** cron表达式 */
	public static final int TYPE_CRON = 6;

	/**
	 * 计划类型
	 * 
	 * <pre>
	 * 1:启动一次 
	 * 2:每分钟执行
	 * 3:每天时间点执行 
	 * 4:每周时间点执行 
	 * 5:每月执行 
	 * 6:cron表达式
	 * </pre>
	 */
	private int type = 0;

	/**
	 * 定时间隔
	 */
	private String timeInterval = "";

	public int getType() {

		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(String timeInterval) {
		this.timeInterval = timeInterval;
	}

}
