package com.bjhy.data.sync.db.domain;

/**
 * 数据源连接配置信息
 * @author wubo
 *
 */
public class ConnectConfig{
	
	/**
	 * 数据源排序号
	 */
	private Integer sortNumber = 0;
	
	/**
	 * 数据源隶属于那个任务
	 */
	private String task;
	
	/**
	 * 是否启用
	 */
	private Boolean isEnable;
	
	/**
	 * 数据源的方向:from/to
	 */
	private String dataSourceDirection;
	
	/**
	 * 所用数据库类型
	 */
	private String databaseType;
	
	/**
	 * 数据源的名称
	 */
	private String dataSourceName;
	
	/**
	 * 数据源的编号
	 */
	private String dataSourceNumber;
	
	/**
	 * 连接驱动
	 */
	private String connectDriver;
	
	/**
	 * 连接url
	 */
	private String connectUrl;
	
	/**
	 * 连接用户名
	 */
	private String connectUsername;
	
	/**
	 * 连接密码
	 */
	private String connectPassword;
	
	/**
	 * 连接方言
	 */
	private String connectDialect;

	public Integer getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(Integer sortNumber) {
		this.sortNumber = sortNumber;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getDataSourceNumber() {
		return dataSourceNumber;
	}

	public void setDataSourceNumber(String dataSourceNumber) {
		this.dataSourceNumber = dataSourceNumber;
	}

	public String getConnectDriver() {
		return connectDriver;
	}

	public void setConnectDriver(String connectDriver) {
		this.connectDriver = connectDriver;
	}

	public String getConnectUrl() {
		return connectUrl;
	}

	public void setConnectUrl(String connectUrl) {
		this.connectUrl = connectUrl;
	}

	public String getConnectUsername() {
		return connectUsername;
	}

	public void setConnectUsername(String connectUsername) {
		this.connectUsername = connectUsername;
	}

	public String getConnectPassword() {
		return connectPassword;
	}

	public void setConnectPassword(String connectPassword) {
		this.connectPassword = connectPassword;
	}

	public String getConnectDialect() {
		return connectDialect;
	}

	public void setConnectDialect(String connectDialect) {
		this.connectDialect = connectDialect;
	}

	public String getDataSourceDirection() {
		return dataSourceDirection;
	}

	public void setDataSourceDirection(String dataSourceDirection) {
		this.dataSourceDirection = dataSourceDirection;
	}

}
