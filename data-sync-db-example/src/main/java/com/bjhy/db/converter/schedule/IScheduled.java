package com.bjhy.db.converter.schedule;

/**
 * 定时器接口
 * @author wubo
 *
 */
public interface IScheduled {

	/**
	 * 定时执行
	 */
	public void execute();
	
	/**
	 * 得到cron表达式
	 * @return
	 */
	public String getCronExpression();
	
	/**
	 * 是否应以并发方式运行多个作业
	 */
	public Boolean concurrent();
	
	/**
	 * 运行作业的bean名称
	 * @return
	 */
	public String jobName();
	
}
