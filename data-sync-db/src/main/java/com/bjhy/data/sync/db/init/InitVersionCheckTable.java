package com.bjhy.data.sync.db.init;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 初始化版本检查表
 * @author wubo
 *
 */
public class InitVersionCheckTable {
	
	/**
	 * 本地存储的jdbcTemplate(暂时没有用)
	 */
	@SuppressWarnings("unused")
	private NamedParameterJdbcTemplate namedNativeStoreTemplate;
	
	/**
	 * 原生的本地的template
	 */
	private SyncTemplate nativeStoreTemplate;

	/**
	 * 初始化版本检查表
	 * @param nativeStoreTemplate
	 */
	public InitVersionCheckTable(SyncTemplate nativeStoreTemplate) {
		this.nativeStoreTemplate = nativeStoreTemplate;
		this.namedNativeStoreTemplate  = new NamedParameterJdbcTemplate(nativeStoreTemplate);  
		initCheckTable();//初始化检查表
	}
	
	/**
	 * 初始化检查表
	 */
	private void initCheckTable(){
		String databaseType = nativeStoreTemplate.getConnectConfig().getDatabaseType();
		
		if("oracle".equalsIgnoreCase(databaseType)){
			createSyncCheckTable(getOracleCreateSql());
			
		}else if("sqlServer".equalsIgnoreCase(databaseType)){
			createSyncCheckTable(getSqlServerCreateSql());
			
		}else if("mysql".equalsIgnoreCase(databaseType)){
			createSyncCheckTable(getMySqlCreateSql());
			
		}else if("dm".equalsIgnoreCase(databaseType)){
			createSyncCheckTable(getDmCreateSql());
			
			
		}
	}
	
	/**
	 * 创建同步检测表
	 */
	private void createSyncCheckTable(String createTableSql){
		Statement stmt = null;
		Connection conn = null;
		try {
			conn = nativeStoreTemplate.getDataSource().getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(createTableSql);
		} catch (SQLException e) {
			LoggerUtils.info("version_check 表已经存在!"+e.getMessage());
		}finally{
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 得到SqlServer创建语句
	 * @return
	 */
	private String getSqlServerCreateSql(){
		String createTableSql = "create table version_check("
			    + " id varchar(255), " //主键Id
			    + " fromDataSourceName varchar(255), " //来源 数据源名称
			    + " fromDataSourceNumber varchar(255), " //来源 数据源编号
			    + " toDataSourceName varchar(255), " //目标 数据源名称
			    + " toDataSourceNumber varchar(255), " //目标 数据源编号
			    + " fromTask varchar(255), " //来源数据源的任务
			    + " toTask varchar(255), " //目标数据源的任务
			    + " toTableName varchar(255), " //目标表名
			    + " stepUniquelyIdentifies varchar(255), " //步骤唯一标示
			    + " currentCheckVersion varchar(255), "  //当前检测列的值
			    + " beforeCheckVersion varchar(2550), " //上N次检测列的值
			    + " PRIMARY KEY (id)" //添加主键Id
			    + " )";
		
		return createTableSql;
	}
	
	/**
	 * 得到Oracle的创建语句
	 * @return
	 */
	private String getOracleCreateSql(){
		String createTableSql = "create table version_check("
			    + " id VARCHAR2(255) primary key not null, " //主键Id
			    + " fromDataSourceName varchar(255), " //来源 数据源名称
			    + " fromDataSourceNumber varchar(255), " //来源 数据源编号
			    + " toDataSourceName varchar(255), " //目标 数据源名称
			    + " toDataSourceNumber varchar(255), " //目标 数据源编号
			    + " fromTask varchar(255), " //来源数据源的任务
			    + " toTask varchar(255), " //目标数据源的任务
			    + " toTableName varchar2(255), " //目标表名
			    + " stepUniquelyIdentifies varchar(255), " //步骤唯一标示
			    + " currentCheckVersion varchar2(255), "  //当前检测列的版本
			    + " beforeCheckVersion varchar2(2550) " //上N次检测列的版本
			    + " )";
		return createTableSql;
	}
	
	/**
	 * 得到Dm的创建语句
	 * @return
	 */
	private String getDmCreateSql(){
		String createTableSql = "create table version_check("
				+ " id VARCHAR2(255) primary key not null, " //主键Id
				+ " fromDataSourceName varchar(255), " //来源 数据源名称
				+ " fromDataSourceNumber varchar(255), " //来源 数据源编号
				+ " toDataSourceName varchar(255), " //目标 数据源名称
				+ " toDataSourceNumber varchar(255), " //目标 数据源编号
				+ " fromTask varchar(255), " //来源数据源的任务
				+ " toTask varchar(255), " //目标数据源的任务
				+ " toTableName varchar2(255), " //目标表名
				+ " stepUniquelyIdentifies varchar(255), " //步骤唯一标示
				+ " currentCheckVersion varchar2(255), "  //当前检测列的版本
				+ " beforeCheckVersion varchar2(2550) " //上N次检测列的版本
				+ " )";
		return createTableSql;
	}
	
	/**
	 * 得到Mysql的创建语句
	 * @return
	 */
	private String getMySqlCreateSql(){
		String createTableSql = "create table version_check("
			    + " id VARCHAR(255) primary key not null, " //主键Id
			    + " fromDataSourceName varchar(255), " //来源 数据源名称
			    + " fromDataSourceNumber varchar(255), " //来源 数据源编号
			    + " toDataSourceName varchar(255), " //目标 数据源名称
			    + " toDataSourceNumber varchar(255), " //目标 数据源编号
			    + " fromTask varchar(255), " //来源数据源的任务
			    + " toTask varchar(255), " //目标数据源的任务
			    + " toTableName varchar(255), " //目标表名
			    + " stepUniquelyIdentifies varchar(255), " //步骤唯一标示
			    + " currentCheckVersion varchar(255), "  //当前检测列的版本
			    + " beforeCheckVersion varchar(2550) " //上N次检测列的版本
			    + " )";
		return createTableSql;
	}
	
	
}
