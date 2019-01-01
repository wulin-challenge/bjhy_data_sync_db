package com.bjhy.data.sync.db.test.compare;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.domain.AddColumnAttribute;
import com.bjhy.data.sync.db.domain.IncrementalSync;
import com.bjhy.data.sync.db.domain.IncrementalSync.AlarmColumnPrintLevel;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

public class CompareHelper {

	
	/**
	 * 该方法添加同步过程中的回调监听
	 * @param singleRunEntity
	 * @param fromTableName
	 * @param toTableName
	 * @param updateAndPageColumn
	 * @param singleStepListener
	 * @return
	 */
	public static void incrementalSync(SingleRunEntity singleRunEntity,String fromTableName,String toTableName,String updateAndPageColumn,SingleStepListener singleStepListener){
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
		//排除age,和create_date字段来测试强制更新字段
		incrementalSync.getExcludeColumn().add("age");
		incrementalSync.getExcludeColumn().add("create_date");
		//添加强制更新字段
		incrementalSync.getForceUpdateColumn().add("age");
		incrementalSync.getForceUpdateColumn().add("create_date");
		//添加警告字段
		incrementalSync.getAlarmColumn().add("jybh");
		incrementalSync.setAlarmColumnPrintLevel(AlarmColumnPrintLevel.EXCEPTION);
		singleStepSyncConfig.setIncrementalSync(incrementalSync);
		
		baseCore.syncEntry(singleStepSyncConfig);
	}
}
