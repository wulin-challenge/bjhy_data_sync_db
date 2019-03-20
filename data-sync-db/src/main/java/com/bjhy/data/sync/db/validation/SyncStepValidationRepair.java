package com.bjhy.data.sync.db.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.natived.dao.StepStoreDao;
import com.bjhy.data.sync.db.natived.dao.VersionCheckDao;
import com.bjhy.data.sync.db.natived.dao.impl.StepStoreDaoImpl;
import com.bjhy.data.sync.db.natived.dao.impl.VersionCheckDaoFactory;
import com.bjhy.data.sync.db.natived.domain.StepStoreEntity;
import com.bjhy.data.sync.db.natived.domain.VersionCheckEntity;
import com.bjhy.data.sync.db.util.DataSourceUtil;
import com.bjhy.data.sync.db.util.LoggerUtils;
import com.bjhy.data.sync.db.util.SyncPropertiesUtil;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

/**
 * 同步步骤校验修复
 * @author wubo
 *
 */
public class SyncStepValidationRepair {
	private Logger logger = Logger.getLogger(SyncStepValidationRepair.class);
	
	/**
	 * 版本检测常量
	 */
	public final static String VERSION_CHECK_PARAM = "versionCheckParam";
	
	
	private static SyncStepValidationRepair syncStepValidationRepair;
	
	private StepStoreDao stepStoreDao;
	
	private VersionCheckDao versionCheckDao;
	
	private SyncTemplate enableNativeSyncTemplate;
	
	private Thread thread;
	
	private SyncStepValidationRepair(){
		this.enableNativeSyncTemplate = DataSourceUtil.getInstance().getEnableNativeSyncTemplate();
		this.stepStoreDao = new StepStoreDaoImpl();
		this.versionCheckDao = VersionCheckDaoFactory.getVersionCheckDao();
	}
	
	/**
	 * 校验修复
	 */
	public void validationRepair(){
		startOneThread();//开启一个线程
		
	}
	
	/**
	 * 开启一个线程
	 */
	private void startOneThread(){
		thread = new Thread(){
			public void run() {
				logic();
			}
		};
		thread.start();
	}
	
	//逻辑
	private void logic(){
		try {
			Thread.sleep(1000*15);
			validationLogic();//校验的逻辑
			repairData();
			
		} catch (Exception e) {
			LoggerUtils.error("修复数据时出现了错误:错误信息 : "+e.getMessage());
		}finally{
			logic();
		}
	}
	
	/**
	 * 校验的逻辑
	 */
	private void validationLogic(){
		List<StepStoreEntity> findAll = stepStoreDao.findAll();
		for (StepStoreEntity stepStoreEntity : findAll) {
			
			String isRepairSync = stepStoreEntity.getIsRepairSync();
			
			//要修复的步骤,步骤配置都不能为null
			if(stepStoreEntity.getSingleStepByte() == null){
				continue;
			}
			
			Boolean isThisOnlyOneSync = stepStoreEntity.getSingleStepSyncConfig().getIsThisOnlyOneSync();
			
			//修复同步值为0,不能同步  && 只同步于一次的步骤只同步一次
			if("0".equals(isRepairSync) || isThisOnlyOneSync){
				continue;
			}
			
			StepStoreEntity findOneById = stepStoreDao.findOneById(stepStoreEntity);
			SingleStepSyncConfig singleStepSyncConfig = findOneById.getSingleStepSyncConfig();

			SyncTemplate fromTemplate = singleStepSyncConfig.getSingleRunEntity().getFromTemplate();
			
			//校验数据源是否可用
			if(fromTemplate == null || !DataSourceUtil.getInstance().isEnableSyncTemplate(fromTemplate)){
				continue;
			}
			
			SyncTemplate toTemplate = singleStepSyncConfig.getSingleRunEntity().getToTemplate();
			if(toTemplate == null || !DataSourceUtil.getInstance().isEnableSyncTemplate(toTemplate)){
				continue;
			}
			
			validationStepData(singleStepSyncConfig, findOneById);//校验步骤数据
		}
	}
	
	/**
	 * 校验步骤数据
	 * @param singleStepSyncConfig
	 */
	private void validationStepData(SingleStepSyncConfig singleStepSyncConfig,StepStoreEntity stepStoreEntity){
		NamedParameterJdbcTemplate fromTemplate = DataSourceUtil.getInstance().getNamedTemplate(singleStepSyncConfig.getSingleRunEntity().getFromTemplate());
		NamedParameterJdbcTemplate toTemplate = DataSourceUtil.getInstance().getNamedTemplate(singleStepSyncConfig.getSingleRunEntity().getToTemplate());
		
		//校验参数
		Map<String, Object> validationParams = singleStepSyncConfig.getAddStaticFromColumns();
		
		//校验的条件
		String toValidationWhere = singleStepSyncConfig.getToValidationWhere();
		if(StringUtils.isEmpty(toValidationWhere)){
			toValidationWhere = "";
		}
		
		//目标检测并删除脏数据
		boolean versionCheck = SyncPropertiesUtil.getProperty("sync.to.version.check.delete",false);
		if(versionCheck){
			toVersionCheckAndDelete(singleStepSyncConfig, toTemplate, validationParams);
		}
		
		String fromCountSql = singleStepSyncConfig.getFromCountSql();
		Integer fromDataNumber = fromTemplate.queryForObject(fromCountSql,validationParams,Integer.class);
		
		String toSql = "select count(1) num from "+singleStepSyncConfig.getToTableName()+" "+toValidationWhere;
		Integer toDataNumber = toTemplate.queryForObject(toSql,validationParams,Integer.class);
		
		stepStoreEntity.setFromDataNumber(fromDataNumber);
		stepStoreEntity.setToDataNumber(toDataNumber);
		
		stepStoreDao.updateByDataNumber(stepStoreEntity);
		
	}

	/**
	 * 目标检测并删除脏数据
	 * @param singleStepSyncConfig
	 * @param toTemplate
	 * @param validationParams
	 */
	private void toVersionCheckAndDelete(SingleStepSyncConfig singleStepSyncConfig,NamedParameterJdbcTemplate toTemplate, Map<String, Object> validationParams) {
		VersionCheckEntity versionCheckEntity = new VersionCheckEntity();
		String fromDataSourceNumber = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
		String ToDataSourceNumber = singleStepSyncConfig.getSingleRunEntity().getToTemplate().getConnectConfig().getDataSourceNumber();
		String fromTask = singleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().getFromTask();
		String toTask = singleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().getToTask();
		String stepUniquelyIdentifies = singleStepSyncConfig.getStepUniquelyIdentifies();
		
		versionCheckEntity.setFromDataSourceNumber(fromDataSourceNumber);
		versionCheckEntity.setToDataSourceNumber(ToDataSourceNumber);
		versionCheckEntity.setFromTask(fromTask);
		versionCheckEntity.setToTask(toTask);
		versionCheckEntity.setStepUniquelyIdentifies(stepUniquelyIdentifies);
		
		VersionCheckEntity reallyVersionCheckEntity = versionCheckDao.findOneByTaskAndTableName(versionCheckEntity);
		
		//得到目标检测版本参数
		List<String> toVersionCheckParams = getToVersionCheckParams(singleStepSyncConfig, reallyVersionCheckEntity);
		
		if(toVersionCheckParams.size()>0){
			//得到目标检测版本SQL
			String toVersionCheckSql = getToVersionCheckSql(singleStepSyncConfig, reallyVersionCheckEntity);
			
			Map<String,Object> VersionCheckParams = new HashMap<String,Object>();
			VersionCheckParams.put(VERSION_CHECK_PARAM, toVersionCheckParams);
			VersionCheckParams.putAll(validationParams);
			
			Integer hasDataNumber = toTemplate.queryForObject(toVersionCheckSql, VersionCheckParams, Integer.class);
			if(hasDataNumber>0){
				logger.warn("有 "+hasDataNumber+"条脏数据被清除!sql语句: "+toVersionCheckSql);
				String toVersionDeleteSql = getToVersionDeleteSql(singleStepSyncConfig, reallyVersionCheckEntity);
				toTemplate.update(toVersionDeleteSql, VersionCheckParams);
			}
		}
	}
	
	/**
	 * 得到目标检测版本SQL
	 * @param singleStepSyncConfig
	 * @param reallyVersionCheckEntity
	 * @return
	 */
	private String getToVersionCheckSql(SingleStepSyncConfig singleStepSyncConfig,VersionCheckEntity reallyVersionCheckEntity){
		//校验的条件
		String toValidationWhere = singleStepSyncConfig.getToValidationWhere();
		toValidationWhere = StringUtils.isBlank(toValidationWhere)?" where 1=1 ":toValidationWhere;
				
		StringBuilder toVersionCheckSql = new StringBuilder();
		toVersionCheckSql.append("select count(1) num from ");
		toVersionCheckSql.append(singleStepSyncConfig.getToTableName());
		toVersionCheckSql.append(" "+toValidationWhere+" AND ");
		toVersionCheckSql.append(VersionCheckCore.SYNC_VERSION_CHECK);
		toVersionCheckSql.append(" not in(:"+VERSION_CHECK_PARAM+") ");
		return toVersionCheckSql.toString();
	}
	
	/**
	 * 得到目标删除版本SQL
	 * @param singleStepSyncConfig
	 * @param reallyVersionCheckEntity
	 * @return
	 */
	private String getToVersionDeleteSql(SingleStepSyncConfig singleStepSyncConfig,VersionCheckEntity reallyVersionCheckEntity){
		//校验的条件
		String toValidationWhere = singleStepSyncConfig.getToValidationWhere();
		toValidationWhere = StringUtils.isBlank(toValidationWhere)?" where 1=1 ":toValidationWhere;
				
		StringBuilder toVersionCheckSql = new StringBuilder();
		toVersionCheckSql.append("delete from ");
		toVersionCheckSql.append(singleStepSyncConfig.getToTableName());
		toVersionCheckSql.append(" "+toValidationWhere+" AND ");
		toVersionCheckSql.append(VersionCheckCore.SYNC_VERSION_CHECK);
		toVersionCheckSql.append(" not in(:"+VERSION_CHECK_PARAM+") ");
		return toVersionCheckSql.toString();
	}
	
	/**
	 * 得到目标检测版本参数
	 * @param singleStepSyncConfig
	 * @param reallyVersionCheckEntity
	 * @return
	 */
	private List<String> getToVersionCheckParams(SingleStepSyncConfig singleStepSyncConfig,VersionCheckEntity reallyVersionCheckEntity){
		List<String> versionParams = new ArrayList<String>();
		
		String beforeCheckVersion = reallyVersionCheckEntity.getBeforeCheckVersion();
		if(StringUtils.isNotBlank(beforeCheckVersion)){
			versionParams.addAll(Arrays.asList(beforeCheckVersion.split(",")));
		}
		
		String currentCheckVersion = reallyVersionCheckEntity.getCurrentCheckVersion();
		if(StringUtils.isNotBlank(currentCheckVersion)){
			versionParams.add(currentCheckVersion);
		}
		return versionParams;
	}
	
	/**
	 * 修复数据
	 */
	private void repairData(){
		List<StepStoreEntity> findAll = stepStoreDao.findAll();
		for (StepStoreEntity stepStoreEntity : findAll) {
			StepStoreEntity findOneById = stepStoreDao.findOneById(stepStoreEntity);
			
			//目标数据与来源数据不等且同步标记为1
			if(findOneById.getFromDataNumber() != null 
					&& findOneById.getToDataNumber() != null 
					&& (int)findOneById.getFromDataNumber() != (int)findOneById.getToDataNumber() 
					&& "1".equals(findOneById.getIsRepairSync())){
				
				SingleStepSyncConfig singleStepSyncConfig = findOneById.getSingleStepSyncConfig();
				
				String dataSourceName = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
				String dataSourceNumber = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
				String toTableName = singleStepSyncConfig.getToTableName();
				LoggerUtils.info("[开始修复数据] 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber);
			
				BaseCore baseCore = new BaseCore();
				baseCore.syncEntry(singleStepSyncConfig);
				
				LoggerUtils.info("[结束修复数据] 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber);
			}
			
		}
	}
	
	//	
	public static SyncStepValidationRepair getInstance(){
		if(syncStepValidationRepair == null){
			syncStepValidationRepair = new SyncStepValidationRepair();
		}
		return syncStepValidationRepair;
	}

}
