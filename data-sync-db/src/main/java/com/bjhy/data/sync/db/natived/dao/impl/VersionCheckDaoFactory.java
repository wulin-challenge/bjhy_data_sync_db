package com.bjhy.data.sync.db.natived.dao.impl;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.natived.dao.VersionCheckDao;
import com.bjhy.data.sync.db.version.check.OracleVersionCheck;
import com.bjhy.data.sync.db.version.check.SqlServerVersionCheck;

public class VersionCheckDaoFactory{
	
	/**
	 * 同步逻辑实体
	 */
	private SyncLogicEntity syncLogicEntity;
	
	/**
	 * 版本检测dao
	 */
	private VersionCheckDao versionCheckDao;
	

	public VersionCheckDaoFactory(SyncLogicEntity syncLogicEntity) {
		super();
		this.syncLogicEntity = syncLogicEntity;
		initVersionCheckDao();
	}
	
	
	
	/**
	 * 初始化VersionCheckDao的实现对象
	 */
	private void initVersionCheckDao(){
		String databaseType = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDatabaseType();
		
		if("Oracle".equalsIgnoreCase(databaseType)){
			versionCheckDao = new OracleVersionCheckDaoImpl(syncLogicEntity);
			
		}else if("SqlServer".equalsIgnoreCase(databaseType)){
			versionCheckDao = new SqlServerVersionCheckDaoImpl(syncLogicEntity);
			
		}else if("MySql".equalsIgnoreCase(databaseType)){
			versionCheckDao = new MySqlVersionCheckDaoImpl(syncLogicEntity);
			
		}else if("DM".equalsIgnoreCase(databaseType)){
			versionCheckDao = new DmVersionCheckDaoImpl(syncLogicEntity);
			
		}
	}

	public VersionCheckDao getVersionCheckDao() {
		return versionCheckDao;
	}
	
}
