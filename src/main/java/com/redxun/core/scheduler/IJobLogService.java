package com.redxun.core.scheduler;

/**
 * 任务日志接口类。
 * @author redxun
 */
public interface IJobLogService {
	
	/**
	 * 创建日志。
	 * @param jobLog
	 */
	void create(SysJobLog jobLog);

}
