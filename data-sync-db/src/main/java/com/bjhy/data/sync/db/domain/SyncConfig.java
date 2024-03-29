package com.bjhy.data.sync.db.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步配置实体类
 * @author wubo
 *
 */
public class SyncConfig {

	/**
	 * 同步数据源加载方式
	 * 方式: sync.data.source.loader.model=prop,xml,web.other
	 */
	private List<String> syncDataSourceLoaderModel = new ArrayList<String>();
	
	/**
	 * 若SingleStepSyncConfig中isThisOnlyOne设置为true的步骤,sync.is.this.only.one=true才生效同步,默认为false
	 */
	private Boolean syncIsThisOnlyOne = false;
	
	/**
	 * 是否同步null值,true表示同步空值,否则反之
	 * 若配置文件中 sync.is.sync.null.value=true 那表示所有步骤都要同步空值
	 * 若配置文件中 sync.is.sync.null.value=false 而步骤中 isSyncNullValue = true 那表示该步骤要同步空值
	 */
	private Boolean syncSyncNullValue = true;
	
	/**
	 * 是否自动修复
	 */
	private Boolean syncIsAutoRepair = false;
	
	/**
	 * 目标最大线程数
	 */
	private Integer syncToMaxThreadNum = 1;
	
	/**
	 * 来源最大线程数
	 */
	private Integer syncFromMaxThreadNum = 1;
	
	/**
	 * 单表(即步骤)分页最大线程数
	 */
	private Integer syncTablePageMaxThreadNum = 1;
	
	/**
	 * 插入或者更新线程
	 */
	private Integer syncInsertOrUpdateMaxThreadNum = 1;
	
	/**
	 * 行拆分最大线程数
	 */
	private Integer syncPageRowThreadMaxThreadNum = 1;
	
	/**
	 * 拆分行最大列数量
	 */
	private Integer syncPageRowMaxColumnNum = 50;
	
	/**
	 * 同步警告列日志打印开关,true:打印所有日志,false:相同日志只打印一次
	 */
	private Boolean syncAlarmColumnLoggingPrint = false;
	
	/**
	 * 最大队列数
	 */
	private Integer maxQueueNum = 30;
	/**
	 * 批量提交数
	 */
	private Integer batchCommitNumber = 500;

	public List<String> getSyncDataSourceLoaderModel() {
		return syncDataSourceLoaderModel;
	}

	public void setSyncDataSourceLoaderModel(List<String> syncDataSourceLoaderModel) {
		this.syncDataSourceLoaderModel = syncDataSourceLoaderModel;
	}

	public Boolean getSyncIsThisOnlyOne() {
		return syncIsThisOnlyOne;
	}

	public void setSyncIsThisOnlyOne(Boolean syncIsThisOnlyOne) {
		this.syncIsThisOnlyOne = syncIsThisOnlyOne;
	}

	public Integer getSyncToMaxThreadNum() {
		return syncToMaxThreadNum;
	}

	public void setSyncToMaxThreadNum(Integer syncToMaxThreadNum) {
		this.syncToMaxThreadNum = syncToMaxThreadNum;
	}

	public Integer getSyncFromMaxThreadNum() {
		return syncFromMaxThreadNum;
	}

	public void setSyncFromMaxThreadNum(Integer syncFromMaxThreadNum) {
		this.syncFromMaxThreadNum = syncFromMaxThreadNum;
	}

	public Integer getSyncTablePageMaxThreadNum() {
		return syncTablePageMaxThreadNum;
	}

	public void setSyncTablePageMaxThreadNum(Integer syncTablePageMaxThreadNum) {
		this.syncTablePageMaxThreadNum = syncTablePageMaxThreadNum;
	}

	public Integer getSyncInsertOrUpdateMaxThreadNum() {
		return syncInsertOrUpdateMaxThreadNum;
	}

	public void setSyncInsertOrUpdateMaxThreadNum(Integer syncInsertOrUpdateMaxThreadNum) {
		this.syncInsertOrUpdateMaxThreadNum = syncInsertOrUpdateMaxThreadNum;
	}

	public Boolean getSyncSyncNullValue() {
		return syncSyncNullValue;
	}

	public void setSyncSyncNullValue(Boolean syncSyncNullValue) {
		this.syncSyncNullValue = syncSyncNullValue;
	}

	public Boolean getSyncIsAutoRepair() {
		return syncIsAutoRepair;
	}

	public void setSyncIsAutoRepair(Boolean syncIsAutoRepair) {
		this.syncIsAutoRepair = syncIsAutoRepair;
	}

	public Integer getSyncPageRowThreadMaxThreadNum() {
		return syncPageRowThreadMaxThreadNum;
	}

	public void setSyncPageRowThreadMaxThreadNum(Integer syncPageRowThreadMaxThreadNum) {
		this.syncPageRowThreadMaxThreadNum = syncPageRowThreadMaxThreadNum;
	}

	public Integer getSyncPageRowMaxColumnNum() {
		return syncPageRowMaxColumnNum;
	}

	public void setSyncPageRowMaxColumnNum(Integer syncPageRowMaxColumnNum) {
		this.syncPageRowMaxColumnNum = syncPageRowMaxColumnNum;
	}

	public Boolean getSyncAlarmColumnLoggingPrint() {
		return syncAlarmColumnLoggingPrint;
	}

	public void setSyncAlarmColumnLoggingPrint(Boolean syncAlarmColumnLoggingPrint) {
		this.syncAlarmColumnLoggingPrint = syncAlarmColumnLoggingPrint;
	}

	public Integer getMaxQueueNum() {
		return maxQueueNum;
	}

	public void setMaxQueueNum(Integer maxQueueNum) {
		this.maxQueueNum = maxQueueNum;
	}

	public Integer getBatchCommitNumber() {
		return batchCommitNumber;
	}

	public void setBatchCommitNumber(Integer batchCommitNumber) {
		this.batchCommitNumber = batchCommitNumber;
	}
}
