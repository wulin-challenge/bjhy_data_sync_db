package com.bjhy.data.sync.db.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.core.BaseLoaderCore;
import com.bjhy.data.sync.db.datasource.DBDruidDataSource;
import com.bjhy.data.sync.db.domain.ConnectConfig;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.log.LogCache;

/**
 * 操作数据源的工具类
 * @author wubo
 *
 */
public class DataSourceUtil {
	
	private static DataSourceUtil dataSourceUtil;
	
	/**
	 * 得到能用的 来源 同步Template 通过 ConnectConfig
	 * @param connectConfig
	 * @return
	 */
	public SyncTemplate getEnableFromSyncTemplateByConnectConfig(ConnectConfig connectConfig){
		return getEnableFromSyncTemplateByConnectConfig(connectConfig, true);
	}
	
	/**
	 * 得到能用的 来源 同步Template 通过 ConnectConfig
	 * @param connectConfig
	 * @param isPrintLog是否打印日志
	 * @return
	 */
	public SyncTemplate getEnableFromSyncTemplateByConnectConfig(ConnectConfig connectConfig,boolean isPrintLog){
		//得到能用的 来源 同步Template
		List<SyncTemplate> enableFromSyncTemplateList = getEnableFromSyncTemplate(isPrintLog);
		for (SyncTemplate syncTemplate : enableFromSyncTemplateList) {
			ConnectConfig connectConfig2 = syncTemplate.getConnectConfig();
			if(connectConfig.getTask().equals(connectConfig2.getTask()) && connectConfig.getDatabaseType().equals(connectConfig2.getDatabaseType()) && connectConfig.getDataSourceNumber().equals(connectConfig2.getDataSourceNumber())){
				return syncTemplate;
			}
		}
		return null;
	}
	
	/**
	 * 得到能用的 目标 同步Template 通过 ConnectConfig
	 * @param connectConfig
	 * @return
	 */
	public SyncTemplate getEnableToSyncTemplateByConnectConfig(ConnectConfig connectConfig){
		return getEnableToSyncTemplateByConnectConfig(connectConfig, true);
	}
	
	/**
	 * 得到能用的 目标 同步Template 通过 ConnectConfig
	 * @param connectConfig
	 * @param isPrintLog 是否打印日志
	 * @return
	 */
	public SyncTemplate getEnableToSyncTemplateByConnectConfig(ConnectConfig connectConfig,boolean isPrintLog){
		//得到能用的 目标  同步Template
		List<SyncTemplate> enableToSyncTemplateList = getEnableToSyncTemplate(isPrintLog);
		for (SyncTemplate syncTemplate : enableToSyncTemplateList) {
			ConnectConfig connectConfig2 = syncTemplate.getConnectConfig();
			if(connectConfig.getTask().equals(connectConfig2.getTask()) && connectConfig.getDatabaseType().equals(connectConfig2.getDatabaseType()) && connectConfig.getDataSourceNumber().equals(connectConfig2.getDataSourceNumber())){
				return syncTemplate;
			}
		}
		return null;
	}
	
	/**
	 * 得到能用的 本地 同步Template 通过 ConnectConfig
	 * @param connectConfig
	 * @return
	 */
	public SyncTemplate getEnableNativeSyncTemplateByConnectConfig(ConnectConfig connectConfig){
		return getEnableNativeSyncTemplateByConnectConfig(connectConfig, true);
	}
	
	/**
	 * 得到能用的 本地 同步Template 通过 ConnectConfig
	 * @param connectConfig
	 * @param isPrintLog 是否打印日志
	 * @return
	 */
	public SyncTemplate getEnableNativeSyncTemplateByConnectConfig(ConnectConfig connectConfig,boolean isPrintLog){
		//得到能用的 本地 同步Template
		SyncTemplate syncTemplate = getEnableNativeSyncTemplate(isPrintLog);
		
		ConnectConfig connectConfig2 = syncTemplate.getConnectConfig();
		if(connectConfig.getTask().equals(connectConfig2.getTask()) 
		  && connectConfig.getDatabaseType().equals(connectConfig2.getDatabaseType()) 
		  && connectConfig.getDataSourceNumber().equals(connectConfig2.getDataSourceNumber())){
			
			return syncTemplate;
		}
		return null;
	}
	
	/**
	 * 通用数据源number从来源数据源配置和目标数据源配置中找到对应的 SyncTemplate
	 * @param dataSourceNumber 数据源number
	 * @return
	 */
	public SyncTemplate getEnableSyncTemplateBySourceNumber(String dataSourceNumber) {
		if(StringUtils.isBlank(dataSourceNumber)) {
			throw new IllegalArgumentException("dataSourceNumber 参数不能为空!");
		}
		dataSourceNumber = dataSourceNumber.trim();
		
		List<SyncTemplate> newEnableSyncTemplateList = new ArrayList<SyncTemplate>();
		//得到能用的 来源 同步Template
		List<SyncTemplate> enableFromSyncTemplateList = getEnableFromSyncTemplate();
		//得到能用的 来源 同步Template
		List<SyncTemplate> enableToSyncTemplateList = getEnableToSyncTemplate();
		
		newEnableSyncTemplateList.addAll(enableFromSyncTemplateList);
		newEnableSyncTemplateList.addAll(enableToSyncTemplateList);
		
		for (SyncTemplate syncTemplate : newEnableSyncTemplateList) {
			if(syncTemplate.getConnectConfig().getDataSourceNumber().trim().equals(dataSourceNumber)) {
				return syncTemplate;
			}
		}
		return null;
	}
	
	/**
	 * 得到能用的 来源 同步Template 通过 fromTask
	 * @param fromTask 来源任务
	 * @return
	 */
	public List<SyncTemplate> getEnableFromSyncTemplateByTask(String fromTask){
		List<SyncTemplate> newEnableFromSyncTemplateList = new ArrayList<SyncTemplate>();
		//得到能用的 来源 同步Template
		List<SyncTemplate> enableFromSyncTemplateList = getEnableFromSyncTemplate();
		
		for (SyncTemplate syncTemplate : enableFromSyncTemplateList) {
			String[] taskArray = syncTemplate.getConnectConfig().getTask().split(",");
			secondFor:for (String task : taskArray) {
				if(fromTask.trim().equalsIgnoreCase(task.trim())){
					newEnableFromSyncTemplateList.add(syncTemplate);
					break secondFor;
				}
			}
		}
		return newEnableFromSyncTemplateList;
	}
	
	/**
	 * 得到能用的 目标  同步Template 通过 toTask
	 * @param toTask 目标任务
	 * @return
	 */
	public List<SyncTemplate> getEnableToSyncTemplateByTask(String toTask){
		List<SyncTemplate> newEnableToSyncTemplateList = new ArrayList<SyncTemplate>();
		//得到能用的 来源 同步Template
		List<SyncTemplate> enableToSyncTemplateList = getEnableToSyncTemplate();
		
		for (SyncTemplate syncTemplate : enableToSyncTemplateList) {
			String[] taskArray = syncTemplate.getConnectConfig().getTask().split(",");
			secondFor:for (String task : taskArray) {
				if(toTask.trim().equalsIgnoreCase(task.trim())){
					newEnableToSyncTemplateList.add(syncTemplate);
					break secondFor;
				}
			}
		}
		return newEnableToSyncTemplateList;
	}
	
	/**
	 * 得到能用的 来源 同步Template
	 * 
	 * @return
	 */
	public List<SyncTemplate> getEnableFromSyncTemplate(){
		return getEnableFromSyncTemplate(true);
	}
	
	/**
	 * 得到能用的 来源 同步Template
	 * 
	 * @param isPrintLog是否打印日志
	 * @return
	 */
	public List<SyncTemplate> getEnableFromSyncTemplate(boolean isPrintLog){
		List<SyncTemplate> enableSyncTemplate = new ArrayList<SyncTemplate>();
		
		List<SyncTemplate> fromSyncTemplate = BaseLoaderCore.getInstance().getFromSyncTemplate();
		for (SyncTemplate syncTemplate : fromSyncTemplate) {
			Boolean enableSyncTemplate2 = isEnableSyncTemplate(syncTemplate,isPrintLog);
			if(enableSyncTemplate2){
				enableSyncTemplate.add(syncTemplate);
			}
		}
		return enableSyncTemplate;
	}
	
	/**
	 * 得到能用的 目标 同步Template
	 * @return
	 */
	public List<SyncTemplate> getEnableToSyncTemplate(){
		return getEnableToSyncTemplate(true);
	}
	
	/**
	 * 得到能用的 目标 同步Template
	 * @param isPrintLog 是否打印日志
	 * @return
	 */
	public List<SyncTemplate> getEnableToSyncTemplate(boolean isPrintLog){
		List<SyncTemplate> enableSyncTemplate = new ArrayList<SyncTemplate>();
		
		List<SyncTemplate> toSyncTemplate = BaseLoaderCore.getInstance().getToSyncTemplate();
		for (SyncTemplate syncTemplate : toSyncTemplate) {
			Boolean enableSyncTemplate2 = isEnableSyncTemplate(syncTemplate,isPrintLog);
			if(enableSyncTemplate2){
				enableSyncTemplate.add(syncTemplate);
			}
		}
		return enableSyncTemplate;
	}
	
	/**
	 * 得到能用的 本地 存储Template
	 * @return
	 */
	public SyncTemplate getEnableNativeSyncTemplate(){
		return getEnableNativeSyncTemplate(true);
	}
	
	/**
	 * 得到能用的 本地 存储Template
	 * @param isPrintLog 是否打印日志
	 * @return
	 */
	public SyncTemplate getEnableNativeSyncTemplate(boolean isPrintLog){
		
		SyncTemplate nativeStoreTemplate = BaseLoaderCore.getInstance().getNativeStoreTemplate();
		Boolean enableSyncTemplate2 = isEnableSyncTemplate(nativeStoreTemplate,isPrintLog);
		if(enableSyncTemplate2){
			return nativeStoreTemplate;
		}
		return null;
	}
	
	/**
	 * 可以用
	 * @param syncTemplate
	 * @return
	 */
	public Boolean isEnableSyncTemplate(SyncTemplate syncTemplate){
		return isEnableSyncTemplate(syncTemplate,true);
	}
	
	/**
	 * 可以用
	 * @param syncTemplate
	 * @return
	 */
	public Boolean isEnableSyncTemplate(SyncTemplate syncTemplate,boolean isPrintLog){
		ConnectConfig connect = syncTemplate.getConnectConfig();
		if(!connect.getIsEnable()){
			if(isPrintLog){
				LogCache.addDataSourceLog("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源以被停用!!");
				LoggerUtils.warn("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源以被停用!!");
			}
			return false;
		}
		return isEnableConnection(syncTemplate);
	}
	
	/**
	 * 测试连接是否可用
	 * @param syncTemplate
	 * @return
	 */
	public boolean isEnableConnection(SyncTemplate syncTemplate){
		DataSource dataSource = syncTemplate.getDataSource();
		if(dataSource instanceof DBDruidDataSource){
			return isEnableUseDruidConnection(syncTemplate);
		}
		return isEnableUseDefaultConnection(syncTemplate);
	}
	
	private boolean isEnableUseDruidConnection(SyncTemplate syncTemplate){
		ConnectConfig connect = syncTemplate.getConnectConfig();
		DBDruidDataSource dataSource = (DBDruidDataSource)syncTemplate.getDataSource();
		
		Connection connection = null;
		try {
			connection = dataSource.getTestConnection();
		}catch (SQLException e) {
			LogCache.addDataSourceLog("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源不能连接!!-->详细信息 : "+e.getMessage());
			LoggerUtils.error("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源不能连接!!-->详细信息 : "+e.getMessage());
			return false;
		}finally{
			if(connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					LogCache.addDataSourceLog("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源不能连接!!-->详细信息 : "+e.getMessage());
					LoggerUtils.warn("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源不能连接!!-->详细信息 : "+e.getMessage());
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 使用默认方式测试连接是否可用
	 * @param syncTemplate
	 * @return
	 */
	private boolean isEnableUseDefaultConnection(SyncTemplate syncTemplate){
		ConnectConfig connect = syncTemplate.getConnectConfig();
		DataSource driverManagerDataSource = syncTemplate.getDriverManagerDataSource();
		Connection connection = null;
		try {
			connection = driverManagerDataSource.getConnection();
			connection.close();
		}catch (SQLException e) {
			LogCache.addDataSourceLog("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源不能连接!!-->详细信息 : "+e.getMessage());
			LoggerUtils.warn("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源不能连接!!-->详细信息 : "+e.getMessage());
			return false;
		}finally{
			if(connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					LogCache.addDataSourceLog("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源不能连接!!-->详细信息 : "+e.getMessage());
					LoggerUtils.warn("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:当前数据源不能连接!!-->详细信息 : "+e.getMessage());
				}
			}
		}
		return true;
	}
	
	/**
	 * 得到NamedTemplate
	 * @param syncTemplate
	 * @return
	 */
	public NamedParameterJdbcTemplate getNamedTemplate(SyncTemplate syncTemplate){
		NamedParameterJdbcTemplate jdbcTemplate  = new NamedParameterJdbcTemplate(syncTemplate);  
		return jdbcTemplate;
	}
	
	public static DataSourceUtil getInstance(){
		if(dataSourceUtil == null){
			synchronized(DataSourceUtil.class) {
				if(dataSourceUtil == null){
					dataSourceUtil = new DataSourceUtil();
				}
			}
		}
		return dataSourceUtil;
	}

}
