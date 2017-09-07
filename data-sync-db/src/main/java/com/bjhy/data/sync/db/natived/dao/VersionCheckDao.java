package com.bjhy.data.sync.db.natived.dao;

import com.bjhy.data.sync.db.natived.domain.VersionCheckEntity;

/**
 * 版本检测dao
 * @author wubo
 *
 */
public interface VersionCheckDao {
	
	/**
	 * 保存
	 * @param versionCheckEntity 版本检测实体
	 */
	public void store(VersionCheckEntity versionCheckEntity);
	
	/**
	 * 更新
	 * @param versionCheckEntity 版本检测实体
	 */
	public void update(VersionCheckEntity versionCheckEntity);
	
	/**
	 * 通过fromTask,toTask,fromTableName,toTableName查找VersionCheckEntity
	 * @param versionCheckEntity
	 * @return versionCheckEntity
	 */
	public VersionCheckEntity findOneByTaskAndTableName(VersionCheckEntity versionCheckEntity);

}
