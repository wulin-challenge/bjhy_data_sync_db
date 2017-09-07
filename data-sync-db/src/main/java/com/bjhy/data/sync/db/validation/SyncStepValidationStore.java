package com.bjhy.data.sync.db.validation;

import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.loader.DataSourceLoader;
import com.bjhy.data.sync.db.natived.dao.StepStoreDao;
import com.bjhy.data.sync.db.natived.dao.impl.StepStoreDaoImpl;
import com.bjhy.data.sync.db.natived.domain.StepStoreEntity;
import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 同步步骤校验 存储逻辑类
 * @author wubo
 *
 */
public class SyncStepValidationStore {
	
	private static SyncStepValidationStore syncStepIntercept;
	
	private StepStoreDao stepStoreDao;
	
	private SyncStepValidationStore(){
		this.stepStoreDao = new StepStoreDaoImpl();
	}
	
	/**
	 * 拦截器
	 * @param singleStepSyncConfig 单个步骤同步配置参数
	 */
	public void intercept(SingleStepSyncConfig singleStepSyncConfig){
		StepStoreEntity initStepStoreEntity = initStepStoreEntity(singleStepSyncConfig);//初始化步骤存储实体
		save(initStepStoreEntity,singleStepSyncConfig);
	}
	
	/**
	 * 保存数据
	 */
	public void save(StepStoreEntity stepStoreEntity,SingleStepSyncConfig singleStepSyncConfig){
		
		StepStoreEntity findOneByTaskAndName = stepStoreDao.findOneByTaskAndName(stepStoreEntity);
		if(findOneByTaskAndName == null){
			stepStoreDao.store(stepStoreEntity);
		}else{
			String startStepSyncType = singleStepSyncConfig.getStartStepSyncType();
			if(SingleStepSyncConfig.START_STEP_USER_SYNC.equals(startStepSyncType)){
				stepStoreEntity.setId(findOneByTaskAndName.getId());
				stepStoreDao.update(stepStoreEntity);
			}
		}
	}
	
	/**
	 * 初始化步骤存储实体
	 */
	private StepStoreEntity initStepStoreEntity(SingleStepSyncConfig singleStepSyncConfig){
		//步骤存储实体
		StepStoreEntity stepStoreEntity = new StepStoreEntity();
		stepStoreEntity.setIsRepairSync("0");
		stepStoreEntity.setId(DataSourceLoader.getUUID());
		stepStoreEntity.setFromTask(singleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().getFromTask());
		stepStoreEntity.setToTask(singleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().getToTask());
		stepStoreEntity.setFromDataSourceName(singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName());
		stepStoreEntity.setFromDataSourceNumber(singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber());
		stepStoreEntity.setToDataSourceName(singleStepSyncConfig.getSingleRunEntity().getToTemplate().getConnectConfig().getDataSourceName());
		stepStoreEntity.setToDataSourceNumber(singleStepSyncConfig.getSingleRunEntity().getToTemplate().getConnectConfig().getDataSourceNumber());
//		stepStoreEntity.setFromDataNumber(fromDataNumber);
//		stepStoreEntity.setToDataNumber(toDataNumber);
		stepStoreEntity.setStepUniquelyIdentifies(singleStepSyncConfig.getStepUniquelyIdentifies());
		stepStoreEntity.setSingleStepSyncConfig(singleStepSyncConfig);
		stepStoreEntity.setToTableName(singleStepSyncConfig.getToTableName());
		return stepStoreEntity;
	}
	
	/**
	 * 编辑修复同步标记
	 * @param singleStepSyncConfig
	 */
	public void editIsRepirSyncFlag(SingleStepSyncConfig singleStepSyncConfig){
		String startStepSyncType = singleStepSyncConfig.getStartStepSyncType();
		if(SingleStepSyncConfig.START_STEP_USER_SYNC.equals(startStepSyncType)){
			//得到编辑修复同步标记的查询实体
			StepStoreEntity editIsRepirSyncFlagEntity = getEditIsRepirSyncFlagEntity(singleStepSyncConfig);
			
			//通过 fromTask,toTask,toTableName,stepUniquelyIdentifies,isReqairSync 查询数据
			StepStoreEntity findOneByTaskAndName = stepStoreDao.findOneByTaskAndNameAndIsRepairSync(editIsRepirSyncFlagEntity);
			
			if(findOneByTaskAndName == null){
				
				String dataSourceName = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
				String dataSourceNumber = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
				String toTableName = singleStepSyncConfig.getToTableName();
				String stepUniquelyIdentifies = singleStepSyncConfig.getStepUniquelyIdentifies();
				LoggerUtils.warn("没有找的当前步骤:  表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber+" , 步骤唯一标示 : "+stepUniquelyIdentifies);
			}else{
				
					editIsRepirSyncFlagEntity.setId(findOneByTaskAndName.getId());
					editIsRepirSyncFlagEntity.setIsRepairSync("1");
					stepStoreDao.updateByRepirSync(editIsRepirSyncFlagEntity);
			}
		}
	}
	
	/**
	 * 得到编辑修复同步标记的查询实体
	 * @param singleStepSyncConfig
	 * @return
	 */
	private StepStoreEntity getEditIsRepirSyncFlagEntity(SingleStepSyncConfig singleStepSyncConfig){
		StepStoreEntity stepStoreEntity = new StepStoreEntity();
		stepStoreEntity.setIsRepairSync("0");
		stepStoreEntity.setFromTask(singleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().getFromTask());
		stepStoreEntity.setToTask(singleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().getToTask());
		stepStoreEntity.setFromDataSourceName(singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName());
		stepStoreEntity.setFromDataSourceNumber(singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber());
		stepStoreEntity.setToDataSourceName(singleStepSyncConfig.getSingleRunEntity().getToTemplate().getConnectConfig().getDataSourceName());
		stepStoreEntity.setToDataSourceNumber(singleStepSyncConfig.getSingleRunEntity().getToTemplate().getConnectConfig().getDataSourceNumber());
		stepStoreEntity.setStepUniquelyIdentifies(singleStepSyncConfig.getStepUniquelyIdentifies());
		stepStoreEntity.setToTableName(singleStepSyncConfig.getToTableName());
		return stepStoreEntity;
	}
	
	/**
	 * 得到同步步骤拦截器
	 * @return
	 */
	public static SyncStepValidationStore getInstance(){
		if(syncStepIntercept == null){
			syncStepIntercept = new SyncStepValidationStore();
		}
		return syncStepIntercept;
	}
}
