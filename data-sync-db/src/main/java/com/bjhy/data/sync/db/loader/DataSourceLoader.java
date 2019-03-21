package com.bjhy.data.sync.db.loader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.bjhy.data.sync.db.core.BaseLoaderCore;
import com.bjhy.data.sync.db.datasource.DBDruidDataSource;
import com.bjhy.data.sync.db.domain.ConnectConfig;
import com.bjhy.data.sync.db.domain.SyncConfig;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.loader.xml.DataSourceLoaderXml;
import com.bjhy.data.sync.db.loader.xml.DataSourceLoaderXml.DataSources;
import com.bjhy.data.sync.db.log.LogCache;
import com.bjhy.data.sync.db.util.LoggerUtils;
import com.bjhy.data.sync.db.util.SyncPropertiesUtil;

/**
 * 数据源加载(同步时才来加载数据源)
 * @author wubo
 */
public class DataSourceLoader {
	
	private static DataSourceLoader dataSourceLoader;
	
	/**
	 * 得到来源同步Template
	 * @return
	 */
	public List<SyncTemplate> getFromSyncTemplate(){
		List<SyncTemplate> fromSyncTemplate = new ArrayList<SyncTemplate>();
		
		SyncConfig syncConfig = BaseLoaderCore.getInstance().getSyncConfig();
		List<String> syncDataSourceLoaderModelList = syncConfig.getSyncDataSourceLoaderModel();
		for (String model : syncDataSourceLoaderModelList) {
			if("prop".equalsIgnoreCase(model)){
				
			}else if("xml".equalsIgnoreCase(model)){
				DataSourceLoaderXml dataSourceLoaderXml = new DataSourceLoaderXml();
				DataSources dataSource = dataSourceLoaderXml.loadFileXml();
				List<SyncTemplate> syncTemplateList = connectConfigListConvertSyncTemplateList(dataSource.getFromTemplate().getConnectConfigList());
				fromSyncTemplate.addAll(syncTemplateList);
			}else if("web".equalsIgnoreCase(model)){
				
			}else if("other".equalsIgnoreCase(model)){
				
			}
		}
		
		return fromSyncTemplate;
	}
	
	/**
	 * 得到目标同步Template
	 * @return
	 */
	public List<SyncTemplate> getToSyncTemplate(){
		List<SyncTemplate> toSyncTemplate = new ArrayList<SyncTemplate>();
		
		SyncConfig syncConfig = BaseLoaderCore.getInstance().getSyncConfig();
		List<String> syncDataSourceLoaderModelList = syncConfig.getSyncDataSourceLoaderModel();
		for (String model : syncDataSourceLoaderModelList) {
			if("prop".equalsIgnoreCase(model)){
				
			}else if("xml".equalsIgnoreCase(model)){
				DataSourceLoaderXml dataSourceLoaderXml = new DataSourceLoaderXml();
				DataSources dataSource = dataSourceLoaderXml.loadFileXml();
				List<SyncTemplate> syncTemplateList = connectConfigListConvertSyncTemplateList(dataSource.getToTemplate().getConnectConfigList());
				toSyncTemplate.addAll(syncTemplateList);
				
			}else if("web".equalsIgnoreCase(model)){
				
			}else if("other".equalsIgnoreCase(model)){
				
			}
		}
		return toSyncTemplate;
	}
	
	/**
	 * 得到本地存储的Template/只能采用xml进行配置
	 * @return
	 */
	public SyncTemplate getNativeStoreTemplate(){
		DataSourceLoaderXml dataSourceLoaderXml = new DataSourceLoaderXml();
		DataSources dataSource = dataSourceLoaderXml.loadFileXml();
		ConnectConfig nativeTemplate = dataSource.getNativeTemplate();
		return getSyncTemplate(nativeTemplate);
	}
	
	/**
	 * 将ConnectConfigList转为SyncTemplateList
	 * @param connectConfigList
	 * @return
	 */
	private List<SyncTemplate> connectConfigListConvertSyncTemplateList(List<ConnectConfig> connectConfigList){
		List<SyncTemplate> syncTemplateList = new ArrayList<SyncTemplate>();
		for (ConnectConfig connectConfig : connectConfigList) {
			SyncTemplate syncTemplate = getSyncTemplate(connectConfig);
			syncTemplateList.add(syncTemplate);
		}
		return syncTemplateList;
	}
	
	public SyncTemplate getSyncTemplate(ConnectConfig connectConfig){
		SyncTemplate syncTemplate = new SyncTemplate();
		DataSource dataSource = getDataSource(connectConfig);
		DataSource driverManagerDataSource = getDriverManagerDataSource(connectConfig);
		syncTemplate.setConnectConfig(connectConfig);
		syncTemplate.setDataSource(dataSource);
		syncTemplate.setDriverManagerDataSource(driverManagerDataSource);
		
		Integer validationQueryTimeout = getPropertyOfInt("validationQueryTimeout", -1);
		syncTemplate.setQueryTimeout(validationQueryTimeout);
		return syncTemplate;
	}
	
	/**
	 * 设置数据源,这使用的是阿里巴巴的DruidDataSource,放弃使用spring的DriverManagerDataSource和c3p0
	 * @param connect
	 * @return
	 */
	public DataSource getDataSource(ConnectConfig connect){
		DBDruidDataSource dataSource = new DBDruidDataSource();
		try {
			dataSource.setUrl(connect.getConnectUrl());
			dataSource.setUsername(connect.getConnectUsername());
			dataSource.setPassword(connect.getConnectPassword());
//			dataSource.setDriverClassName(connect.getConnectDriver());
			
			//设置 DruidDataSource 的参数
			setDruidDataSourceParams(dataSource);
			
		} catch (Exception e) {
			//e.printStackTrace();
			LogCache.addDataSourceLog("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:"+e.getMessage());
			LoggerUtils.error("数据源名称:"+connect.getDataSourceName()+" , 数据源编号:"+connect.getDataSourceNumber()+" , 错误信息:"+e.getMessage());
		}
		return dataSource;
	}
	
	/**
	 * 设置 DruidDataSource 的参数
	 * @param dataSource 
	 */
	private void setDruidDataSourceParams(DBDruidDataSource dataSource){
		//设置这个Name是为了防止 DruidDataSource 抛出 
		if(getPropertyOfBoolean("druidName", false)){
			dataSource.setName(DataSourceLoader.getUUID());
		}
		dataSource.setMinIdle(getPropertyOfInt("minIdle", 1));
		dataSource.setMaxActive(getPropertyOfInt("maxActive", 200));
		dataSource.setInitialSize(getPropertyOfInt("initialSize", 1));
		dataSource.setMaxWait(getPropertyOfLong("maxWait", 60000L)); //默认1分钟
		dataSource.setPoolPreparedStatements(getPropertyOfBoolean("poolPreparedStatements", false));
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(getPropertyOfInt("maxPoolPreparedStatementPerConnectionSize", -1));
		dataSource.setValidationQuery(getPropertyOfStr("validationQuery", null));
		dataSource.setValidationQueryTimeout(getPropertyOfInt("validationQueryTimeout", -1));
		dataSource.setTestOnBorrow(getPropertyOfBoolean("testOnBorrow", true));
		dataSource.setTestOnReturn(getPropertyOfBoolean("testOnReturn", false));
		dataSource.setTestWhileIdle(getPropertyOfBoolean("testWhileIdle", true));
		dataSource.setTimeBetweenEvictionRunsMillis(getPropertyOfLong("timeBetweenEvictionRunsMillis", 60000L));
		dataSource.setMinEvictableIdleTimeMillis(getPropertyOfLong("minEvictableIdleTimeMillis", 600000L));
		try {
			dataSource.setFilters(getPropertyOfStr("filters", null));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 得到DriverManagerDataSource ,用它主要是用来测试是否能够连接成功
	 * @param connect
	 * @return
	 */
	public DataSource getDriverManagerDataSource(ConnectConfig connect){
		DriverManagerDataSource source = new DriverManagerDataSource();
		source.setDriverClassName(connect.getConnectDriver());
		source.setUrl(connect.getConnectUrl());
		source.setUsername(connect.getConnectUsername());
		source.setPassword(connect.getConnectPassword());
		return source;
	}
	
	/**
	 * 
	 * 得到propertyOfBoolean
	 * @return
	 */
	private Boolean getPropertyOfBoolean(String fieldName,Boolean defaultValue){
		String checkoutTimeoutString = SyncPropertiesUtil.getProperty(fieldName);
		Boolean value = defaultValue;
		if(StringUtils.isNotEmpty(checkoutTimeoutString)){
			value = Boolean.parseBoolean(checkoutTimeoutString);
		}
		return value;
	}
	
	/**
	 * 
	 * 得到propertyOfLong
	 * @return
	 */
	private Long getPropertyOfLong(String fieldName,Long defaultValue){
		String checkoutTimeoutString = SyncPropertiesUtil.getProperty(fieldName);
		Long value = defaultValue;
		if(StringUtils.isNotEmpty(checkoutTimeoutString)){
			value = Long.parseLong(checkoutTimeoutString);
		}
		return value;
	}
	
	/**
	 * 得到propertyOfInt
	 * @return
	 */
	private Integer getPropertyOfInt(String fieldName,Integer defaultValue){
		String checkoutTimeoutString = SyncPropertiesUtil.getProperty(fieldName);
		Integer value = defaultValue;
		if(StringUtils.isNotEmpty(checkoutTimeoutString)){
			value = Integer.parseInt(checkoutTimeoutString);
		}
		return value;
	}
	
	/**
	 * 得到propertyOfStr
	 * @return
	 */
	private String getPropertyOfStr(String fieldName,String defaultValue){
		String checkoutTimeoutString = SyncPropertiesUtil.getProperty(fieldName);
		String value = defaultValue;
		if(StringUtils.isNotEmpty(checkoutTimeoutString)){
			value = checkoutTimeoutString;
		}
		return value;
	}
	
	/**
	 * 得到UUID
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().toUpperCase().replace("-", "");
	}
	
	/**
	 * 得到单个是实例对象(单例)
	 * @return
	 */
	public static DataSourceLoader getInstance(){
		if(dataSourceLoader == null){
			dataSourceLoader = new DataSourceLoader();
		}
		return dataSourceLoader;
	}
	

}
