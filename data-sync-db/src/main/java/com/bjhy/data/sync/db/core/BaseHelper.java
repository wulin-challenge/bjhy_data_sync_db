package com.bjhy.data.sync.db.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bjhy.data.sync.db.domain.AddColumnAttribute;
import com.bjhy.data.sync.db.domain.IncrementalSync;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncPageRowEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

/**
 * 提供各种快捷的同步封装及同步实现
 * @author wubo
 *
 */
public class BaseHelper {
	
	/**
	 * 该同步开启了版本检查,高性能多线程同步,空值不同步,自动添加了 jymc,jybh字段
	 * @param singleRunEntity
	 * @param fromTableName
	 * @param toTableName
	 * @param updateAndPageColumn
	 * @return
	 */
	public static void checkSync(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn){
		BaseCore baseCore = new BaseCore();
		
		String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		
		String fromSelectPart = "select * ";
		String fromFromPart = "from "+fromTableName;
		SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName+"_"+toTableName);
		singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		singleStepSyncConfig.setFromFromPart(fromFromPart);
		singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		singleStepSyncConfig.setToTableName(toTableName);
		singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		singleStepSyncConfig.setIsAddVersionCheckFilter(true);
		singleStepSyncConfig.setIsMultiThreadPage(true);
		singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		singleStepSyncConfig.setIsSyncNullValue(false);
		singleStepSyncConfig.getAddStaticFromColumns().put("jymc", dataSourceName);
		singleStepSyncConfig.getAddStaticFromColumns().put("jybh", dataSourceNumber);
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jymc", toTableName));
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jybh", toTableName));
		singleStepSyncConfig.setToValidationWhere(" where jybh=:jybh ");
		
		//增量同步配置
		IncrementalSync incrementalSync = new IncrementalSync();
		incrementalSync.setUniqueValueKey(updateAndPageColumn);
		incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
		incrementalSync.getAlarmColumn().add("JYBH");
		incrementalSync.getAlarmColumn().add("JYMC");
		singleStepSyncConfig.setIncrementalSync(incrementalSync);
		
		baseCore.syncEntry(singleStepSyncConfig);
	}
	
	/**
	 * 该方法添加同步过程中的回调监听
	 * @param singleRunEntity
	 * @param fromTableName
	 * @param toTableName
	 * @param updateAndPageColumn
	 * @param singleStepListener
	 * @return
	 */
	public static void checkSync(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn,SingleStepListener singleStepListener){
		BaseCore baseCore = new BaseCore();
		
		String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		
		String fromSelectPart = "select * ";
		String fromFromPart = "from "+fromTableName;
		SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName+"_"+toTableName);
		singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		singleStepSyncConfig.setFromFromPart(fromFromPart);
		singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		singleStepSyncConfig.setToTableName(toTableName);
		singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		singleStepSyncConfig.setIsAddVersionCheckFilter(true);
		singleStepSyncConfig.setIsMultiThreadPage(true);
		singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		singleStepSyncConfig.getAddStaticFromColumns().put("jymc", dataSourceName);
		singleStepSyncConfig.getAddStaticFromColumns().put("jybh", dataSourceNumber);
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jymc", toTableName));
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jybh", toTableName));
		singleStepSyncConfig.setSingleStepListener(singleStepListener);
		singleStepSyncConfig.setToValidationWhere(" where jybh=:jybh ");
		
		//增量同步配置
		IncrementalSync incrementalSync = new IncrementalSync();
		incrementalSync.setUniqueValueKey(updateAndPageColumn);
		incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
		incrementalSync.getAlarmColumn().add("JYBH");
		incrementalSync.getAlarmColumn().add("JYMC");
		singleStepSyncConfig.setIncrementalSync(incrementalSync);
		
		baseCore.syncEntry(singleStepSyncConfig);
	}
	
	
	/**
	 * 该同步开启了版本检查,高性能多线程同步,空值不同步,自动添加了 jymc,jybh字段
	 * @param singleRunEntity
	 * @param fromTableName
	 * @param toTableName
	 * @param updateAndPageColumn
	 * @return
	 */
	public static void checkSyncNotNull(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn){
		BaseCore baseCore = new BaseCore();
		
		String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		
		String fromSelectPart = "select * ";
		String fromFromPart = "from "+fromTableName;
		SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName+"_"+toTableName);
		singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		singleStepSyncConfig.setFromFromPart(fromFromPart);
		singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		singleStepSyncConfig.setToTableName(toTableName);
		singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		singleStepSyncConfig.setIsSyncNullValue(false);
		singleStepSyncConfig.setIsAddVersionCheckFilter(true);
		singleStepSyncConfig.setIsMultiThreadPage(true);
		singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		singleStepSyncConfig.getAddStaticFromColumns().put("jymc", dataSourceName);
		singleStepSyncConfig.getAddStaticFromColumns().put("jybh", dataSourceNumber);
		singleStepSyncConfig.setToValidationWhere(" where jybh=:jybh ");
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jymc", toTableName));
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jybh", toTableName));
		
		//增量同步配置
		IncrementalSync incrementalSync = new IncrementalSync();
		incrementalSync.setUniqueValueKey(updateAndPageColumn);
		incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
		incrementalSync.getAlarmColumn().add("JYBH");
		incrementalSync.getAlarmColumn().add("JYMC");
		singleStepSyncConfig.setIncrementalSync(incrementalSync);
		
		baseCore.syncEntry(singleStepSyncConfig);
	}
	/**
	 * 该同步开启了版本检查,高性能多线程同步,空值不同步,自动添加了 jymc,jybh字段(并且拆分行)
	 * @param singleRunEntity
	 * @param fromTableName
	 * @param toTableName
	 * @param updateAndPageColumn
	 * @return
	 */
	public static void checkSyncNotNullAndPageRow(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn){
		BaseCore baseCore = new BaseCore();
		
		String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		
		String fromSelectPart = "select * ";
		String fromFromPart = "from "+fromTableName;
		SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName+"_"+toTableName);
		singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		singleStepSyncConfig.setFromFromPart(fromFromPart);
		singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		singleStepSyncConfig.setToTableName(toTableName);
		singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		singleStepSyncConfig.setIsSyncNullValue(false);
		singleStepSyncConfig.setIsAddVersionCheckFilter(true);
		singleStepSyncConfig.setIsMultiThreadPage(true);
		singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		singleStepSyncConfig.getAddStaticFromColumns().put("jymc", dataSourceName);
		singleStepSyncConfig.getAddStaticFromColumns().put("jybh", dataSourceNumber);
		singleStepSyncConfig.setToValidationWhere(" where jybh=:jybh ");
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jymc", toTableName));
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jybh", toTableName));
		//拆分行配置
		SyncPageRowEntity syncPageRowEntity =new SyncPageRowEntity();
		List<String> pageRowInsertColumns = new ArrayList<String>();
		pageRowInsertColumns.add(updateAndPageColumn);
		pageRowInsertColumns.add(VersionCheckCore.SYNC_VERSION_CHECK);
		syncPageRowEntity.setPageRowInsertColumns(pageRowInsertColumns);
		singleStepSyncConfig.setSyncPageRowEntity(syncPageRowEntity);
		
		//增量同步配置
		IncrementalSync incrementalSync = new IncrementalSync();
		incrementalSync.setUniqueValueKey(updateAndPageColumn);
		incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
		incrementalSync.getAlarmColumn().add("JYBH");
		incrementalSync.getAlarmColumn().add("JYMC");
		singleStepSyncConfig.setIncrementalSync(incrementalSync);
		
		//开始同步
		baseCore.syncEntry(singleStepSyncConfig);
	}
	
	/**
	 * 该方法添加同步过程中的回调监听
	 * @param singleRunEntity
	 * @param fromTableName
	 * @param toTableName
	 * @param updateAndPageColumn
	 * @param singleStepListener
	 * @return
	 */
	public static void checkSyncNotNull(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn,SingleStepListener singleStepListener){
		BaseCore baseCore = new BaseCore();
		
		String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		
		String fromSelectPart = "select * ";
		String fromFromPart = "from "+fromTableName;
		SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName+"_"+toTableName);
		singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		singleStepSyncConfig.setFromFromPart(fromFromPart);
		singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		singleStepSyncConfig.setToTableName(toTableName);
		singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		singleStepSyncConfig.setIsAddVersionCheckFilter(true);
		singleStepSyncConfig.setIsSyncNullValue(false);
		singleStepSyncConfig.setIsMultiThreadPage(true);
		singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		singleStepSyncConfig.getAddStaticFromColumns().put("jymc", dataSourceName);
		singleStepSyncConfig.getAddStaticFromColumns().put("jybh", dataSourceNumber);
		singleStepSyncConfig.setSingleStepListener(singleStepListener);
		singleStepSyncConfig.setToValidationWhere(" where jybh=:jybh ");
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jymc", toTableName));
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jybh", toTableName));
		
		//增量同步配置
		IncrementalSync incrementalSync = new IncrementalSync();
		incrementalSync.setUniqueValueKey(updateAndPageColumn);
		incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
		incrementalSync.getAlarmColumn().add("JYBH");
		incrementalSync.getAlarmColumn().add("JYMC");
		singleStepSyncConfig.setIncrementalSync(incrementalSync);
		
		baseCore.syncEntry(singleStepSyncConfig);
	}
	
	
	/**
	 * 该方法添加同步过程中的回调监听
	 * @param singleRunEntity
	 * @param fromTableName
	 * @param toTableName
	 * @param updateAndPageColumn
	 * @param singleStepListener
	 * @return
	 */
	public static void checkSyncNotNull(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn,String singleStepListenerName){
		BaseCore baseCore = new BaseCore();
		
		String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		
		String fromSelectPart = "select * ";
		String fromFromPart = "from "+fromTableName;
		SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName+"_"+toTableName);
		singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		singleStepSyncConfig.setFromFromPart(fromFromPart);
		singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		singleStepSyncConfig.setToTableName(toTableName);
		singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		singleStepSyncConfig.setIsAddVersionCheckFilter(true);
		singleStepSyncConfig.setIsSyncNullValue(false);
		singleStepSyncConfig.setIsMultiThreadPage(true);
		singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		singleStepSyncConfig.getAddStaticFromColumns().put("jymc", dataSourceName);
		singleStepSyncConfig.getAddStaticFromColumns().put("jybh", dataSourceNumber);
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jymc", toTableName));
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jybh", toTableName));
		singleStepSyncConfig.setSingleStepListenerName(singleStepListenerName);
		singleStepSyncConfig.setToValidationWhere(" where jybh=:jybh ");
		
		//增量同步配置
		IncrementalSync incrementalSync = new IncrementalSync();
		incrementalSync.setUniqueValueKey(updateAndPageColumn);
		incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
		incrementalSync.getAlarmColumn().add("JYBH");
		incrementalSync.getAlarmColumn().add("JYMC");
		singleStepSyncConfig.setIncrementalSync(incrementalSync);
		
		baseCore.syncEntry(singleStepSyncConfig);
	}
	
	/**
	 * 该同步开启了版本检查,高性能多线程同步,空值不同步,自动添加了 jymc,jybh字段,以及只同步一次步骤功能
	 * @param singleRunEntity
	 * @param fromTableName
	 * @param toTableName
	 * @param updateAndPageColumn
	 * @return
	 */
	public static void checkOnlyOneSync(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn){
		BaseCore baseCore = new BaseCore();
		
		String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		
		String fromSelectPart = "select * ";
		String fromFromPart = "from "+fromTableName;
		SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName+"_"+toTableName);
		singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		singleStepSyncConfig.setFromFromPart(fromFromPart);
		singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		singleStepSyncConfig.setToTableName(toTableName);
		singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		singleStepSyncConfig.setIsAddVersionCheckFilter(true);
		singleStepSyncConfig.setIsMultiThreadPage(true);
		singleStepSyncConfig.setIsThisOnlyOneSync(true);
		singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		singleStepSyncConfig.getAddStaticFromColumns().put("jymc", dataSourceName);
		singleStepSyncConfig.getAddStaticFromColumns().put("jybh", dataSourceNumber);
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jymc", toTableName));
		singleStepSyncConfig.getAddToTableColumns().add(new AddColumnAttribute("jybh", toTableName));
		singleStepSyncConfig.setToValidationWhere(" where jybh=:jybh ");
		
		//增量同步配置
		IncrementalSync incrementalSync = new IncrementalSync();
		incrementalSync.setUniqueValueKey(updateAndPageColumn);
		incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
		incrementalSync.getAlarmColumn().add("JYBH");
		incrementalSync.getAlarmColumn().add("JYMC");
		singleStepSyncConfig.setIncrementalSync(incrementalSync);
		
		baseCore.syncEntry(singleStepSyncConfig);
	}

}
