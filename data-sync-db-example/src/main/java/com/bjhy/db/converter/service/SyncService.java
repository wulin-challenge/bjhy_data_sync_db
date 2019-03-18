package com.bjhy.db.converter.service;

import com.bjhy.data.sync.db.domain.SingleRunEntity;

/**
 * 同步业务数据的Service
 * @author wubo
 *
 */
public interface SyncService {

	/**
	 * 执行同步
	 */
	public void runSync(SingleRunEntity singleRunEntity);
	/**
	 * 运行任务前
	 */
	public void runTaskBefore();
	/**
	 * 运行任务后
	 */
	public void runTaskAfter();
	
	/**
	 * 目标任务
	 */
	public String toTask();
	
	/**
	 * 来源任务
	 */
	public String fromTask();
	
	/**
	 * 多个任务的排序,数值越小,优先级就越高
	 * @return
	 */
	public int taskOrder();
	
	/**
	 * 相同任务的子任务排序,数值越小,优先级就越高
	 * @return
	 */
	public int order();
	
	
	
}
