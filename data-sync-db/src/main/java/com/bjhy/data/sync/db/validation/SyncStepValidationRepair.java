package com.bjhy.data.sync.db.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.ConcurrentHashSet;
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
import com.bjhy.data.sync.db.thread.ThreadFactoryImpl;
import com.bjhy.data.sync.db.util.DataSourceUtil;
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
	
	/**
	 * 修复最大重试次数
	 */
	private int maxRetryNumber = 5;
	
	/**
	 * 修复线程运行状态
	 */
	private volatile boolean repairThreadRunStatus = false;
	
	/**
	 * 需要修复的步骤
	 */
	private ConcurrentHashSet<SingleStepSyncConfig> needRepairSteps = new ConcurrentHashSet<SingleStepSyncConfig>();
	
	/**
	 * 最大修复次数
	 */
	private ConcurrentHashMap<SingleStepSyncConfig,Integer> stepMaxRepairNumber  = new  ConcurrentHashMap<SingleStepSyncConfig,Integer>();
	
	/**
	 * 定时执行修复线程
	 */
	private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
	        "SyncStepRepairScheduledThread"));
	
	private StepStoreDao stepStoreDao;
	
	private VersionCheckDao versionCheckDao;
	
	/**
	 * 暂时未使用
	 */
	@SuppressWarnings("unused")
	private SyncTemplate enableNativeSyncTemplate;
	
	private SyncStepValidationRepair(){
		this.enableNativeSyncTemplate = DataSourceUtil.getInstance().getEnableNativeSyncTemplate();
		this.stepStoreDao = new StepStoreDaoImpl();
		this.versionCheckDao = VersionCheckDaoFactory.getVersionCheckDao();
	}
	
	/**
	 * 校验修复开始,这是修复线程的入口
	 */
	public void validationRepairStart(){
		
		 if(repairThreadRunStatus){
			return; 
		 }
		 repairThreadRunStatus = true;
		 
		 final long period = 120;
         this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
             @Override
             public void run() {
                scheduledRepairLogic();
             }
         }, 60, period, TimeUnit.SECONDS);
         
         final long period2 = 60;
         this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
             @Override
             public void run() {
            	 // 修复定时同步过程中的步骤
            	 try {
					scheduledSyncRunningStep();
				} catch (Throwable e) {
					logger.error("scheduled scheduledSyncRunningStep error : "+e.getMessage());
				}
             }
         }, 30, period2, TimeUnit.SECONDS);
	}
	
	/**
	 * 修复定时同步过程中的步骤
	 */
	private void scheduledSyncRunningStep(){
		
		//若有待修复的步骤,这添加到最大修复步骤中
		addToStepMaxRepairNumber();
		//执行步骤修复
		executeStepRepair();
	}
	
	/**
	 * 若有待修复的步骤,这添加到最大修复步骤中
	 */
	private void addToStepMaxRepairNumber(){
		if(needRepairSteps.size() == 0){
			return;
		}
		
		//要修复的步骤
		Set<SingleStepSyncConfig> toRepairSteps = new HashSet<SingleStepSyncConfig>();
		toRepairSteps.addAll(needRepairSteps);
		
		//从需要修复的步骤中清除[要修复的步骤]
		needRepairSteps.removeAll(toRepairSteps);
		
		for (SingleStepSyncConfig singleStepSyncConfig : toRepairSteps) {
			//此处只添加来自用户定时同步的步骤,不添加系统自动修复的步骤
			if(SingleStepSyncConfig.START_STEP_USER_SYNC.equals(singleStepSyncConfig.getStartStepSyncType())){
				singleStepSyncConfig.setStartStepSyncType(SingleStepSyncConfig.START_STEP_SYSTEM_CHECK_SYNC);
				stepMaxRepairNumber.put(singleStepSyncConfig, maxRetryNumber);
			}
		}
	}
	
	/**
	 * 执行步骤修复
	 */
	private void executeStepRepair(){
		if(stepMaxRepairNumber.size() == 0){
			return;
		}
		
		Set<Entry<SingleStepSyncConfig, Integer>> entrySet = new ConcurrentHashMap<SingleStepSyncConfig,Integer>(stepMaxRepairNumber).entrySet();
		for (Entry<SingleStepSyncConfig, Integer> entry : entrySet) {
			SingleStepSyncConfig singleStepSyncConfig = entry.getKey();
			Integer repairNumber = entry.getValue()-1;
			
			if(repairNumber<0){
				logger.error("该步骤执行"+maxRetryNumber+"次修复都没有成功!在下一次用户自定义的定时器到来之前将放弃修复,请人工解决!,具体步骤信息:"+singleStepSyncConfig);
				removeStepMaxRepairNumber(singleStepSyncConfig);
			}else{
				logger.info("步骤 "+singleStepSyncConfig.getStepUniquelyIdentifies()+" 正在执行第 "+(maxRetryNumber-repairNumber)+"次修复,最多执行 "+maxRetryNumber+"次!");
				stepMaxRepairNumber.put(singleStepSyncConfig, (repairNumber));
				
				BaseCore baseCore = new BaseCore();
				baseCore.syncEntry(singleStepSyncConfig);
			}
		}
		
		
	}
	
	/**
	 * 删除 [最大修复次数] 列表中的步骤
	 * @param singleStepSyncConfig
	 */
	public void removeStepMaxRepairNumber(SingleStepSyncConfig singleStepSyncConfig){
		if(SingleStepSyncConfig.START_STEP_SYSTEM_CHECK_SYNC.equals(singleStepSyncConfig.getStartStepSyncType())){
			if(stepMaxRepairNumber.containsKey(singleStepSyncConfig)){
				stepMaxRepairNumber.remove(singleStepSyncConfig);
			}
		}
	}
	
	/**
	 * 定时修复逻辑
	 */
	private void scheduledRepairLogic(){
		try {
			validationLogic();//校验的逻辑
			repairData();
		} catch (Throwable e) {
			logger.error("修复数据时出现了错误:错误信息 : "+e.getMessage());
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
			try {
				toVersionCheckAndDelete(singleStepSyncConfig, toTemplate, validationParams);
			} catch (Exception e) {
				String dataSourceName = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
				String dataSourceNumber = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
				String toTableName = singleStepSyncConfig.getToTableName();
				
				logger.error("改表存在程序无法修复的数据,请人工处理! "+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber+" , 错误信息  : "+e.getMessage());
			}
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
				SyncStepValidationRepair.getInstance().getNeedRepairSteps().add(singleStepSyncConfig);
			}
		}
	}
	
	/**
	 * 得到同步步骤验证修复实例,有且只有一个实例
	 * @return
	 */
	public static SyncStepValidationRepair getInstance(){
		if(syncStepValidationRepair == null){
			synchronized(SyncStepValidationRepair.class){
				if(syncStepValidationRepair == null){
					syncStepValidationRepair = new SyncStepValidationRepair();
				}
			}
		}
		return syncStepValidationRepair;
	}

	/**
	 * 得到需要修复的步骤集合
	 * @return
	 */
	public ConcurrentHashSet<SingleStepSyncConfig> getNeedRepairSteps() {
		return needRepairSteps;
	}
	
}
