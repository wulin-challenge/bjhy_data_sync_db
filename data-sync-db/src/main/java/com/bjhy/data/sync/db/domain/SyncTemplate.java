package com.bjhy.data.sync.db.domain;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 同步的template
 * @author wubo
 *
 */
public class SyncTemplate  extends JdbcTemplate{
	/**
	 * 配置的连接信息
	 */
	private ConnectConfig connectConfig;
	
	/**
	 * 数据源连接信息
	 */
	private DataSource dataSource;
	
	/**
	 * driverManagerDataSource类型的数据源,主要是用于测试是否能够连接成功!!
	 */
	private DataSource driverManagerDataSource;

	public ConnectConfig getConnectConfig() {
		return connectConfig;
	}

	public void setConnectConfig(ConnectConfig connectConfig) {
		this.connectConfig = connectConfig;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDriverManagerDataSource() {
		return driverManagerDataSource;
	}

	public void setDriverManagerDataSource(DataSource driverManagerDataSource) {
		this.driverManagerDataSource = driverManagerDataSource;
	}
}
