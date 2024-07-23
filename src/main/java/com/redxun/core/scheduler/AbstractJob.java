package com.redxun.core.scheduler;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.impl.jdbcjobstore.oracle.OracleDelegate;

import com.redxun.core.util.ExceptionUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;


/**
 * Job(任务)的执行时间[比如需要10秒]大于任务的时间间隔 [Interval（5秒)],
 * 那么默认情况下,调度框架为了能让 任务按照我们预定的时间间隔执行,
 * 会马上启用新的线程执行任务。否则的话会等待任务执行完毕以后 再重新执行
 * @author redxun
 *
 */
public abstract class AbstractJob implements Job {
	
	//抽象方法。
	public abstract void executeJob(JobExecutionContext context);
	

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//日志记录
		String jobName=context.getJobDetail().getKey().getName();
		OracleDelegate o;
		String trigName="directExec";
		Trigger trig=context.getTrigger();
		if(trig!=null)
			trigName=trig.getKey().getName();
		Date strStartTime=new Date();
		long startTime=System.currentTimeMillis();
		try{
			executeJob(context);
			long endTime=System.currentTimeMillis();
			Date strEndTime=new Date();
			//记录日志
			long runTime=(endTime-startTime) /1000;
			addLog(jobName, trigName, strStartTime, strEndTime, runTime, "任务执行成功!", "1");
		}
		catch(Exception ex)
		{
			String msg=ExceptionUtil.getExceptionMessage(ex);
			long endTime=System.currentTimeMillis();
			Date strEndTime=new Date();
			long runTime=(endTime-startTime) /1000;
			try {
				addLog(jobName, trigName, strStartTime, strEndTime, runTime,msg,"0");
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
	}

	private void addLog(String jobName, String trigName, Date startTime,
			Date endTime, long runTime,String content,String state) throws Exception {
		IJobLogService logService=WebAppUtil.getBean(IJobLogService.class);
	
		SysJobLog jobLog=new SysJobLog(jobName, trigName, startTime, endTime, content, state,runTime);
		
		String id=IdUtil.getId();
		jobLog.setId(id);
		if(logService!=null){
			logService.create(jobLog);
		}
		
	}

}
