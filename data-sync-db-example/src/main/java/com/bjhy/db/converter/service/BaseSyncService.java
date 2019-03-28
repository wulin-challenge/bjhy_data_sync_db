package com.bjhy.db.converter.service;

import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.util.LoggerUtils;
import com.bjhy.db.converter.util.DataBaseConstraintHelper;

/**
 * 同步service基类
 * @author wubo
 *
 */
public abstract class BaseSyncService implements SyncService{
	
	/**
	 * 任务开始时间
	 */
	protected static long taskBeginTime;
	
	/**
	 * 任务结束时间
	 */
	protected static long taskEndTime;

	@Override
	public void runSync(SingleRunEntity singleRunEntity) {
		//禁用oracle的相关约束
//		DataBaseConstraintHelper.disableOracleConstrain(singleRunEntity.getToTemplate());
		doRunSync(singleRunEntity);
		//起用oracle的相关约束
//		DataBaseConstraintHelper.enableOracleConstrain(singleRunEntity.getToTemplate());
	}
	
	public abstract void doRunSync(SingleRunEntity singleRunEntity);

	@Override
	public void runTaskBefore() {
		taskBeginTime = System.currentTimeMillis();
//		System.out.println("runTaskBefore--");
	}

	@Override
	public void runTaskAfter() {
		taskEndTime = System.currentTimeMillis();
		long s = (taskEndTime-taskBeginTime)/1000;
		long m = s/60;
		LoggerUtils.info("总共的同步时间: "+m+" 分,"+s+" 秒");
//		System.out.println("--runTaskAfter");
	}

	@Override
	public String toTask() {
		return "to_new_yz_task";
	}

	@Override
	public String fromTask() {
		return "from_old_yz_task";
	}

	@Override
	public int taskOrder() {
		return 0;
	}

	@Override
	public int order() {
		return 0;
	}
}
