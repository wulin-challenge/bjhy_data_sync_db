package com.bjhy.db.converter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.domain.IncrementalSync;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncPageRowEntity;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

/**
 * 步骤工具类
 * @author wubo
 *
 */
public class StepUtil {

	/**
	 * 
	 * @param singleRunEntity 同步运行的配置信息
	 * @param uniqueKey 同步步骤唯一key
	 * @param fromSelectPart 来源表的 select 部分sql ，即 select * 部分
	 * @param fromFromPart 来源表 的from 后的部分 ，即 from 。。。
	 * @param toTableName 同步目标表的表名
	 * @param updateAndPageColumn 来源表的Id
	 * @param singleStepListenerName 监听全限定名称
	 * @param simpleColumnNameMapping 简单的字段映射
	 */
	public static void checkSyncNotNullWhere2(SingleRunEntity singleRunEntity,String uniqueKey,String fromSelectPart,String fromFromPart,String toTableName,String updateAndPageColumn,String singleStepListenerName,Map<String,String> simpleColumnNameMapping){
		   BaseCore baseCore = new BaseCore();
		    
		    String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		    String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		    
		    SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		    singleStepSyncConfig.setStepUniquelyIdentifies(uniqueKey);
		    singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		    singleStepSyncConfig.setFromFromPart(fromFromPart);
		    singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		    singleStepSyncConfig.setToTableName(toTableName);
		    singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		    singleStepSyncConfig.setIsAddVersionCheckFilter(Boolean.valueOf(true));
		    singleStepSyncConfig.setIsSyncNullValue(Boolean.valueOf(true));
		    singleStepSyncConfig.setIsMultiThreadPage(Boolean.valueOf(true));
		    singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		    singleStepSyncConfig.setSingleStepListenerName(singleStepListenerName);
		    
		    singleStepSyncConfig.getAddStaticFromColumns().put("sys_orgName", dataSourceName);
		    singleStepSyncConfig.getAddStaticFromColumns().put("sys_orgCode", dataSourceNumber);
		    singleStepSyncConfig.setToValidationWhere(" where sys_orgCode=:sys_orgCode ");
		    if ((simpleColumnNameMapping != null) && (!simpleColumnNameMapping.isEmpty())) {
		      singleStepSyncConfig.getSimpleColumnNameMapping().putAll(simpleColumnNameMapping);
		    }
		    SyncPageRowEntity syncPageRowEntity = new SyncPageRowEntity();
		    List<String> pageRowInsertColumns = new ArrayList<String>();
		    pageRowInsertColumns.add(updateAndPageColumn);
		    syncPageRowEntity.setPageRowInsertColumns(pageRowInsertColumns);
		    
		    //增量同步配置
			IncrementalSync incrementalSync = new IncrementalSync();
			incrementalSync.setUniqueValueKey(updateAndPageColumn);
			incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
			incrementalSync.getAlarmColumn().add("SYS_ORGCODE");
			incrementalSync.getAlarmColumn().add("SYS_ORGNAME");
			singleStepSyncConfig.setIncrementalSync(incrementalSync);
		    
		    baseCore.syncEntry(singleStepSyncConfig);
	}
	
	public static void checkSyncNotNullWhere(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String fromWhere,String updateAndPageColumn,String singleStepListenerName,Map<String,String> simpleColumnNameMapping){
	    BaseCore baseCore = new BaseCore();
	    
	    String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
	    String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
	    
	    String fromSelectPart = "select * ";
	    String fromFromPart = "from " + fromTableName + " " + fromWhere;
	    SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
	    singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName + "_" + toTableName);
	    singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
	    singleStepSyncConfig.setFromFromPart(fromFromPart);
	    singleStepSyncConfig.setFromSelectPart(fromSelectPart);
	    singleStepSyncConfig.setToTableName(toTableName);
	    singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
	    singleStepSyncConfig.setIsAddVersionCheckFilter(Boolean.valueOf(true));
	    singleStepSyncConfig.setIsSyncNullValue(Boolean.valueOf(true));
	    singleStepSyncConfig.setIsMultiThreadPage(Boolean.valueOf(true));
	    singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
	    singleStepSyncConfig.setSingleStepListenerName(singleStepListenerName);
	    
//	    singleStepSyncConfig.getAddStaticFromColumns().put("sys_orgName", dataSourceName);
//	    singleStepSyncConfig.getAddStaticFromColumns().put("sys_orgCode", dataSourceNumber);
//	    singleStepSyncConfig.setToValidationWhere(" where sys_orgCode=:sys_orgCode ");
	    if ((simpleColumnNameMapping != null) && (!simpleColumnNameMapping.isEmpty())) {
	      singleStepSyncConfig.getSimpleColumnNameMapping().putAll(simpleColumnNameMapping);
	    }
	    SyncPageRowEntity syncPageRowEntity = new SyncPageRowEntity();
	    List<String> pageRowInsertColumns = new ArrayList<String>();
	    pageRowInsertColumns.add(updateAndPageColumn);
	    syncPageRowEntity.setPageRowInsertColumns(pageRowInsertColumns);
	    
	    //增量同步配置
		IncrementalSync incrementalSync = new IncrementalSync();
		incrementalSync.setUniqueValueKey(updateAndPageColumn);
		incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
		incrementalSync.getAlarmColumn().add("SYS_ORGCODE");
		incrementalSync.getAlarmColumn().add("SYS_ORGNAME");
		singleStepSyncConfig.setIncrementalSync(incrementalSync);
	    
	    baseCore.syncEntry(singleStepSyncConfig);
	}
	
	public static void checkSyncNotNull(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn,String singleStepListenerName,Map<String,String> simpleColumnNameMapping){
		
		   BaseCore baseCore = new BaseCore();
		    
		    String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		    String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		    
		    String fromSelectPart = "select * ";
		    String fromFromPart = "from " + fromTableName;
		    SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		    singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName + "_" + toTableName);
		    singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		    singleStepSyncConfig.setFromFromPart(fromFromPart);
		    singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		    singleStepSyncConfig.setToTableName(toTableName);
		    singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		    singleStepSyncConfig.setIsAddVersionCheckFilter(Boolean.valueOf(true));
		    singleStepSyncConfig.setIsSyncNullValue(Boolean.valueOf(true));
		    singleStepSyncConfig.setIsMultiThreadPage(Boolean.valueOf(true));
		    singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		    singleStepSyncConfig.setSingleStepListenerName(singleStepListenerName);
		    
		    singleStepSyncConfig.getAddStaticFromColumns().put("sys_orgName", dataSourceName);
		    singleStepSyncConfig.getAddStaticFromColumns().put("sys_orgCode", dataSourceNumber);
		    singleStepSyncConfig.setToValidationWhere(" where sys_orgCode=:sys_orgCode ");
		    if ((simpleColumnNameMapping != null) && (!simpleColumnNameMapping.isEmpty())) {
		      singleStepSyncConfig.getSimpleColumnNameMapping().putAll(simpleColumnNameMapping);
		    }
		    SyncPageRowEntity syncPageRowEntity = new SyncPageRowEntity();
		    List<String> pageRowInsertColumns = new ArrayList<String>();
		    pageRowInsertColumns.add(updateAndPageColumn);
		    syncPageRowEntity.setPageRowInsertColumns(pageRowInsertColumns);
		    
		    //增量同步配置
			IncrementalSync incrementalSync = new IncrementalSync();
			incrementalSync.setUniqueValueKey(updateAndPageColumn);
			incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
			incrementalSync.getAlarmColumn().add("SYS_ORGCODE");
			incrementalSync.getAlarmColumn().add("SYS_ORGNAME");
			singleStepSyncConfig.setIncrementalSync(incrementalSync);
		    
		    baseCore.syncEntry(singleStepSyncConfig);
	}
	
	public static void checkOnlyOneSync(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn,String singleStepListenerName,Map<String,String> simpleColumnNameMapping){
            BaseCore baseCore = new BaseCore();
		    
		    String dataSourceName = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceName();
		    String dataSourceNumber = singleRunEntity.getFromTemplate().getConnectConfig().getDataSourceNumber();
		    
		    String fromSelectPart = "select * ";
		    String fromFromPart = "from " + fromTableName;
		    SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		    singleStepSyncConfig.setStepUniquelyIdentifies(fromTableName + "_" + toTableName);
		    singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		    singleStepSyncConfig.setFromFromPart(fromFromPart);
		    singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		    singleStepSyncConfig.setToTableName(toTableName);
		    singleStepSyncConfig.setUpdateColumn(updateAndPageColumn);
		    singleStepSyncConfig.setIsAddVersionCheckFilter(Boolean.valueOf(true));
		    singleStepSyncConfig.setIsSyncNullValue(Boolean.valueOf(true));
		    singleStepSyncConfig.setIsMultiThreadPage(Boolean.valueOf(true));
		    singleStepSyncConfig.setIsThisOnlyOneSync(Boolean.valueOf(true));
		    singleStepSyncConfig.setHighPerformancePageColumn(updateAndPageColumn);
		    singleStepSyncConfig.setSingleStepListenerName(singleStepListenerName);
		    
		    singleStepSyncConfig.getAddStaticFromColumns().put("sys_orgName", dataSourceName);
		    singleStepSyncConfig.getAddStaticFromColumns().put("sys_orgCode", dataSourceNumber);
		    singleStepSyncConfig.setToValidationWhere(" where sys_orgCode=:sys_orgCode ");
		    if ((simpleColumnNameMapping != null) && (!simpleColumnNameMapping.isEmpty())) {
		      singleStepSyncConfig.getSimpleColumnNameMapping().putAll(simpleColumnNameMapping);
		    }
		    SyncPageRowEntity syncPageRowEntity = new SyncPageRowEntity();
		    List<String> pageRowInsertColumns = new ArrayList<String>();
		    pageRowInsertColumns.add(updateAndPageColumn);
		    syncPageRowEntity.setPageRowInsertColumns(pageRowInsertColumns);
		    
		    //增量同步配置
			IncrementalSync incrementalSync = new IncrementalSync();
			incrementalSync.setUniqueValueKey(updateAndPageColumn);
			incrementalSync.getExcludeColumn().add(VersionCheckCore.SYNC_VERSION_CHECK);
			incrementalSync.getAlarmColumn().add("SYS_ORGCODE");
			incrementalSync.getAlarmColumn().add("SYS_ORGNAME");
			singleStepSyncConfig.setIncrementalSync(incrementalSync);
		    
		    baseCore.syncEntry(singleStepSyncConfig);
	}
}
