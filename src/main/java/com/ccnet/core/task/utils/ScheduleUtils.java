package com.ccnet.core.task.utils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import com.ccnet.core.entity.TaskSchedule;


public class ScheduleUtils {
	
	/** 日志对象 */
    private static final Logger LOG = Logger.getLogger(ScheduleUtils.class);
    
    /** 任务调度的参数key */
    public static final String JOB_PARAM_KEY    = "scheduleJob";
    
    /**
     * 获取触发器key
     * @param jobName
     * @param jobGroup
     * @return
     */
    public static TriggerKey getTriggerKey(String jobName, String jobGroup) {
        return TriggerKey.triggerKey(jobName, jobGroup);
    }
    /**
     * 获取表达式触发器
     * @param scheduler the scheduler
     * @param jobName the job name
     * @param jobGroup the job group
     * @return cron trigger
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, String jobName, String jobGroup) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            return (CronTrigger) scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            LOG.error("获取定时任务CronTrigger出现异常", e);
        }
		return null;
    }
    
    /**
     * 创建任务
     *
     * @param scheduler the scheduler
     * @param scheduleJob the schedule job
     * @throws Exception 
     */
    public static void createTaskSchedule(Scheduler scheduler, TaskSchedule taskSchedule) throws Exception {
    	//任务执行类
		Class<? extends Job> jobClass =(Class<? extends Job>) Class.forName (taskSchedule.getJobClass());
    	createTaskSchedule(scheduler, taskSchedule.getJobName(), taskSchedule.getJobGroup(),
    			taskSchedule.getCronExpression(),taskSchedule,jobClass);
    }
    
    /**
     * 创建定时任务
     * @param scheduler the scheduler
     * @param jobName the job name
     * @param jobGroup the job group
     * @param cronExpression the cron expression
     * @param isSync the is sync
     * @param param the param
     * @throws Exception 
     */
	public static void createTaskSchedule(Scheduler scheduler, String jobName, String jobGroup, String cronExpression, Object param,Class<? extends Job> jobClass) throws Exception {
	
		//
		// 构建job信息
		JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();
		// 放入参数，运行时的方法可以获取
		jobDetail.getJobDataMap().put(ScheduleUtils.JOB_PARAM_KEY, param);
		// 表达式调度构建器
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		// 按新的cronExpression表达式构建一个新的trigger
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(scheduleBuilder).build();
	    scheduler.scheduleJob(jobDetail, trigger);
	
	}
    
    /**
     * 更新定时任务
     * @param scheduler the scheduler
     * @param scheduleJob the schedule job
     * @throws SchedulerException 
     */
    public static void updateTaskSchedule(Scheduler scheduler, TaskSchedule taskSchedule) throws SchedulerException {
        updateTaskSchedule(scheduler, taskSchedule.getJobName(), taskSchedule.getJobGroup(),
        		taskSchedule.getCronExpression(), taskSchedule);
    }

    /**
     * 更新定时任务
     *
     * @param scheduler the scheduler
     * @param jobName the job name
     * @param jobGroup the job group
     * @param cronExpression the cron expression
     * @param param the param
     * @throws SchedulerException 
     */
    public static void updateTaskSchedule(Scheduler scheduler, String jobName, String jobGroup,
                                         String cronExpression, Object param) throws SchedulerException {
            TriggerKey triggerKey = ScheduleUtils.getTriggerKey(jobName, jobGroup);
            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            //按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
    }
      
    /**
     * 获取jobKey
     * @param jobName the job name
     * @param jobGroup the job group
     * @return the job key
     */
    public static JobKey getJobKey(String jobName, String jobGroup) {
        return JobKey.jobKey(jobName, jobGroup);
    }
    
    /**
     * 删除定时任务
     *
     * @param scheduler
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException 
     */
    public static void deleteTaskScheduler(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
           scheduler.deleteJob(getJobKey(jobName, jobGroup));
    }
    
    /**
     * 恢复任务
     * @param scheduler
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException 
     */
    public static void resumeJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = getJobKey(jobName, jobGroup);
        scheduler.resumeJob(jobKey);
    }
    
    /**
     * 暂停任务
     * @param scheduler
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException 
     */
    public static void pauseJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey =getJobKey(jobName, jobGroup);
        scheduler.pauseJob(jobKey);
    }
    
    /**
     * 运行一次任务
     * @param scheduler
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException 
     */
    public static void runOnce(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = getJobKey(jobName, jobGroup);
        scheduler.triggerJob(jobKey);
    }
}
