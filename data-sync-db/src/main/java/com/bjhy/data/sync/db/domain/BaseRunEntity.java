package com.bjhy.data.sync.db.domain;

import com.bjhy.data.sync.db.core.BaseLoaderCore;

/**
 * 基本架构运行参数实体
 * @author wubo
 */
public class BaseRunEntity {
	
	SyncConfig syncConfig = null;
	
	public BaseRunEntity(){
		syncConfig = BaseLoaderCore.getInstance().getSyncConfig();
	}
	
	public BaseRunEntity(String fromTask,String toTask){
		syncConfig = BaseLoaderCore.getInstance().getSyncConfig();
		this.fromTask = fromTask;
		this.toTask = toTask;
	}
	
	/**
	 * 若SingleStepSyncConfig中isThisOnlyOne设置为true的步骤,sync.is.this.only.one=true才生效同步,默认为false
	 */
	private Boolean isThisOnlyOne = false;
	
	/**
	 * 是否自动修复
	 */
	private Boolean isAutoRepair = false;
	
	/**
	 * 是否同步null值,true表示同步空值,否则反之
	 * 若配置文件中 sync.is.sync.null.value=true 那表示所有步骤都要同步空值
	 * 若配置文件中 sync.is.sync.null.value=false 而步骤中 isSyncNullValue = true 那表示该步骤要同步空值
	 */
	private Boolean isSyncNullValue = false;
	
	/**
	 * 目标最大线程数
	 */
	private Integer toMaxThreadNum = 1;
	
	/**
	 * 来源最大线程数
	 */
	private Integer fromMaxThreadNum = 1;
	
	/**
	 * 单表(即步骤)分页最大线程数
	 */
	private Integer tablePageMaxThreadNum=1;
	
	/**
	 * 插入或者更新线程
	 */
	private Integer insertOrUpdateMaxThreadNum=1;
	
	/**
	 * 目标任务
	 */
	private String toTask;
	
	/**
	 * 来源任务
	 */
	private String fromTask;
	
	
	public Integer getToMaxThreadNum() {
		Integer syncToMaxThreadNum = syncConfig.getSyncToMaxThreadNum();
		if(syncToMaxThreadNum != null){
			return syncToMaxThreadNum;
		}
		return toMaxThreadNum;
	}

	public void setToMaxThreadNum(Integer toMaxThreadNum) {
		this.toMaxThreadNum = toMaxThreadNum;
	}

	public Integer getFromMaxThreadNum() {
		
		Integer syncFromMaxThreadNum = syncConfig.getSyncFromMaxThreadNum();
		if(syncFromMaxThreadNum != null){
			return syncFromMaxThreadNum;
		}
		return fromMaxThreadNum;
	}

	public void setFromMaxThreadNum(Integer fromMaxThreadNum) {
		this.fromMaxThreadNum = fromMaxThreadNum;
	}

	public Integer getTablePageMaxThreadNum() {
		Integer syncTablePageMaxThreadNum = syncConfig.getSyncTablePageMaxThreadNum();
		if(syncTablePageMaxThreadNum != null){
			return syncTablePageMaxThreadNum;
		}
		return tablePageMaxThreadNum;
	}

	public void setTablePageMaxThreadNum(Integer tablePageMaxThreadNum) {
		this.tablePageMaxThreadNum = tablePageMaxThreadNum;
	}

	public String getToTask() {
		return toTask;
	}

	public void setToTask(String toTask) {
		this.toTask = toTask;
	}

	public String getFromTask() {
		return fromTask;
	}

	public void setFromTask(String fromTask) {
		this.fromTask = fromTask;
	}

	public Integer getInsertOrUpdateMaxThreadNum() {
		
		Integer syncInsertOrUpdateMaxThreadNum = syncConfig.getSyncInsertOrUpdateMaxThreadNum();
		if(syncInsertOrUpdateMaxThreadNum != null){
			return syncInsertOrUpdateMaxThreadNum;
		}
		return insertOrUpdateMaxThreadNum;
	}

	public void setInsertOrUpdateMaxThreadNum(Integer insertOrUpdateMaxThreadNum) {
		this.insertOrUpdateMaxThreadNum = insertOrUpdateMaxThreadNum;
	}

	public Boolean getIsThisOnlyOne() {
		Boolean syncIsThisOnlyOne = syncConfig.getSyncIsThisOnlyOne();
		if(syncIsThisOnlyOne != null){
			return syncIsThisOnlyOne;
		}
		return isThisOnlyOne;
	}

	public void setIsThisOnlyOne(Boolean isThisOnlyOne) {
		this.isThisOnlyOne = isThisOnlyOne;
	}

	public Boolean getIsAutoRepair() {
		
		Boolean syncIsAutoRepair = syncConfig.getSyncIsAutoRepair();
		if(syncIsAutoRepair != null){
			return syncIsAutoRepair;
		}
		return isAutoRepair;
	}

	public void setIsAutoRepair(Boolean isAutoRepair) {
		this.isAutoRepair = isAutoRepair;
	}

	public Boolean getIsSyncNullValue() {
		Boolean syncSyncNullValue = syncConfig.getSyncSyncNullValue();
		if(syncSyncNullValue != null){
			return syncSyncNullValue;
		}
		return isSyncNullValue;
	}

	public void setIsSyncNullValue(Boolean isSyncNullValue) {
		this.isSyncNullValue = isSyncNullValue;
	}

	public SyncConfig getSyncConfig() {
		return syncConfig;
	}

	public void setSyncConfig(SyncConfig syncConfig) {
		this.syncConfig = syncConfig;
	}
	
}
