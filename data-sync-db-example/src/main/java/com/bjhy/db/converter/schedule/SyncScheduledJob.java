package com.bjhy.db.converter.schedule;

import com.bjhy.db.converter.core.SyncEntry;
/**
 * 同步定时器作业
 * @author wubo
 *
 */
public class SyncScheduledJob implements IScheduled {
	
	/**
	 * cron表达式
	 */
	private String cronExpression;
	
	/**
	 * 是否应以并发方式运行多个作业
	 */
	private Boolean concurrent;
	
	/**
	 * 同步入口
	 */
	private SyncEntry syncEntry;

	@Override
	public void execute() {
		syncEntry.run();
	}

	@Override
	public String getCronExpression() {
		return cronExpression;
	}

	@Override
	public Boolean concurrent() {
		return concurrent;
	}

	@Override
	public String jobName() {
		return "syncScheduledJob";
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setConcurrent(Boolean concurrent) {
		this.concurrent = concurrent;
	}

	public void setSyncEntry(SyncEntry syncEntry) {
		this.syncEntry = syncEntry;
	}
}
