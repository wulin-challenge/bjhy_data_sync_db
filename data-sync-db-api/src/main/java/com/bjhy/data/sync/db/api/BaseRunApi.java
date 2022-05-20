package com.bjhy.data.sync.db.api;

import com.bjhy.data.sync.db.core.BaseLoaderCore;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SyncConfig;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.init.InitStepStoreTable;
import com.bjhy.data.sync.db.init.InitVersionCheckTable;

/**
 * 无任何配置文件的代码侵入式实现同步
 * @author wulin
 *
 */
public class BaseRunApi {
	
	/**
	 * 同步配置资源
	 */
	private SyncConfig syncConfig;
	
	/**
	 * 来源同步Template
	 */
	private SyncTemplate fromSyncTemplate;
	
	/**
	 * 目标同步Template
	 */
	private SyncTemplate toSyncTemplate;
	
	/**
	 * 本地存储的jdbcTemplate
	 */
	private SyncTemplate nativeStoreTemplate;
	
	
	public BaseRunApi() {
		initDefaultConfig();
	}
	
	/**
	 * 初始化版本检查表及其他表
	 */
	public void initVersionCheckTable(){
		new InitVersionCheckTable(nativeStoreTemplate);//初始化版本检查表
		new InitStepStoreTable(nativeStoreTemplate);//初始化步骤存储表
	}
	
	public SingleRunEntity getSingleRunEntity(BaseRunEntity baseRunEntity) {
		SingleRunEntity singleRunEntity = new SingleRunEntity();
		singleRunEntity.setNativeTemplate(nativeStoreTemplate);
		singleRunEntity.setToTemplate(toSyncTemplate);
		singleRunEntity.setFromTemplate(fromSyncTemplate);
		singleRunEntity.setFromIndex(0);
		singleRunEntity.setToIndex(0);
		singleRunEntity.setBaseRunEntity(baseRunEntity);
		return singleRunEntity;
	}
	
	/**
	 * 初始化默认配置
	 */
	private void initDefaultConfig() {
		syncConfig = new SyncConfig();
		syncConfig.setSyncIsThisOnlyOne(true);
		syncConfig.setSyncSyncNullValue(false);
		syncConfig.setSyncIsAutoRepair(false);
		syncConfig.setSyncToMaxThreadNum(1);
		syncConfig.setSyncFromMaxThreadNum(1);
		syncConfig.setSyncTablePageMaxThreadNum(1);
		syncConfig.setSyncInsertOrUpdateMaxThreadNum(1);
		syncConfig.setSyncPageRowThreadMaxThreadNum(1);
		syncConfig.setSyncPageRowMaxColumnNum(1);
		syncConfig.setSyncAlarmColumnLoggingPrint(false);
		
		BaseLoaderCore.getInstance().setSyncConfig(syncConfig);
	}
	
	public SyncConfig getSyncConfig() {
		return syncConfig;
	}


	public void setSyncConfig(SyncConfig syncConfig) {
		this.syncConfig = syncConfig;
	}

	public SyncTemplate getFromSyncTemplate() {
		return fromSyncTemplate;
	}

	public void setFromSyncTemplate(SyncTemplate fromSyncTemplate) {
		this.fromSyncTemplate = fromSyncTemplate;
	}

	public SyncTemplate getToSyncTemplate() {
		return toSyncTemplate;
	}

	public void setToSyncTemplate(SyncTemplate toSyncTemplate) {
		this.toSyncTemplate = toSyncTemplate;
	}

	public SyncTemplate getNativeStoreTemplate() {
		return nativeStoreTemplate;
	}

	public void setNativeStoreTemplate(SyncTemplate nativeStoreTemplate) {
		this.nativeStoreTemplate = nativeStoreTemplate;
	}
}
