package com.bjhy.data.sync.db.domain;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.util.ConcurrentHashSet;

/**
 * 同步步骤日志信息实体
 * <p> 注意:实体在一个步骤中只能存在一个实例
 * @author wubo
 */
public class SyncStepLogInfoEntity {
	
	/**
	 * 一个步骤中进行了多少次成功的insert操作
	 */
	private AtomicInteger insertCount = new AtomicInteger(0);
	
	/**
	 * 一个步骤中进行了多少次成功的update操作
	 */
	private AtomicInteger updateCount = new AtomicInteger(0);
	
	/**
	 * 一个步骤中进行了多少次成功的delete操作
	 */
	private AtomicInteger deleteCount = new AtomicInteger(0);
	
	/**
	 * 一个步骤中进行了多少次成功的no update操作,(即来源数据与目标数据是一致的,没有进行任何的insert或者update语句操作)
	 */
	private AtomicInteger noUpdateCount = new AtomicInteger(0);
	
	/**
	 * 一个步骤中进行了多少次失败的操作
	 */
	private AtomicInteger failCount = new AtomicInteger(0);
	
	/**
	 * 一个步骤中失败的具体信息
	 */
	private ConcurrentHashSet<String> failInfo = new ConcurrentHashSet<String>();

	public AtomicInteger getInsertCount() {
		return insertCount;
	}

	public void setInsertCount(AtomicInteger insertCount) {
		this.insertCount = insertCount;
	}

	public AtomicInteger getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(AtomicInteger updateCount) {
		this.updateCount = updateCount;
	}

	public AtomicInteger getNoUpdateCount() {
		return noUpdateCount;
	}

	public void setNoUpdateCount(AtomicInteger noUpdateCount) {
		this.noUpdateCount = noUpdateCount;
	}

	public AtomicInteger getFailCount() {
		return failCount;
	}

	public void setFailCount(AtomicInteger failCount) {
		this.failCount = failCount;
	}

	public ConcurrentHashSet<String> getFailInfo() {
		return failInfo;
	}

	public void setFailInfo(ConcurrentHashSet<String> failInfo) {
		this.failInfo = failInfo;
	}

	public AtomicInteger getDeleteCount() {
		return deleteCount;
	}

	public void setDeleteCount(AtomicInteger deleteCount) {
		this.deleteCount = deleteCount;
	}
}
