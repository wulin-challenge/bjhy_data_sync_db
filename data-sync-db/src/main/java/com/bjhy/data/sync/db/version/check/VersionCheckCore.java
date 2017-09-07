package com.bjhy.data.sync.db.version.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.VersionCheck;
import com.bjhy.data.sync.db.loader.DataSourceLoader;
import com.bjhy.data.sync.db.natived.dao.VersionCheckDao;
import com.bjhy.data.sync.db.natived.dao.impl.VersionCheckDaoFactory;
import com.bjhy.data.sync.db.natived.domain.VersionCheckEntity;

/**
 * 版本检测逻辑类
 * @author wubo
 *
 */
public class VersionCheckCore {
	
	/**
	 * 同步版本检测字段
	 */
	public final static String SYNC_VERSION_CHECK = "SYNC_VERSION_CHECK_";
	
	private SyncLogicEntity syncLogicEntity;
	
	/**
	 * 版本检查接口
	 */
	private VersionCheck versionCheck;
	
	/**
	 * 版本检测dao 接口
	 */
	private VersionCheckDao versionCheckDao;

	public VersionCheckCore(SyncLogicEntity syncLogicEntity) {
		this.syncLogicEntity = syncLogicEntity;
		this.versionCheckDao = new VersionCheckDaoFactory(syncLogicEntity).getVersionCheckDao();
		initVersionCheck(); //初始化VersionCheck的实现对象
		addVersionCheckColumn(); //添加versionCheckColumn
		storeOrUpdateVersionCheck();//保存或者更新 VersionCheck值
	}
	
	/**
	 * 初始化VersionCheck的实现对象
	 */
	private void initVersionCheck(){
		String databaseType = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDatabaseType();
		
		if("Oracle".equalsIgnoreCase(databaseType)){
			versionCheck = new OracleVersionCheck(syncLogicEntity);
			
		}else if("SqlServer".equalsIgnoreCase(databaseType)){
			versionCheck = new SqlServerVersionCheck(syncLogicEntity);
			
		}else if("MySql".equalsIgnoreCase(databaseType)){
			versionCheck = new MysqlVersionCheck(syncLogicEntity);
			
		}else if("DM".equalsIgnoreCase(databaseType)){
			versionCheck = new DmVersionCheck(syncLogicEntity);
			
		}
	}
	
	/**
	 * 添加versionCheckColumn
	 */
	private void addVersionCheckColumn(){
		Set<String> toColumns = syncLogicEntity.getToColumns();
		
		String syncVersionCheck = VersionCheckCore.SYNC_VERSION_CHECK;
		
		if(!toColumns.contains(syncVersionCheck)){
			NamedParameterJdbcTemplate namedToTemplate = syncLogicEntity.getNamedToTemplate();
			String addVersionCheckColumnSql = versionCheck.getAddVersionCheckColumnSql();
			namedToTemplate.getJdbcOperations().execute(addVersionCheckColumnSql);
		}
	
	}
	
	/**
	 * 保存或者更新 VersionCheck值
	 */
	public void storeOrUpdateVersionCheck(){
		VersionCheckEntity versionCheckEntity = getVersionCheckEntity();
		VersionCheckEntity findOneByTaskAndTableName = versionCheckDao.findOneByTaskAndTableName(versionCheckEntity);
		
		if(findOneByTaskAndTableName == null){
			versionCheckEntity.setId(DataSourceLoader.getUUID());
			versionCheckEntity.setCurrentCheckVersion(syncLogicEntity.getCheckVersion());
			versionCheckDao.store(versionCheckEntity);
		}else{
			BeanUtils.copyProperties(findOneByTaskAndTableName, versionCheckEntity);
			
			versionCheckEntity.setBeforeCheckVersion(getHistoryCheckVersion(20, findOneByTaskAndTableName));
			versionCheckEntity.setCurrentCheckVersion(syncLogicEntity.getCheckVersion());
			versionCheckDao.update(versionCheckEntity);
		}
		
	}
	
	/**
	 * 得到指定长度的历史版本字符串
	 * @param historyNumber 历史版本长度
	 * @param findOneByTaskAndTableName
	 * @return
	 */
	private String getHistoryCheckVersion(Integer historyVersionNumber,VersionCheckEntity findOneByTaskAndTableName){
		StringBuffer historyCheckVersion = new StringBuffer();
		List<String> historyCheckVersionList = new ArrayList<String>();
		String beforeCheckVersion = findOneByTaskAndTableName.getBeforeCheckVersion();
		String currentCheckVersion = findOneByTaskAndTableName.getCurrentCheckVersion();
		
		
		if(StringUtils.isNotEmpty(beforeCheckVersion)){
			String[] split = beforeCheckVersion.split(",");
			historyCheckVersionList.addAll(Arrays.asList(split));
		}
		
		if(StringUtils.isNotEmpty(currentCheckVersion)){
			historyCheckVersionList.add(0, currentCheckVersion);
		}
		
		//判断历史长度
		if(historyCheckVersionList != null && historyCheckVersionList.size()<historyVersionNumber){
			historyVersionNumber = historyCheckVersionList.size();
		}
		List<String> subList = historyCheckVersionList.subList(0, historyVersionNumber);
		
		//将list转换为用,分隔的字符串
		Boolean isFirst = true;
		for (String checkVersion : subList) {
			if(isFirst){
				isFirst = false;
				historyCheckVersion.append(checkVersion);
			}else{
				historyCheckVersion.append(","+checkVersion);
			}
		}
		
		return historyCheckVersion.toString();
	}
	
	/**
	 * 清楚上个版本或者检测类为null的数据
	 */
	public void clearBeforeVertionData(){
		String syncVersionCheck = VersionCheckCore.SYNC_VERSION_CHECK;
		VersionCheckEntity versionCheckEntity = getVersionCheckEntity();
		
		VersionCheckEntity findOneByTaskAndTableName = versionCheckDao.findOneByTaskAndTableName(versionCheckEntity);
		String beforeCheckVersion = findOneByTaskAndTableName.getBeforeCheckVersion();
		NamedParameterJdbcTemplate namedToTemplate = syncLogicEntity.getNamedToTemplate();
		
		//版本删除参数
		List<String> deleteVersionCheckParams = new ArrayList<String>();
		Map<String,Object> deleteParams = new HashMap<String,Object>();
		
		if(StringUtils.isEmpty(beforeCheckVersion)){
			deleteVersionCheckParams.add("-1");//当参为空值,默认设置为-1
			deleteParams.put(syncVersionCheck, deleteVersionCheckParams);
		}else{
			String[] split = beforeCheckVersion.split(",");
			deleteVersionCheckParams.addAll(Arrays.asList(split));
			deleteParams.put(syncVersionCheck, deleteVersionCheckParams);
		}
		
		String toDeleteSql = versionCheck.getToDeleteByVersionCheck();
		namedToTemplate.update(toDeleteSql, deleteParams);
	}
	
	/**
	 * 得到 VersionCheckEntity
	 * @return
	 */
	private VersionCheckEntity getVersionCheckEntity(){
		VersionCheckEntity versionCheckEntity = new VersionCheckEntity();
		versionCheckEntity.setFromTask(syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getBaseRunEntity().getFromTask());
		versionCheckEntity.setToTask(syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getBaseRunEntity().getToTask());
		versionCheckEntity.setStepUniquelyIdentifies(syncLogicEntity.getSingleStepSyncConfig().getStepUniquelyIdentifies());
		versionCheckEntity.setFromDataSourceName(syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName());
		versionCheckEntity.setFromDataSourceNumber(syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber());
		versionCheckEntity.setToDataSourceName(syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getToTemplate().getConnectConfig().getDataSourceName());
		versionCheckEntity.setToDataSourceNumber(syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getToTemplate().getConnectConfig().getDataSourceNumber());
		versionCheckEntity.setToTableName(syncLogicEntity.getSingleStepSyncConfig().getToTableName());
		return versionCheckEntity;
	}
	
	
	
	

}
