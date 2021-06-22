package com.bjhy.data.sync.db.natived.dao.impl;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.natived.dao.VersionCheckDao;
import com.bjhy.data.sync.db.util.DataSourceUtil;

/**
 * 
 * @author wubo
 *
 */
public class VersionCheckDaoFactory{
	/**
	 * 版本检测dao
	 */
	private static VersionCheckDao versionCheckDao;
	

	private VersionCheckDaoFactory() {}
	
	/**
	 * 得到具体的 [版本检测dao] 对象,且该对象始终只有一个
	 * @return
	 */
	public static VersionCheckDao getVersionCheckDao(){
		if(versionCheckDao == null){
			synchronized(VersionCheckDaoFactory.class){
				if(versionCheckDao == null){
					new VersionCheckDaoFactory().initVersionCheckDao();
				}
			}
		}
		return versionCheckDao;
	}
	
	/**
	 * 初始化VersionCheckDao的实现对象
	 */
	private void initVersionCheckDao(){
		SyncTemplate enableNativeSyncTemplate = DataSourceUtil.getInstance().getEnableNativeSyncTemplate();
		String databaseType = enableNativeSyncTemplate.getConnectConfig().getDatabaseType();
		
		if("Oracle".equalsIgnoreCase(databaseType)){
			versionCheckDao = new OracleVersionCheckDaoImpl();
			
		}else if("SqlServer".equalsIgnoreCase(databaseType)){
			versionCheckDao = new SqlServerVersionCheckDaoImpl();
			
		}else if("MySql".equalsIgnoreCase(databaseType) || "mariadb".equalsIgnoreCase(databaseType)){
			versionCheckDao = new MySqlVersionCheckDaoImpl();
			
		}else if("DM".equalsIgnoreCase(databaseType)){
			versionCheckDao = new DmVersionCheckDaoImpl();
			
		}else if("kingbase8".equalsIgnoreCase(databaseType) || "kingbase".equalsIgnoreCase(databaseType)){
			versionCheckDao = new KingbaseVersionCheckDaoImpl();
		}
	}

}
