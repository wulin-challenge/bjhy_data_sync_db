package com.bjhy.data.sync.db.validation;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.natived.dao.StepStoreDao;
import com.bjhy.data.sync.db.natived.dao.impl.StepStoreDaoImpl;
import com.bjhy.data.sync.db.natived.domain.StepStoreEntity;
import com.bjhy.data.sync.db.util.DataSourceUtil;
import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 同步步骤校验修复
 * @author wubo
 *
 */
public class SyncStepValidationRepair {
	
	private static SyncStepValidationRepair syncStepValidationRepair;
	
	private StepStoreDao stepStoreDao;
	
	private SyncTemplate enableNativeSyncTemplate;
	
	private Thread thread;
	
	private SyncStepValidationRepair(){
		enableNativeSyncTemplate = DataSourceUtil.getInstance().getEnableNativeSyncTemplate();
		stepStoreDao = new StepStoreDaoImpl();
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
				try {
					logic();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	//逻辑
	private void logic() throws InterruptedException{
		
//		Thread.sleep(1000*15);
		
		LoggerUtils.info("开始校验数据库");
		validationLogic();//校验的逻辑
		
		LoggerUtils.info("开始修复数据");
		repairData();
		
//		Thread.sleep(1000*60*5);
		Thread.sleep(1000*15);
		
		logic();
	}
	
	/**
	 * 校验的逻辑
	 */
	private void validationLogic(){
		List<StepStoreEntity> findAll = stepStoreDao.findAll();
		for (StepStoreEntity stepStoreEntity : findAll) {
			
			String isRepairSync = stepStoreEntity.getIsRepairSync();
			
			//修复同步值为0,不能同步
			if("0".equals(isRepairSync)){
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
	@SuppressWarnings("deprecation")
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
		
		String fromCountSql = singleStepSyncConfig.getFromCountSql();
		int fromDataNumber = fromTemplate.queryForInt(fromCountSql,validationParams);
		
		String toSql = "select count(1) num from "+singleStepSyncConfig.getToTableName()+" "+toValidationWhere;
		int toDataNumber = toTemplate.queryForInt(toSql,validationParams);
		
		stepStoreEntity.setFromDataNumber(fromDataNumber);
		stepStoreEntity.setToDataNumber(toDataNumber);
		
		stepStoreDao.updateByDataNumber(stepStoreEntity);
		
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
				BaseCore baseCore = new BaseCore();
				baseCore.syncEntry(singleStepSyncConfig);
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
