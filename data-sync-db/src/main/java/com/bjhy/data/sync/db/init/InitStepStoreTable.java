package com.bjhy.data.sync.db.init;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.alibaba.druid.util.JdbcUtils;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 初始化步骤存储表
 * @author wubo
 *
 */
public class InitStepStoreTable {

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
	 * 初始化步骤存储表
	 * @param nativeStoreTemplate
	 */
	public InitStepStoreTable(SyncTemplate nativeStoreTemplate) {
		this.nativeStoreTemplate = nativeStoreTemplate;
		this.namedNativeStoreTemplate  = new NamedParameterJdbcTemplate(nativeStoreTemplate);  
		initStepStoreTable();//初始化检查表
	}
	
	/**
	 * 初始化检查表
	 */
	private void initStepStoreTable(){
		String databaseType = nativeStoreTemplate.getConnectConfig().getDatabaseType();
		
		if("oracle".equalsIgnoreCase(databaseType)){
			createSyncStepStoreTable(getOracleCreateSql());
			
		}else if("sqlServer".equalsIgnoreCase(databaseType)){
			createSyncStepStoreTable(getSqlServerCreateSql());
			
		}else if("mysql".equalsIgnoreCase(databaseType)){
			createSyncStepStoreTable(getMySqlCreateSql());
			
		}else if("dm".equalsIgnoreCase(databaseType)){
			createSyncStepStoreTable(getDmCreateSql());
			
			
		}
	}
	
	/**
	 * 创建同步检测表
	 */
	private void createSyncStepStoreTable(String createTableSql){
		try (Connection connection = nativeStoreTemplate.getDataSource().getConnection();){
			ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), null, "step_store", null);
			if(tables ==null || !tables.next()) {
				JdbcUtils.execute(connection, createTableSql);
				LoggerUtils.info("初始化表成功,表名称:  step_store");
			}
		} catch (SQLException e) {
			LoggerUtils.error("创建  step_store 失败!,"+e.getMessage());
		}
	}
	
	/**
	 * 得到SqlServer创建语句
	 * @return
	 */
	private String getSqlServerCreateSql(){
		String createTableSql = "create table step_store("
			    + " id varchar(255), " //主键Id
			    + " isRepairSync varchar(10), " //是否修复同步判断
			    + " fromDataSourceName varchar(255), " //来源 数据源名称
			    + " fromDataSourceNumber varchar(255), " //来源 数据源编号
			    + " toDataSourceName varchar(255), " //目标 数据源名称
			    + " toDataSourceNumber varchar(255), " //目标 数据源编号
			    + " fromTask varchar(255), " //来源数据源的任务
			    + " toTask varchar(255), " //目标数据源的任务
			    + " toTableName varchar(255), " //目标表名
			    + " stepUniquelyIdentifies varchar(255), " //步骤唯一标示
			    + " fromDataNumber bigint(15), "  //来源数据数量
			    + " toDataNumber bigint(15), " //目标数据数量
			    + " singleStepByte binary, " //目标数据数量
			    + " PRIMARY KEY (id)" //添加主键Id
			    + " )";
		
		return createTableSql;
	}
	
	/**
	 * 得到Oracle的创建语句
	 * @return
	 */
	private String getOracleCreateSql(){
		String createTableSql = "create table step_store("
			    + " id VARCHAR2(255) primary key not null, " //主键Id
			    + " isRepairSync varchar(10), " //是否修复同步判断
			    + " fromDataSourceName varchar(255), " //来源 数据源名称
			    + " fromDataSourceNumber varchar(255), " //来源 数据源编号
			    + " toDataSourceName varchar(255), " //目标 数据源名称
			    + " toDataSourceNumber varchar(255), " //目标 数据源编号
			    + " fromTask varchar(255), " //来源数据源的任务
			    + " toTask varchar(255), " //目标数据源的任务
			    + " toTableName varchar2(255), " //目标表名
			    + " stepUniquelyIdentifies varchar(255), " //步骤唯一标示
			    + " fromDataNumber number(15), "  //来源数据数量
			    + " toDataNumber number(15), " //目标数据数量
			    + " singleStepByte BLOB " //目标数据数量
			    + " )";
		return createTableSql;
	}
	
	/**
	 * 得到Dm的创建语句
	 * @return
	 */
	private String getDmCreateSql(){
		String createTableSql = "create table step_store("
				+ " id VARCHAR2(255) primary key not null, " //主键Id
				+ " isRepairSync varchar(10), " //是否修复同步判断
				+ " fromDataSourceName varchar(255), " //来源 数据源名称
				+ " fromDataSourceNumber varchar(255), " //来源 数据源编号
				+ " toDataSourceName varchar(255), " //目标 数据源名称
				+ " toDataSourceNumber varchar(255), " //目标 数据源编号
				+ " fromTask varchar(255), " //来源数据源的任务
				+ " toTask varchar(255), " //目标数据源的任务
				+ " toTableName varchar2(255), " //目标表名
				+ " stepUniquelyIdentifies varchar(255), " //步骤唯一标示
				+ " fromDataNumber number(15), "  //来源数据数量
			    + " toDataNumber number(15), " //目标数据数量
			    + " singleStepByte BLOB " //目标数据数量
				+ " )";
		return createTableSql;
	}
	
	/**
	 * 得到Mysql的创建语句
	 * @return
	 */
	private String getMySqlCreateSql(){
		String createTableSql = "create table step_store("
			    + " id VARCHAR(255) primary key not null, " //主键Id
			    + " isRepairSync varchar(10), " //是否修复同步判断
			    + " fromDataSourceName varchar(255), " //来源 数据源名称
			    + " fromDataSourceNumber varchar(255), " //来源 数据源编号
			    + " toDataSourceName varchar(255), " //目标 数据源名称
			    + " toDataSourceNumber varchar(255), " //目标 数据源编号
			    + " fromTask varchar(255), " //来源数据源的任务
			    + " toTask varchar(255), " //目标数据源的任务
			    + " toTableName varchar(255), " //目标表名
			    + " stepUniquelyIdentifies varchar(255), " //步骤唯一标示
			    + " fromDataNumber int(15), "  //来源数据数量
			    + " toDataNumber int(15), " //目标数据数量
			    + " singleStepByte longblob " //目标数据数量
			    + " )";
		return createTableSql;
	}
}
