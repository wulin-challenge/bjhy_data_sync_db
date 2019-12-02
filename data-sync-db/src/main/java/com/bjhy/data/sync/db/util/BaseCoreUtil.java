package com.bjhy.data.sync.db.util;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.domain.SyncStepLogInfoEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.AllThreadAfterRunEndListener;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepAfterListener;
import com.bjhy.data.sync.db.validation.SyncStepValidationRepair;
import com.bjhy.data.sync.db.validation.SyncStepValidationStore;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

/**
 * 核心同步代码工具类
 * @author wulin
 *
 */
public class BaseCoreUtil {
	
	/**
	 * 当前同步步骤Id
	 */
	private final static AtomicLong CURRENT_ID = new AtomicLong();
	
	/**
	 * 检查数据是否存在,存在就返回true,否则返回false
	 * @param syncLogicEntity
	 * @param checkSql
	 * @param params
	 * @return
	 */
	public static Boolean isExis(SyncLogicEntity syncLogicEntity,String checkSql,Map<String,Object> params){
		NamedParameterJdbcTemplate namedToTemplate = syncLogicEntity.getNamedToTemplate();
		Integer queryForInt = namedToTemplate.queryForObject(checkSql, params,Integer.class);
		if(queryForInt>0){
			return true;
		}
		return false;
	}
	
	/**
	 * 结束步骤同步
	 * @param syncLogicEntity
	 */
	public static void endStepSync(SyncLogicEntity syncLogicEntity) {
		stepSyncEndEvent(syncLogicEntity);//步骤同步结束事件
		
		//是否自动修复
		Boolean isAutoRepair = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getBaseRunEntity().getIsAutoRepair();
		if(isAutoRepair){
			//编辑修复同步标记
			SyncStepValidationStore.getInstance().editIsRepirSyncFlag(syncLogicEntity.getSingleStepSyncConfig());
		}
		//单个步骤执行后监听
		singleStepAfterListener(syncLogicEntity);
		//打印同步步骤日志信息
		printSyncStepLogInfo(syncLogicEntity);
	}
	
	/**
	 * 执行 所有线程运行后结束监听
	 * @param syncLogicEntity
	 */
	public static void endListener(SyncLogicEntity syncLogicEntity) {
		AllThreadAfterRunEndListener endListener = syncLogicEntity.getEndListener();
		if(endListener != null) {
			endListener.allThreadAfterRun();
		}
	}
	
	/**
	 * 步骤同步结束事件
	 * @param syncLogicEntity
	 */
	private static void stepSyncEndEvent(SyncLogicEntity syncLogicEntity){
		Boolean isAddVersionCheckFilter = syncLogicEntity.getSingleStepSyncConfig().getIsAddVersionCheckFilter();
		if(isAddVersionCheckFilter){
			VersionCheckCore versionCheckCore = syncLogicEntity.getVersionCheckCore();
			versionCheckCore.clearBeforeVertionData();//清楚上个版本或者检测类为null的数据
		}
	}
	
	/**
	 * 单个步骤执行后监听
	 * @param syncLogicEntity
	 */
	private static void singleStepAfterListener(SyncLogicEntity syncLogicEntity){
		String singleStepAfterListener = syncLogicEntity.getSingleStepSyncConfig().getSingleStepAfterListener();
		if(StringUtils.isNotBlank(singleStepAfterListener)){
			SingleStepAfterListener stepListener = getStepListener(syncLogicEntity, singleStepAfterListener, SingleStepAfterListener.class);
			if(stepListener != null){
				stepListener.stepAfterCall(syncLogicEntity);
				return;
			}
		}
	}
	
	/**
	 * 打印同步步骤日志信息
	 * @param syncLogicEntity 同步逻辑实体
	 */
	private static void printSyncStepLogInfo(final SyncLogicEntity syncLogicEntity){
		String dataSourceName = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		
		SyncStepLogInfoEntity syncStepLogInfoEntity = syncLogicEntity.getSyncStepLogInfoEntity();
		syncStepLogInfoEntity.setSyncStepEndTime(System.currentTimeMillis());
		
		SingleStepSyncConfig singleStepSyncConfig = syncLogicEntity.getSingleStepSyncConfig();
		
		StringBuilder failInfo = new StringBuilder("[同步结束] 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber);
		
		int insertCount = syncStepLogInfoEntity.getInsertCount().get();
		int updateCount = syncStepLogInfoEntity.getUpdateCount().get();
		syncStepLogInfoEntity.getUpdateCount().incrementAndGet();
		int deleteCount = syncStepLogInfoEntity.getDeleteCount().get();
		int noUpdateCount = syncStepLogInfoEntity.getNoUpdateCount().get();
		int failCount = syncStepLogInfoEntity.getFailCount().get();
		
		Long startTime = syncStepLogInfoEntity.getSyncStepStartTime();
		Long endTime = syncStepLogInfoEntity.getSyncStepEndTime();
		
		failInfo.append("\n\t "+(insertCount>0?"<定位标记>":"")+"insertCount:"+insertCount);
		failInfo.append("\n\t "+(updateCount>0?"<定位标记>":"")+"updateCount:"+updateCount);
		failInfo.append("\n\t "+(deleteCount>0?"<定位标记>":"")+"deleteCount:"+deleteCount);
		failInfo.append("\n\t "+(noUpdateCount>0?"<定位标记>":"")+"noUpdateCount:"+noUpdateCount);
		failInfo.append("\n\t "+(failCount>0?"<定位标记>":"")+"failCount:"+failCount);
		failInfo.append("\n\t "+"startStepSyncType[同步步骤执行方式]:"+singleStepSyncConfig.getStartStepSyncType());
		failInfo.append("\n\t 该步骤的同步时间:"+((endTime-startTime)/1000)+" 秒钟   <==> "+((endTime-startTime)/60000)+" 分钟");
		
		ConcurrentHashSet<String> failMessageSet = syncStepLogInfoEntity.getFailInfo();
		for (String failMessage : failMessageSet) {
			failInfo.append("\n\t <定位标记>失败具体信息:"+failMessage);
		}
		
		//同步成功与否的判断
		if(failCount == 0 && failMessageSet.size() == 0){
			LoggerUtils.info(failInfo.toString());
			// 删除 [最大修复次数] 列表中的步骤
			SyncStepValidationRepair.getInstance().removeStepMaxRepairNumber(singleStepSyncConfig);
		}else{
			LoggerUtils.error(failInfo.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getStepListener(SyncLogicEntity syncLogicEntity,String classString,Class<T> clazz){
		T newInstance = null;
		try {
			Class<T> forName = (Class<T>) Class.forName(classString);
			newInstance = forName.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			String dataSourceName = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
			String dataSourceNumber = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
			String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
			LoggerUtils.error("执行监听异常,监听名称:"+classString+" 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber+",异常信息:"+e.getLocalizedMessage());
		}
		return newInstance;
	}
	
	/**
	 * 得到同步步骤Id
	 * @return
	 */
	public static long getSyncStepId() {
		return CURRENT_ID.getAndIncrement();
	}

}
