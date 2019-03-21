package com.bjhy.data.sync.db.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

/**
 * 继承DruidDataSource,扩展一些自定义逻辑
 * @author wubo
 *
 */
public class DBDruidDataSource extends DruidDataSource {
	private final static Log LOG = LogFactory.getLog(DruidDataSource.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * 最大重试次数
	 */
	private ThreadLocal<AtomicInteger> maxRetryNumber = new ThreadLocal<AtomicInteger>(){
		@Override
		protected AtomicInteger initialValue() {
			return new AtomicInteger(2);
		}
		
	};

	/**
	 * 得到一个测试连接
	 * @return
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public Connection getTestConnection() throws SQLException {
		//最后一次调用的异常
		Throwable lastException = null;
		//重试次数
		int retryNumber = 3;
		for (int i = 0; i < retryNumber; i++) {
			try {
				Connection connection = getTempConnection();
				if(connection.isClosed()){
					throw new SQLException("连接连接已经被关闭,username:"+username+",url:"+jdbcUrl);
				}
				validationConnection(connection);
				return connection;
			} catch (Throwable e1) {
				lastException = e1;
			}
		}
		throw new SQLException("连接重连3次都失败,username:"+username+",url:"+jdbcUrl+",具体原因:"+lastException.getMessage(),lastException);
	}

	/**
	 * 得到连接
	 */
	@Override
	public DruidPooledConnection getConnection() throws SQLException {
		DruidPooledConnection connection = super.getConnection();
		return connection;
	}

	/**
	 * 在给定时间范围内得到连接
	 * @param maxWaitMillis 得到连接的最大等待时间
	 */
	@Override
	public DruidPooledConnection getConnection(long maxWaitMillis) throws SQLException {
		DruidPooledConnection connection = null;
		try {
			connection = super.getConnection(maxWaitMillis);
			if(connection.isClosed()){
				throw new SQLException("连接连接已经被关闭,username:"+username+",url:"+jdbcUrl);
			}
			validationConnection(connection);
		} catch (Exception e) {
			int decrementAndGet = maxRetryNumber.get().decrementAndGet();
			if(decrementAndGet<0){
				throw new SQLException("连接重连3次都失败,username:"+username+",url:"+jdbcUrl,e);
			}else{
				connection = getConnection(maxWaitMillis);
			}
		}
		return connection;
	}
	
	/**
	 * 通过用户名和密码得连接
	 */
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return super.getConnection(username, password);
	}

	/**
	 * 创建物理连接
	 */
	@Override
	public Connection createPhysicalConnection(String url, Properties info) throws SQLException {
		Connection connection =null;
		try {
			connection = super.createPhysicalConnection(url, info);
		} catch (Exception e) {
			LOG.error("创建物理连接失败!,username:"+username+",url:"+jdbcUrl+","+e.getMessage());
		}
		return connection;
	}

	/**
	 * 创建物理连接
	 */
	@Override
	public PhysicalConnectionInfo createPhysicalConnection() throws SQLException {
		return super.createPhysicalConnection();
	}
	
	/**
	 * 验证连接
	 * @param connection
	 * @throws SQLException
	 */
	private void validationConnection(Connection connection) throws SQLException {
		//默认添加自动测试连接是否可用测试
		if(validationQuery != null && validationQueryTimeout>0){
			Statement createStatement = connection.createStatement();
			createStatement.setQueryTimeout(validationQueryTimeout);
			createStatement.executeQuery(validationQuery);
		}
	}
	
	/**
	 * 得到一个临时连接
	 * @return
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	private Connection getTempConnection() throws SQLException,InterruptedException{
		final ReentrantLock lock = this.lock;
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException e) {
			throw new SQLException("interrupt", e);
		}

		try {

			this.id = DruidDriver.createDataSourceId();

			if (this.jdbcUrl != null) {
				this.jdbcUrl = this.jdbcUrl.trim();
			}

			for (Filter filter : filters) {
				filter.init(this);
			}

			if (this.dbType == null || this.dbType.length() == 0) {
				this.dbType = JdbcUtils.getDbType(jdbcUrl, null);
			}

			if (JdbcConstants.MYSQL.equals(this.dbType) || JdbcConstants.MARIADB.equals(this.dbType)
					|| JdbcConstants.ALIYUN_ADS.equals(this.dbType)) {
				boolean cacheServerConfigurationSet = false;
				if (this.connectProperties.containsKey("cacheServerConfiguration")) {
					cacheServerConfigurationSet = true;
				} else if (this.jdbcUrl.indexOf("cacheServerConfiguration") != -1) {
					cacheServerConfigurationSet = true;
				}
				if (cacheServerConfigurationSet) {
					this.connectProperties.put("cacheServerConfiguration", "true");
				}
			}

			if (maxActive <= 0) {
				throw new IllegalArgumentException("illegal maxActive " + maxActive);
			}

			if (maxActive < minIdle) {
				throw new IllegalArgumentException("illegal maxActive " + maxActive);
			}

			if (getInitialSize() > maxActive) {
				throw new IllegalArgumentException(
						"illegal initialSize " + this.initialSize + ", maxActive " + maxActive);
			}

			if (maxEvictableIdleTimeMillis < minEvictableIdleTimeMillis) {
				throw new SQLException("maxEvictableIdleTimeMillis must be grater than minEvictableIdleTimeMillis");
			}

			if (this.driverClass != null) {
				this.driverClass = driverClass.trim();
			}

			if (this.driver == null) {
				if (this.driverClass == null || this.driverClass.isEmpty()) {
					this.driverClass = JdbcUtils.getDriverClassName(this.jdbcUrl);
				}

				if (MockDriver.class.getName().equals(driverClass)) {
					driver = MockDriver.instance;
				} else {
					if (jdbcUrl == null && (driverClass == null || driverClass.length() == 0)) {
						throw new SQLException("url not set");
					}
					driver = JdbcUtils.createDriver(driverClassLoader, driverClass);
				}
			} else {
				if (this.driverClass == null) {
					this.driverClass = driver.getClass().getName();
				}
			}

			initCheck();

			if (isUseGlobalDataSourceStat()) {
				dataSourceStat = JdbcDataSourceStat.getGlobal();
				if (dataSourceStat == null) {
					dataSourceStat = new JdbcDataSourceStat("Global", "Global", this.dbType);
					JdbcDataSourceStat.setGlobal(dataSourceStat);
				}
				if (dataSourceStat.getDbType() == null) {
					dataSourceStat.setDbType(this.dbType);
				}
			} else {
				dataSourceStat = new JdbcDataSourceStat(this.name, this.jdbcUrl, this.dbType, this.connectProperties);
			}
			
			PhysicalConnectionInfo pyConnectInfo = createPhysicalConnection();
			return pyConnectInfo.getPhysicalConnection();
		} catch (SQLException e) {
			throw e;
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}
}