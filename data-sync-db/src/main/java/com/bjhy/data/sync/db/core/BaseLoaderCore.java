package com.bjhy.data.sync.db.core;

import java.util.List;

import com.bjhy.data.sync.db.domain.SyncConfig;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.init.InitStepStoreTable;
import com.bjhy.data.sync.db.init.InitVersionCheckTable;
import com.bjhy.data.sync.db.loader.DataSourceLoader;
import com.bjhy.data.sync.db.loader.SyncConfigLoader;

/**
 * 加载资源最核心的core
 * @author wubo
 *
 */
public class BaseLoaderCore {
	
	private static BaseLoaderCore baseLoaderCore;
	
	private BaseLoaderCore(){}
	
	/**
	 * 同步配置资源
	 */
	private SyncConfig syncConfig;
	
	/**
	 * 来源同步Template
	 */
	private List<SyncTemplate> fromSyncTemplate;
	
	/**
	 * 目标同步Template
	 */
	private List<SyncTemplate> toSyncTemplate;
	
	/**
	 * 本地存储的jdbcTemplate
	 */
	private SyncTemplate nativeStoreTemplate;
	
	/**
	 * 核心加载
	 */
	public void coreLoader(){
		refreshLoader();
		initLoader();
		
	}
	
	/**
	 * 重新加载
	 */
	private void refreshLoader(){
		
		//刷新加载同步配置信息
		refreshLoaderSyncConfig();
		
		//刷新加载同步DataSource
		refreshLoaderSyncDataSource();
		
	}
	
	/**
	 * 刷新加载同步配置信息
	 */
	public void refreshLoaderSyncConfig(){
		//加载同步资源配置
		SyncConfigLoader syncConfigLoader = new SyncConfigLoader();
		syncConfig = syncConfigLoader.configLoader();
	}
	
	/**
	 * 刷新加载同步DataSource
	 */
	public void refreshLoaderSyncDataSource(){
		DataSourceLoader dataSourceLoader = DataSourceLoader.getInstance();
		fromSyncTemplate = dataSourceLoader.getFromSyncTemplate();
		toSyncTemplate = dataSourceLoader.getToSyncTemplate();
	}
	
	/**
	 * 初始化本地存储的dataSource
	 */
	private void initNativeStoreDataSource(){
		//初始化本地存储的dataSource
		DataSourceLoader dataSourceLoader = DataSourceLoader.getInstance();
		nativeStoreTemplate = dataSourceLoader.getNativeStoreTemplate();
	}
	
	/**
	 * 初始化版本检查表及其他表
	 */
	private void initVersionCheckTable(){
		new InitVersionCheckTable(nativeStoreTemplate);//初始化版本检查表
		new InitStepStoreTable(nativeStoreTemplate);//初始化步骤存储表
	}
	
	/**
	 * 初始化时加载:也就是说这个方法只有程序启动时才会执行
	 */
	private void initLoader(){
		initNativeStoreDataSource();//初始化本地存储的dataSource
		initVersionCheckTable();//初始化版本检查表
	}
	
	/**
	 * 得到单个是实例对象(单例)
	 * @return
	 */
	public static BaseLoaderCore getInstance(){
		if(baseLoaderCore == null){
			baseLoaderCore = new BaseLoaderCore();
		}
		return baseLoaderCore;
	}

	public SyncConfig getSyncConfig() {
		return syncConfig;
	}

	public List<SyncTemplate> getFromSyncTemplate() {
		return fromSyncTemplate;
	}

	public List<SyncTemplate> getToSyncTemplate() {
		return toSyncTemplate;
	}

	public SyncTemplate getNativeStoreTemplate() {
		return nativeStoreTemplate;
	}

	public void setNativeStoreTemplate(SyncTemplate nativeStoreTemplate) {
		this.nativeStoreTemplate = nativeStoreTemplate;
	}
}
