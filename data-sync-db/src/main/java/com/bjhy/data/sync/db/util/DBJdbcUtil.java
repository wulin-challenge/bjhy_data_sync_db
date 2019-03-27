package com.bjhy.data.sync.db.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 重新Druid的JdbcUtils
 * @author wubo
 *
 */
public class DBJdbcUtil {
	private static Logger logger = Logger.getLogger(DBJdbcUtil.class);

	public static void close(Statement x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
        	logger.debug("close statement error", e);
        }
    }

    public static void close(ResultSet x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
        	logger.debug("close result set error", e);
        }
    }
    
    public static void close(Connection x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
        	ConnectionMetaData connectionMetaData = getConnectionMetaData(x);
        	String userName = connectionMetaData.userName();
        	String database = connectionMetaData.database();
        	logger.error("关闭连接错误 : userName:"+userName+",database:"+database+",详细信息"+e.getMessage());
        }
    }
    
    /**
     * 得到连接元数据
     * @return
     */
    private static ConnectionMetaData getConnectionMetaData(Connection x){
    	
    	if(x == null) return new DefaultConnectionMetaData();
    	
    	Set<ConnectionMetaData> cmdRegisterList = new HashSet<ConnectionMetaData>();
    	cmdRegisterList.add(new OracleConnectionMetaData(x));
    	
    	for (ConnectionMetaData connectionMetaData : cmdRegisterList) {
			if(x != null && x.getClass().getName().equals(connectionMetaData.implClassName())){
				return connectionMetaData;
			}
		}
    	return new DefaultConnectionMetaData();
    }
    
    /**
     * 连接元数据
     * @author wubo
     *
     */
    private static interface ConnectionMetaData{
    	
    	/**
    	 * 用户名
    	 * @return
    	 */
    	String userName();
    	
    	/**
    	 * 数据库
    	 * @return
    	 */
    	String database();
    	
    	/**
    	 * 实现类名称
    	 * @return
    	 */
    	String implClassName();
    }
    
    /**
     * Oracle的连接元数据实现类
     * @author wubo
     */
    private static class OracleConnectionMetaData implements ConnectionMetaData{
    	private String userName;
    	private String database;
    	private Connection x;
    	
		public OracleConnectionMetaData(Connection x) {
			this.x = x;
		}

		@Override
		public String userName() {
			if(this.userName == null){
				userName = (String) ReflectUtil.getFieldValue(x,x.getClass().getSuperclass(), "userName");
			}
			return userName;
		}

		@Override
		public String database() {
			if(this.database == null){
				database = (String) ReflectUtil.getFieldValue(x,x.getClass().getSuperclass(), "database");
			}
			return database;
		}

		@Override
		public String implClassName() {
			return "oracle.jdbc.driver.T4CConnection";
		}
    }
    
    /**
     * 默认的连接元数据实现类
     * @author wubo
     */
    private static class DefaultConnectionMetaData implements ConnectionMetaData{

		@Override
		public String userName() {
			return "";
		}

		@Override
		public String database() {
			return "";
		}

		@Override
		public String implClassName() {
			return "";
		}
    }
}
