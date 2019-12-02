package com.bjhy.data.sync.db.domain;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.inter.face.OwnInterface.AllThreadAfterRunEndListener;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

/**
 * 同步逻辑实体
 * @author wubo
 *
 */
public class SyncLogicEntity {
	
	/**
	 * 单个步骤同步配置参数
	 */
	private SingleStepSyncConfig singleStepSyncConfig;
	
	//版本检测逻辑类
	private VersionCheckCore versionCheckCore;
	
	/**
	 * 同步列(线程安全的List)
	 */
	private Set<String> syncColumns = new LinkedHashSet<String>();
	
	/**
	 * 目标表的所有列(线程安全的List)
	 */
	private Set<String> toColumns = new LinkedHashSet<String>();
	
	/**
	 * 增量数据同步hash
	 */
	private Map<Object,Map<String,Object>> incrementalSyncDataHash = new HashMap<Object,Map<String,Object>>();
	
	/**
	 * 插入的sql
	 */
	private String insertSql;
	
	/**
	 * 更新语句Sql
	 */
	private String updateSql;
	
	/**
	 * 删除语句Sql
	 */
	private String deleteSql;
	
	/**
	 * 检查语句Sql
	 */
	private String checkSql;
	
	/**
	 * 检测版本号
	 */
	private String checkVersion;
	
	/**
	 * 同步步骤日志信息实体 
	 */
	private SyncStepLogInfoEntity syncStepLogInfoEntity;
	
	/**
	 * 所有线程运行后结束监听器
	 */
	private AllThreadAfterRunEndListener endListener;
	
	/**
	 * 同步步骤Id
	 */
	private long syncStepId;
	
	/**
	 * 得到Named式的fromTemplate
	 * @return
	 */
	public NamedParameterJdbcTemplate getNamedFromTemplate(){
		SyncTemplate fromTemplate = singleStepSyncConfig.getSingleRunEntity().getFromTemplate();
		NamedParameterJdbcTemplate jdbcTemplate  = new NamedParameterJdbcTemplate(fromTemplate);  
		return jdbcTemplate;
	}
	
	/**
	 * 得到Named式的toTemplate
	 * @return
	 */
	public NamedParameterJdbcTemplate getNamedToTemplate(){
		SyncTemplate toTemplate = singleStepSyncConfig.getSingleRunEntity().getToTemplate();
		NamedParameterJdbcTemplate jdbcTemplate  = new NamedParameterJdbcTemplate(toTemplate);  
		return jdbcTemplate;
	}
	
	/**
	 * 得到Named式的NativeTemplate
	 * @return
	 */
	public NamedParameterJdbcTemplate getNamedNativeTemplate(){
		SyncTemplate nativeTemplate = singleStepSyncConfig.getSingleRunEntity().getNativeTemplate();
		NamedParameterJdbcTemplate jdbcTemplate  = new NamedParameterJdbcTemplate(nativeTemplate);  
		return jdbcTemplate;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}

	public String getDeleteSql() {
		return deleteSql;
	}

	public void setDeleteSql(String deleteSql) {
		this.deleteSql = deleteSql;
	}

	public String getCheckSql() {
		return checkSql;
	}

	public void setCheckSql(String checkSql) {
		this.checkSql = checkSql;
	}

	public SingleStepSyncConfig getSingleStepSyncConfig() {
		return singleStepSyncConfig;
	}

	public void setSingleStepSyncConfig(SingleStepSyncConfig singleStepSyncConfig) {
		this.singleStepSyncConfig = singleStepSyncConfig;
	}

	public String getCheckVersion() {
		return checkVersion;
	}

	public void setCheckVersion(String checkVersion) {
		this.checkVersion = checkVersion;
	}

	public Set<String> getSyncColumns() {
		return syncColumns;
	}

	public void setSyncColumns(Set<String> syncColumns) {
		this.syncColumns = syncColumns;
	}

	public Set<String> getToColumns() {
		return toColumns;
	}

	public void setToColumns(Set<String> toColumns) {
		this.toColumns = toColumns;
	}

	public VersionCheckCore getVersionCheckCore() {
		return versionCheckCore;
	}

	public void setVersionCheckCore(VersionCheckCore versionCheckCore) {
		this.versionCheckCore = versionCheckCore;
	}

	public Map<Object, Map<String, Object>> getIncrementalSyncDataHash() {
		return incrementalSyncDataHash;
	}

	public SyncStepLogInfoEntity getSyncStepLogInfoEntity() {
		return syncStepLogInfoEntity;
	}

	public void setSyncStepLogInfoEntity(SyncStepLogInfoEntity syncStepLogInfoEntity) {
		this.syncStepLogInfoEntity = syncStepLogInfoEntity;
	}
	
	public AllThreadAfterRunEndListener getEndListener() {
		return endListener;
	}

	public void setEndListener(AllThreadAfterRunEndListener endListener) {
		this.endListener = endListener;
	}

	public long getSyncStepId() {
		return syncStepId;
	}

	public void setSyncStepId(long syncStepId) {
		this.syncStepId = syncStepId;
	}
}
