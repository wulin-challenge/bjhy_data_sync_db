package com.bjhy.db.converter.schedule;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/**
 * 定时器作业创建工厂
 * 
 * @author wubo
 *
 */
public class ScheduledFactory {

	private Logger logger = Logger.getLogger(ScheduledFactory.class);

	/**
	 * 具体要执行的作业
	 */
	private IScheduled scheduledJob;

	/**
	 * quartz 的Scheduler
	 */
	private Scheduler scheduler;

	public void init() {

		try {
			MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
			jobDetail.setTargetObject(scheduledJob);
			jobDetail.setTargetMethod("execute");
			jobDetail.setName(scheduledJob.jobName());
			jobDetail.setConcurrent(scheduledJob.concurrent());
			jobDetail.afterPropertiesSet();

			CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
			cronTrigger.setBeanName(scheduledJob.jobName() + "CronTrigger");
			cronTrigger.setCronExpression(scheduledJob.getCronExpression());
			cronTrigger.setJobDetail(jobDetail.getObject());
			cronTrigger.afterPropertiesSet();

			scheduler.scheduleJob(jobDetail.getObject(), cronTrigger.getObject());
			scheduler.start();

		} catch (Exception e) {
			logger.error("创建定时器作业失败!", e);
		}
	}

	public IScheduled getScheduledJob() {
		return scheduledJob;
	}

	public void setScheduledJob(IScheduled scheduledJob) {
		this.scheduledJob = scheduledJob;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
}
