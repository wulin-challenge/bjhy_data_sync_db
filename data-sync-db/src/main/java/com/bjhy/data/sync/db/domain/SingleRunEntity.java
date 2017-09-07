package com.bjhy.data.sync.db.domain;

/**
 * 当个数据源运行的Entity
 * @author wubo
 *
 */
public class SingleRunEntity {
	
	/**
	 * 基本架构运行参数实体
	 */
	private BaseRunEntity baseRunEntity;
	/**
	 * 来源数据源
	 */
	private SyncTemplate fromTemplate;
	
	/**
	 * 目标数据源
	 */
	private SyncTemplate toTemplate;
	
	/**
	 * 本地存储数据源
	 */
	private SyncTemplate nativeTemplate;
	
	/**
	 * 当前来源的index
	 */
	private int fromIndex;
	
	/**
	 * 当前目标的index
	 */
	private int toIndex;

	public SyncTemplate getFromTemplate() {
		return fromTemplate;
	}

	public void setFromTemplate(SyncTemplate fromTemplate) {
		this.fromTemplate = fromTemplate;
	}

	public SyncTemplate getToTemplate() {
		return toTemplate;
	}

	public void setToTemplate(SyncTemplate toTemplate) {
		this.toTemplate = toTemplate;
	}

	public SyncTemplate getNativeTemplate() {
		return nativeTemplate;
	}

	public void setNativeTemplate(SyncTemplate nativeTemplate) {
		this.nativeTemplate = nativeTemplate;
	}

	public int getFromIndex() {
		return fromIndex;
	}

	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}

	public int getToIndex() {
		return toIndex;
	}

	public void setToIndex(int toIndex) {
		this.toIndex = toIndex;
	}

	public BaseRunEntity getBaseRunEntity() {
		return baseRunEntity;
	}

	public void setBaseRunEntity(BaseRunEntity baseRunEntity) {
		this.baseRunEntity = baseRunEntity;
	}
}
