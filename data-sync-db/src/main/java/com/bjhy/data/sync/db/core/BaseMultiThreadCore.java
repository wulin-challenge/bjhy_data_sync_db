package com.bjhy.data.sync.db.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.data.sync.db.domain.IncrementalSync;
import com.bjhy.data.sync.db.domain.IncrementalSync.AlarmColumnPrintLevel;
import com.bjhy.data.sync.db.domain.OneAndMultipleDataCompare;
import com.bjhy.data.sync.db.domain.RowCompareParam;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.domain.SyncPageRowEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunThread;
import com.bjhy.data.sync.db.inter.face.OwnInterface.MultiThreadPage;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepBeforeListener;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;
import com.bjhy.data.sync.db.multi.thread.page.DmMultiThreadPage;
import com.bjhy.data.sync.db.multi.thread.page.MySqlMultiThreadPage;
import com.bjhy.data.sync.db.multi.thread.page.NoMultiThreadPage;
import com.bjhy.data.sync.db.multi.thread.page.OracleMultiThreadPage;
import com.bjhy.data.sync.db.multi.thread.page.SqlServerMultiThreadPage;
import com.bjhy.data.sync.db.thread.ThreadControl;
import com.bjhy.data.sync.db.util.BaseCoreUtil;
import com.bjhy.data.sync.db.util.BeanUtils;
import com.bjhy.data.sync.db.util.CollectionUtil;
import com.bjhy.data.sync.db.util.LoggerUtils;
import com.bjhy.data.sync.db.util.MapUtil;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

/**
 * 线程安全的逻辑处理类,该类绝对不能出现任何操作成员变量的代码,任何变量操作都必须是方法内部的局部变量传递
 * @author wubo
 *
 */
public class BaseMultiThreadCore {
	
	/**
	 * 去重警告日志日志打印
	 */
	private static Set<String> alarmLogCache = new HashSet<String>();
	
	/**
	 * 利用 ThreadControl 的事件机制进行控制同步事件的触发,同时控制是否开启多线程
	 * @param syncLogicEntity
	 */
	public void  threadSyncEvent(final SyncLogicEntity syncLogicEntity){
		//得到多线程分页的 MultiThreadPage 对象
		final MultiThreadPage multiThreadPage = getMultiThreadPage(syncLogicEntity);
		
		ThreadControl threadControl = new ThreadControl(multiThreadPage.stepMaxThreadNumber());
		threadControl.forRunStart(multiThreadPage.pageIterations(), new ForRunThread(){

			@Override
			public void allThreadBeforeRun(int iterations) {
				String dataSourceName = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
				String dataSourceNumber = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
				String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
				LoggerUtils.info("[开始同步..] 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber);
				//单个步骤执行执行前监听
				singleStepBeforeListener(syncLogicEntity);
			}

			@Override
			public void currentThreadRunning(int iterations, int i) {
				List<Map<String, Object>> pageFromData = multiThreadPage.pageData(i);
				SyncLogicEntity newSyncLogicEntity = copyNewSyncLogicEntity(syncLogicEntity);
				
				SingleStepListener singleStepListener = syncLogicEntity.getSingleStepSyncConfig().getSingleStepListener();
				
				//page回调
				if(singleStepListener != null){
					singleStepListener.pageCall(syncLogicEntity, pageFromData);
				}
				pageSyncLogic(newSyncLogicEntity,pageFromData);//分页同步逻辑
			}

			@Override
			public void allThreadAfterRun(int iterations) {
				BaseAsynchronousBatchCommitCode.getInstance().addEndTask(syncLogicEntity);
			}
		});
	}
	
	/**
	 * 单个步骤执行执行前监听
	 * @param syncLogicEntity
	 */
	private void singleStepBeforeListener(SyncLogicEntity syncLogicEntity){
		String singleStepBeforeListener = syncLogicEntity.getSingleStepSyncConfig().getSingleStepBeforeListener();
		if(StringUtils.isNotBlank(singleStepBeforeListener)){
			SingleStepBeforeListener stepListener = BaseCoreUtil.getStepListener(syncLogicEntity, singleStepBeforeListener, SingleStepBeforeListener.class);
			if(stepListener != null){
				stepListener.stepBeforeCall(syncLogicEntity);
			}
		}
	}
	
	/**
	 * 得到多线程分页的 MultiThreadPage 对象
	 * @param syncLogicEntity
	 * @return
	 */
	private MultiThreadPage getMultiThreadPage(SyncLogicEntity syncLogicEntity){
		MultiThreadPage multiThreadPage = null;
		
		Boolean isMultiThreadPage = syncLogicEntity.getSingleStepSyncConfig().getIsMultiThreadPage();
		String databaseType = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDatabaseType();
		if(isMultiThreadPage){
			if("Oracle".equalsIgnoreCase(databaseType)){
				multiThreadPage = new OracleMultiThreadPage(syncLogicEntity);
				
			}else if("SQlServer".equalsIgnoreCase(databaseType)){
				multiThreadPage = new SqlServerMultiThreadPage(syncLogicEntity);
				
			}else if("MySql".equalsIgnoreCase(databaseType)){
				multiThreadPage = new MySqlMultiThreadPage(syncLogicEntity);
				
			}else if("DM".equalsIgnoreCase(databaseType)){
				multiThreadPage = new DmMultiThreadPage(syncLogicEntity);
				
			}
			
		}else{
			multiThreadPage = new NoMultiThreadPage(syncLogicEntity);
		}
		
		return multiThreadPage;
	}
	
	/**
	 * 分页同步逻辑(也采用多线程的方式以提高插入或者更新的效率)
	 */
	public void pageSyncLogic(final SyncLogicEntity syncLogicEntity,final List<Map<String, Object>> pageFromData){
		//增量同步hash目标数据源
		incrementalSyncHashToData(syncLogicEntity, pageFromData);
		for (Map<String, Object> map : pageFromData) {
			SyncLogicEntity newSyncLogicEntity = copyNewSyncLogicEntity(syncLogicEntity);
			Map<String, Object> rowParam = rowParamToUpperCase(newSyncLogicEntity, map);
			
			//判断是否有改变列数量的操作
			threadSafetyLogic(newSyncLogicEntity, rowParam);
		}
	}
	
	/**
	 * 增量同步hash目标数据源
	 * @param syncLogicEntity 同步逻辑实体
	 * @param pageFromData 分页的来源数据
	 */
	private void incrementalSyncHashToData(final SyncLogicEntity syncLogicEntity,final List<Map<String, Object>> pageFromData){
		IncrementalSync incrementalSync = syncLogicEntity.getSingleStepSyncConfig().getIncrementalSync();
		if(incrementalSync != null && pageFromData != null){
			// 增量同步查询目标源数据
			List<Map<String, Object>> incrementalSyncQueryToData = incrementalSyncQueryToData(syncLogicEntity, pageFromData);
			
			MapUtil mapUtil = new MapUtil();
			Map<Object, Map<String, Object>> hashMoreRowSet = mapUtil.hashMoreRowSet(incrementalSync.getToIdColumn(), incrementalSyncQueryToData);
			if(hashMoreRowSet != null && hashMoreRowSet.size()>0){
				syncLogicEntity.getIncrementalSyncDataHash().clear();
				syncLogicEntity.getIncrementalSyncDataHash().putAll(hashMoreRowSet);
			}
		}
	}
	
	/**
	 * 增量同步查询目标源数据
	 * @param syncLogicEntity 同步逻辑实体
	 * @param pageFromData 分页的来源数据
	 * @return
	 */
	private List<Map<String, Object>> incrementalSyncQueryToData(final SyncLogicEntity syncLogicEntity,final List<Map<String, Object>> pageFromData){
		//目标源数据
		List<Map<String, Object>> toData = new ArrayList<Map<String, Object>>();
		IncrementalSync incrementalSync = syncLogicEntity.getSingleStepSyncConfig().getIncrementalSync();
		
		//构建查询语句
		String toIdColumn = incrementalSync.getToIdColumn();
		String fromIdColumn = incrementalSync.getFromIdColumn();
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		String querySql = "SELECT * FROM "+toTableName+" WHERE "+toIdColumn+" in (:"+toIdColumn+")";
		
		//增量同步参数
		Map<String,Set<Object>> incrementalSyncParam = getIncrementalSyncParam(fromIdColumn,toIdColumn, pageFromData);
		
		//查询完整的目标数据
		List<Map<String, Object>> rowDataList = syncLogicEntity.getNamedToTemplate().queryForList(querySql, incrementalSyncParam);
		if(rowDataList != null && rowDataList.size()>0){
			for (Map<String, Object> rowData : rowDataList) {
				toData.add(rowParamToUpperCase(syncLogicEntity, rowData));
			}
			//批量更新同步检测版本号
			batchUpdateSyncCheckVersion(syncLogicEntity, incrementalSyncParam,toIdColumn);
		}
		return toData;
	}
	
	/**
	 * 批量更新同步检测版本号
	 * @param syncLogicEntity
	 * @param incrementalSyncParam 增量同步参数
	 * @param uniqueValueKey 唯一值key
	 */
	private void batchUpdateSyncCheckVersion(final SyncLogicEntity syncLogicEntity,Map<String,Set<Object>> incrementalSyncParam,String toIdColumn){
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		String updateCheckVersionSql = "UPDATE "+toTableName+" SET "+VersionCheckCore.SYNC_VERSION_CHECK+"=:"+VersionCheckCore.SYNC_VERSION_CHECK+" WHERE "+toIdColumn+" in (:"+toIdColumn+")";
		//更新检测版本参数
		Map<String,Object> updateCheckVersionParam = new HashMap<String,Object>(incrementalSyncParam);
		updateCheckVersionParam.put(VersionCheckCore.SYNC_VERSION_CHECK, syncLogicEntity.getCheckVersion());
		syncLogicEntity.getNamedToTemplate().update(updateCheckVersionSql, updateCheckVersionParam);
	}
	
	/**
	 * 增量同步参数
	 * @param fromIdColumn 来源Id列
	 * @param toIdColumn 目标Id列
	 * @param pageFromData 分页的来源数据
	 * @return
	 */
	private Map<String,Set<Object>> getIncrementalSyncParam(String fromIdColumn,String toIdColumn,final List<Map<String, Object>> pageFromData){
		Map<String,Set<Object>> result = new HashMap<String,Set<Object>>();
		Set<Object> params = new HashSet<Object>();
		for (Map<String, Object> fromData : pageFromData) {
			params.add(fromData.get(fromIdColumn));
		}
		result.put(toIdColumn, params);
		return result;
	}
	
	/**
	 * 拷贝新的  SyncLogicEntity,保证多线程操作时是线程安全的
	 * @param syncLogicEntity
	 * @return
	 */
	private SyncLogicEntity copyNewSyncLogicEntity(SyncLogicEntity syncLogicEntity){
			
		SyncLogicEntity newSyncLogicEntity = new SyncLogicEntity();
		newSyncLogicEntity.setSyncStepId(syncLogicEntity.getSyncStepId());
		newSyncLogicEntity.setCheckSql(syncLogicEntity.getCheckSql());
		newSyncLogicEntity.setCheckVersion(syncLogicEntity.getCheckVersion());
		newSyncLogicEntity.setDeleteSql(syncLogicEntity.getDeleteSql());
		newSyncLogicEntity.setInsertSql(syncLogicEntity.getInsertSql());
		newSyncLogicEntity.setSyncColumns(new LinkedHashSet<String>(syncLogicEntity.getSyncColumns()));
		newSyncLogicEntity.setUpdateSql(syncLogicEntity.getUpdateSql());
		newSyncLogicEntity.setToColumns(syncLogicEntity.getToColumns());
		newSyncLogicEntity.getIncrementalSyncDataHash().putAll(syncLogicEntity.getIncrementalSyncDataHash());
		
		newSyncLogicEntity.setVersionCheckCore(syncLogicEntity.getVersionCheckCore());
		
		//将最原始的 同步步骤日志信息实例(SyncStepLogInfoEntity) 赋予给  新的同步逻辑实例,
		//保证  同步步骤日志信息实例(SyncStepLogInfoEntity) 在一个步骤中始终只有一个实例
		newSyncLogicEntity.setSyncStepLogInfoEntity(syncLogicEntity.getSyncStepLogInfoEntity());
		
		//复制单个步骤的数据
		SingleStepSyncConfig singleStepSyncConfig = syncLogicEntity.getSingleStepSyncConfig();
		SingleStepSyncConfig newSingleStepSyncConfig = new SingleStepSyncConfig();
		BeanUtils.copyNotNullProperties(singleStepSyncConfig, newSingleStepSyncConfig);
		newSingleStepSyncConfig.getSimpleColumnNameMapping().putAll(singleStepSyncConfig.getSimpleColumnNameMapping());
		newSyncLogicEntity.setSingleStepSyncConfig(newSingleStepSyncConfig);
		
		//复制行分页对象
		SyncPageRowEntity syncPageRowEntity = syncLogicEntity.getSingleStepSyncConfig().getSyncPageRowEntity();
		if(syncPageRowEntity != null){
			SyncPageRowEntity newSyncPageRowEntity = new SyncPageRowEntity();
			newSyncPageRowEntity.setPageRowColumns(syncPageRowEntity.getPageRowColumns());
			newSyncPageRowEntity.setPageRowInsertColumns(new ArrayList<String>(syncPageRowEntity.getPageRowInsertColumns()));
			newSyncPageRowEntity.setPageRowInsertColumnSql(syncPageRowEntity.getPageRowInsertColumnSql());
			newSyncPageRowEntity.setPageRowUpdateColumnSqlList(new ArrayList<String>(syncPageRowEntity.getPageRowUpdateColumnSqlList()));
			newSyncLogicEntity.getSingleStepSyncConfig().setSyncPageRowEntity(newSyncPageRowEntity);
		}
		return newSyncLogicEntity;
	}
	
	/**
	 * 线程安全逻辑
	 * @param syncLogicEntity
	 * @param rowParam
	 */
	private void threadSafetyLogic(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		syncLogicEntity = copyNewSyncLogicEntity(syncLogicEntity);
		///分页中每一行的逻辑处理的方法
		pageRowLogicDealWith(syncLogicEntity, rowParam);
		
		//增量同步hash对比
		if(incrementalSyncHashCompare(syncLogicEntity, rowParam)){
			//增量同步强制更新字段
			incrementalSyncForceUpdateColumn(syncLogicEntity, rowParam);
			
			//一个步骤中进行了多少次成功的no update操作,(即来源数据与目标数据是一致的,没有进行任何的insert或者update语句操作)
			//将 noUpdateCount 加1
			syncLogicEntity.getSyncStepLogInfoEntity().getNoUpdateCount().incrementAndGet();
			return;
		}
		
		BaseAsynchronousBatchCommitCode.getInstance().batchInsertOrUpdate(syncLogicEntity, rowParam);
	}
	
	/**
	 * 增量同步强制更新字段
	 * @param syncLogicEntity
	 * @param rowParam 正在同步的行数据
	 */
	private void incrementalSyncForceUpdateColumn(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		IncrementalSync incrementalSync = syncLogicEntity.getSingleStepSyncConfig().getIncrementalSync();
		
		if(incrementalSync == null || incrementalSync.getForceUpdateColumn().size()==0){
			return;
		}
		
		String forceUpdateSql = getIncrementalSyncForceUpdateSql(syncLogicEntity, incrementalSync);
		syncLogicEntity.getNamedToTemplate().update(forceUpdateSql, rowParam);
	}
	
	/**
	 * 得到增量同步强制更新sql
	 * @param syncLogicEntity
	 * @param incrementalSync 增量同步实例
	 * @return
	 */
	private String getIncrementalSyncForceUpdateSql(SyncLogicEntity syncLogicEntity,IncrementalSync incrementalSync){
		List<String> forceUpdateColumn = incrementalSync.getForceUpdateColumn();
		String toIdColumn = incrementalSync.getToIdColumn();
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		
		StringBuilder forceUpdateSql = new StringBuilder("UPDATE "+toTableName+" SET ");
		
		int i=0;
		for (String updateColumn : forceUpdateColumn) {
			updateColumn = updateColumn.trim().toUpperCase();
			if(i == 0){
				forceUpdateSql.append(updateColumn+"=:"+updateColumn);
			}else{
				forceUpdateSql.append(","+updateColumn+"=:"+updateColumn);
			}
			i++;
		}
		forceUpdateSql.append(" WHERE "+toIdColumn+"=:"+toIdColumn);
		return forceUpdateSql.toString();
	}
	
	/**
	 * 增量同步hash对比
	 * @param syncLogicEntity
	 * @param rowParam
	 * @return 对比成功返回true,否则返回false
	 */
	private boolean incrementalSyncHashCompare(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		
		IncrementalSync incrementalSync = syncLogicEntity.getSingleStepSyncConfig().getIncrementalSync();
		Map<Object, Map<String, Object>> incrementalSyncDataHash = syncLogicEntity.getIncrementalSyncDataHash();
		
		if(incrementalSync == null || incrementalSyncDataHash.size()==0){
			return false;
		}
		//增量同步检测警告字段
		incrementalSyncCheckAlarmColumn(syncLogicEntity, rowParam, incrementalSyncDataHash);
		//得到最终的指定的同步比较列
		Collection<String> specifyCompareColumn = getSpecifyCompareColumnFinally(syncLogicEntity, incrementalSync);
		
		OneAndMultipleDataCompare oneAndMultipleDataCompare = new OneAndMultipleDataCompare();
		oneAndMultipleDataCompare.getExcludeColumn().addAll(CollectionUtil.collectionToUpperCase(incrementalSync.getExcludeColumn()));
		oneAndMultipleDataCompare.getSpecifyCompareColumn().addAll(specifyCompareColumn);
		oneAndMultipleDataCompare.setValueCompare(incrementalSync.getValueCompare());
		oneAndMultipleDataCompare.setUniqueValueKey(incrementalSync.getFromIdColumn());
		oneAndMultipleDataCompare.getMoreRowHash().putAll(incrementalSyncDataHash);
		oneAndMultipleDataCompare.getLessRow().putAll(rowParam);
		oneAndMultipleDataCompare.getLessRowTypeConvert().putAll(incrementalSync.getSpecifyColumnValueTypeConvert());
		
		MapUtil mapUtil = new MapUtil();
		Map<String, Object> oneAndMultipleDataCompare2 = mapUtil.oneAndMultipleDataCompare(oneAndMultipleDataCompare);
		if(oneAndMultipleDataCompare2.size()>0){
			return false;
		}
		return true;
	}

	/**
	 * 得到最终的指定的同步比较列
	 * @param syncLogicEntity
	 * @param incrementalSync 增量同步配置
	 * @return
	 */
	private Collection<String> getSpecifyCompareColumnFinally(SyncLogicEntity syncLogicEntity,IncrementalSync incrementalSync) {
		//最终的同步自定列
		List<String> specifyCompareColumnFinally = new ArrayList<String>();
		//同步比较列
		Collection<String> specifyCompareColumn = CollectionUtil.collectionToUpperCase(incrementalSync.getSpecifyCompareColumn());
		
		switch (incrementalSync.getIncrementalSyncMode()) {
		case INTERSECTION_COLUMN:
			Set<String> syncColumnList = new HashSet<String>(syncLogicEntity.getSyncColumns());
			syncColumnList.addAll(specifyCompareColumn);
			specifyCompareColumnFinally.addAll(syncColumnList);
			break;
		case TO_CONTAIN_FROM:
			specifyCompareColumnFinally.addAll(specifyCompareColumn);
			break;
		default:
			break;
		}
		return specifyCompareColumnFinally;
	}
	
	/**
	 * 增量同步检测经过字段
	 * @param syncLogicEntity 同步逻辑实体
	 * @param rowParam 当前同步行参数
	 * @param incrementalSyncDataHash 增量同步数据的hash
	 */
	private void incrementalSyncCheckAlarmColumn(SyncLogicEntity syncLogicEntity,Map<String, Object> fromRowParam,Map<Object, Map<String, Object>> incrementalSyncDataHash){
		IncrementalSync incrementalSync = syncLogicEntity.getSingleStepSyncConfig().getIncrementalSync();
		
		if(incrementalSync == null || incrementalSync.getAlarmColumn().size()==0 ||
		   incrementalSyncDataHash == null || incrementalSyncDataHash.size()==0  ||
				   fromRowParam == null || fromRowParam.size()==0){
			return;
		}
		String fromIdColumn = incrementalSync.getFromIdColumn();
		Object uniqueValue = fromRowParam.get(fromIdColumn);
		
		if(uniqueValue == null){
			return;
		}
		Map<String, Object> toRowParam = incrementalSyncDataHash.get(uniqueValue);
		if(toRowParam == null || toRowParam.size() ==0){
			return;
		}
		RowCompareParam rowCompareParam = new RowCompareParam();
		rowCompareParam.getLessRow().putAll(fromRowParam);
		rowCompareParam.getMoreRow().putAll(toRowParam);
		rowCompareParam.getSpecifyCompareColumn().addAll(CollectionUtil.collectionToUpperCase(incrementalSync.getAlarmColumn()));
		MapUtil mapUtil = new MapUtil();
		boolean compare = mapUtil.compare(rowCompareParam);
		if(!compare){
			//得到警告详细信息
			String alarmColumnMessage = getAlarmColumnMessage(syncLogicEntity, rowCompareParam, incrementalSync);
			
			//得到警告简单信息
			String alarmColumnSimpleMessage = getAlarmColumnSimpleMessage(syncLogicEntity, rowCompareParam, incrementalSync);
			
			AlarmColumnPrintLevel alarmColumnPrintLevel = incrementalSync.getAlarmColumnPrintLevel();
			if(AlarmColumnPrintLevel.LOGGING == alarmColumnPrintLevel){
				
				Boolean loggingPrint = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getBaseRunEntity().getSyncAlarmColumnLoggingPrint();
				if(!loggingPrint && !alarmLogCache.contains(alarmColumnSimpleMessage)){
					alarmLogCache.add(alarmColumnSimpleMessage);
					LoggerUtils.warn(alarmColumnMessage);
				}
				
				if(loggingPrint){
					LoggerUtils.warn(alarmColumnMessage);
				}
			}else if(AlarmColumnPrintLevel.EXCEPTION == alarmColumnPrintLevel){
				IllegalStateException ex = new IllegalStateException(alarmColumnMessage);
				LoggerUtils.error(ex);
				throw ex;
			}
		}
	}
	
	/**
	 * 得到警告简单信息
	 * @param syncLogicEntity 同步逻辑实体
	 * @param rowCompareParam 行比较参数
	 * @param incrementalSync 增量同步实体
	 * @return 得到警告信息
	 */
	private String getAlarmColumnSimpleMessage(SyncLogicEntity syncLogicEntity,RowCompareParam rowCompareParam,IncrementalSync incrementalSync){
		String dataSourceName = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		
		StringBuilder alarmString = new StringBuilder("[警告字段] 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber+",详细信息:[");
		List<String> alarmColumn = incrementalSync.getAlarmColumn();
		for (String column : alarmColumn) {
			alarmString.append("(字段:"+column+")");
		}
		alarmString.append("]");
		return alarmString.toString();
	}

	/**
	 * 得到警告详细信息
	 * @param syncLogicEntity 同步逻辑实体
	 * @param rowCompareParam 行比较参数
	 * @param incrementalSync 增量同步实体
	 * @return 得到警告信息
	 */
	private String getAlarmColumnMessage(SyncLogicEntity syncLogicEntity,RowCompareParam rowCompareParam,IncrementalSync incrementalSync){
		String dataSourceName = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		
		Map<String, Object> fromRowData = rowCompareParam.getLessRow();
		Map<String, Object> toRowData = rowCompareParam.getMoreRow();
		
		//得到唯一key对应的值
		String fromIdColumn = incrementalSync.getFromIdColumn();
		String toIdColumn = incrementalSync.getToIdColumn();
		Object fromUniqueValue = fromRowData == null?"":fromRowData.get(fromIdColumn);
		Object toUniqueValue = toRowData == null?"":toRowData.get(toIdColumn);
		
		StringBuilder alarmString = new StringBuilder("[警告字段] 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber+",(唯一key: fromIdColumn|toIdColumn "+fromUniqueValue+",来源唯一key值:"+fromUniqueValue+",原本唯一key值:"+toUniqueValue+"),详细信息:[");
		List<String> alarmColumn = incrementalSync.getAlarmColumn();
		int i=0;
		for (String column : alarmColumn) {
			Object fromValue = fromRowData == null?"":fromRowData.get(column);
			Object toValue = toRowData == null?"":toRowData.get(column);
			if(i==0){
				alarmString.append("(字段:"+column+",来源值:"+fromValue+",原本值:"+toValue+")");
			}else{
				alarmString.append(",(字段:"+column+",来源值:"+fromValue+",原本值:"+toValue+")");
			}
			i++;
		}
		alarmString.append("]");
		return alarmString.toString();
	}

	/**
	 * 分页中每一行的逻辑处理的方法
	 * @param syncLogicEntity
	 * @param rowParam 行参数
	 */
	private void pageRowLogicDealWith(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		SingleStepListener singleStepListener = syncLogicEntity.getSingleStepSyncConfig().getSingleStepListener();
		
		//处理简单字段映射
		dealWithSimpleColumnMapping(syncLogicEntity, rowParam);
		
		//行回调
		if(singleStepListener != null){
			pageDealWithRowCall(syncLogicEntity, rowParam);
		}
		
		//是否同步空值
		Boolean isSyncNullValue = syncLogicEntity.getSingleStepSyncConfig().getIsSyncNullValue();
		if(!isSyncNullValue){
			filterNullValue(syncLogicEntity, rowParam);
		}
		
		//刷新  构建inserSql和updateSql和checkSql和DeleteSql语句(即dll)
		refreshBuildDllSql(syncLogicEntity, rowParam);
	}
	
	
	
	private void filterNullValue(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		String databaseType = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getToTemplate().getConnectConfig().getDatabaseType();
		
		Map<String,Object> notNullRowParams = new LinkedHashMap<String,Object>();
		Set<Entry<String, Object>> entrySet = rowParam.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			//sqlServer判断为空的方式
			if(entry.getValue() != null){
				if("sqlServer".equalsIgnoreCase(databaseType)){
					if(entry.getValue() instanceof String){
						String strValue = (String) entry.getValue();
						if(StringUtils.isNotEmpty(strValue)){
							notNullRowParams.put(entry.getKey(), entry.getValue());
						}
					}else{
						notNullRowParams.put(entry.getKey(), entry.getValue());
					}
				}else{
					notNullRowParams.put(entry.getKey(), entry.getValue());
				}
			}
		}
		
		rowParam.clear();
		rowParam.putAll(notNullRowParams);
	}
	
	/**
	 * 刷新  构建inserSql和updateSql和checkSql和DeleteSql语句(即dll)
	 * @param syncLogicEntity
	 * @param rowParam
	 */
	private void refreshBuildDllSql(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		List<String> rowColumns = getRowColumns(syncLogicEntity, rowParam);
		BaseCore baseCore = new BaseCore();
		baseCore.refreshSyncColumns(syncLogicEntity, rowColumns,rowParam);
		baseCore.addStaticColumns(syncLogicEntity,rowParam);
		baseCore.removeFromColumns(syncLogicEntity);
		baseCore.buildDllSql(syncLogicEntity);
	}
	
	/**
	 * 分页处理行回调
	 * @param syncLogicEntity
	 * @param params
	 */
	private void pageDealWithRowCall(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		SingleStepListener singleStepListener = syncLogicEntity.getSingleStepSyncConfig().getSingleStepListener();
		Map<String, Object> rowCallParams = singleStepListener.rowCall(syncLogicEntity,rowParam);
		rowCallParams = rowParamToUpperCase(syncLogicEntity, rowCallParams);
		rowParam.putAll(rowCallParams);
	}
	
	/**
	 * 处理简单字段映射
	 * @param syncLogicEntity
	 * @param rowParam
	 */
	private void dealWithSimpleColumnMapping(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		Map<String, String> simpleColumnNameMapping = syncLogicEntity.getSingleStepSyncConfig().getSimpleColumnNameMapping();
		
		if(simpleColumnNameMapping != null){
			Set<Entry<String, String>> entrySet = simpleColumnNameMapping.entrySet();
			for (Entry<String, String> entry : entrySet) {
				String upperCaseKey = entry.getKey().toUpperCase();
				String upperCaseValue = entry.getValue().toUpperCase();
				if(rowParam.containsKey(upperCaseKey)){
					rowParam.put(upperCaseValue, rowParam.get(upperCaseKey));
				}
			}
		}
	}
	
	/**
	 * 将行参数转大写
	 * @param syncLogicEntity
	 * @param rowParam
	 * @return
	 */
	private Map<String,Object> rowParamToUpperCase(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		Map<String,Object> newRowParams = new LinkedHashMap<String,Object>();
		Set<Entry<String, Object>> entrySet = rowParam.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			newRowParams.put(entry.getKey().toUpperCase(), entry.getValue());
		}
		return newRowParams;
	}
	
	/**
	 * 通过行参数得到行列
	 * @param syncLogicEntity
	 * @param rowParam
	 * @return
	 */
	private List<String> getRowColumns(SyncLogicEntity syncLogicEntity,Map<String,Object> rowParam){
		List<String> rowColumns = new ArrayList<String>();
		Set<Entry<String, Object>> entrySet = rowParam.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			rowColumns.add(entry.getKey());
		}
		return rowColumns;
	}
}
