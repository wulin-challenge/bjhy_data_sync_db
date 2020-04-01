package com.bjhy.data.sync.db.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.api.domain.SingleTableEntity;
import com.bjhy.data.sync.db.core.BaseCore;

/**
 * 对单表的insert,update,delete的通用api,使用方式代码侵入式
 * @author wulin
 *
 */
public class BaseSingleTableApi {
	/**
	 * 存储表的完整列
	 */
	private static final ConcurrentHashMap<String,List<String>> tableColumn = new ConcurrentHashMap<String,List<String>>();
	
	private Logger logger = Logger.getLogger(BaseCore.class);
	
	public void entry(SingleTableEntity singleTableEntity) {
		toUpperCase(singleTableEntity);
		buildSQl(singleTableEntity);
		
		execute(singleTableEntity);
	}
	
	private void execute(SingleTableEntity singleTableEntity) {
		if(!checkExecute(singleTableEntity)) {
			return;
		}
		
		NamedParameterJdbcTemplate namedToTemplate = singleTableEntity.getNamedToTemplate();
		String sql = singleTableEntity.getSql();
		Map<String, Object> rowParam = singleTableEntity.getRowParam();
		namedToTemplate.update(sql, rowParam);
	}
	
	/**
	 * 检测成功返回true,否则返回false
	 * @param singleTableEntity
	 * @return
	 */
	private boolean checkExecute(SingleTableEntity singleTableEntity) {
		NamedParameterJdbcTemplate namedToTemplate = singleTableEntity.getNamedToTemplate();
		String checkSql = singleTableEntity.getCheckSql();
		Map<String, Object> rowParam = singleTableEntity.getRowParam();
		Integer count = namedToTemplate.queryForObject(checkSql, rowParam, Integer.class);
		
		String ddlType = singleTableEntity.getDdlType();
		if(SingleTableEntity.DDL_INSERT.equals(ddlType) && count == 0) {
			
			return true;
		}else if(SingleTableEntity.DDL_UPDATE.equals(ddlType) && count > 0){
			
			return true;
		}else if(SingleTableEntity.DDL_DELETE.equals(ddlType) && count > 0){
			
			return true;
		}
		
		String primaryKey = singleTableEntity.getPrimaryKey();
		Object primaryValue = singleTableEntity.getRowParam().get(primaryKey);
		logger.warn("当前"+ddlType+" 将不执行,主键为 "+primaryKey+"="+primaryValue);
		return false;
	}
	

	private void toUpperCase(SingleTableEntity singleTableEntity) {
		//将行参数转大写
		singleTableEntity.setRowParam(rowParamToUpperCase(singleTableEntity.getRowParam()));
		String primaryKey = singleTableEntity.getPrimaryKey().toUpperCase();
		singleTableEntity.setPrimaryKey(primaryKey);
	}
	
	private void buildSQl(SingleTableEntity singleTableEntity) {
		List<String> tableColumns = getTableColumns(singleTableEntity);
		String ddlType = singleTableEntity.getDdlType();
		
		if(singleTableEntity.getSql() == null) {
			if(SingleTableEntity.DDL_INSERT.equals(ddlType)) {
				
				buildInsertSql(singleTableEntity, tableColumns);
			}else if(SingleTableEntity.DDL_UPDATE.equals(ddlType)){
				
				buildUpdateSql(singleTableEntity, tableColumns);
			}else if(SingleTableEntity.DDL_DELETE.equals(ddlType)){
				
				buildDeleteSql(singleTableEntity, tableColumns);
			}else {
				throw new RuntimeException("不支持该ddl类型");
			}
		}
		
		if(singleTableEntity.getCheckSql() == null) {
			buildCheckSql(singleTableEntity, tableColumns);
		}
	}
	
	private void buildInsertSql(SingleTableEntity singleTableEntity,List<String> tableColumns) {
		StringBuilder noExistColumns = new StringBuilder();
		
		Set<String> keySet = singleTableEntity.getRowParam().keySet();
		String toTableName = singleTableEntity.getToTableName();
		
		StringBuilder insertTable = new StringBuilder("insert into "+toTableName+"(");
		StringBuilder insertValues = new StringBuilder(") values( ");
		
		boolean isFirst = true;
		for (String column : keySet) {
			
			if(!tableColumns.contains(column)) {
				noExistColumns.append(column);
				continue;
			}
			
			if(isFirst) {
				isFirst = false;
				
				insertTable.append(column);
				insertValues.append(":"+column);
				
			}else {
				insertTable.append(","+column);
				insertValues.append(",:"+column);
			}
		}
		insertValues.append(")");
		
		if(noExistColumns.length()>0) {
			logger.warn("insert: 这些列"+noExistColumns+"在 "+toTableName+" 中不存在!");
		}
		
		String insertSql = insertTable.toString()+insertValues.toString();
		singleTableEntity.setSql(insertSql);
	}
	
	private void buildUpdateSql(SingleTableEntity singleTableEntity,List<String> tableColumns) {
		StringBuilder noExistColumns = new StringBuilder();
		
		String primaryKey = singleTableEntity.getPrimaryKey();
		Set<String> keySet = singleTableEntity.getRowParam().keySet();
		String toTableName = singleTableEntity.getToTableName();
		
		StringBuilder updateTable = new StringBuilder("update "+toTableName+" set ");
		StringBuilder updateWhere = new StringBuilder(" where "+primaryKey+"=:"+primaryKey);
		
		boolean isFirst = true;
		for (String column : keySet) {
			
			if(!tableColumns.contains(column)) {
				noExistColumns.append(column);
				continue;
			}
			
			if(isFirst) {
				isFirst = false;
				
				updateTable.append(column+"=:"+column);
				
			}else {
				updateTable.append(","+column+"=:"+column);
			}
		}
		
		if(noExistColumns.length()>0) {
			logger.warn("update: 这些列"+noExistColumns+"在 "+toTableName+" 中不存在!");
		}
		
		String updateSql = updateTable.toString()+updateWhere.toString();
		singleTableEntity.setSql(updateSql);
	}
	
	private void buildCheckSql(SingleTableEntity singleTableEntity,List<String> tableColumns) {
		String primaryKey = singleTableEntity.getPrimaryKey();
		String toTableName = singleTableEntity.getToTableName();
		
		StringBuilder checkSql = new StringBuilder("select count(1) from  "+toTableName+" where "+primaryKey+"=:"+primaryKey);
		singleTableEntity.setCheckSql(checkSql.toString());
	}
	
	private void buildDeleteSql(SingleTableEntity singleTableEntity,List<String> tableColumns) {
		String primaryKey = singleTableEntity.getPrimaryKey();
		String toTableName = singleTableEntity.getToTableName();
		
		StringBuilder deleteSql = new StringBuilder("delete from  "+toTableName+" where "+primaryKey+"=:"+primaryKey);
		singleTableEntity.setSql(deleteSql.toString());
	}
	
	/**
	 * 获取表的完整的
	 * @param singleTableEntity
	 * @return
	 */
	public List<String> getTableColumns(SingleTableEntity singleTableEntity){
		synchronized(tableColumn) {
			return getTableColumns2(singleTableEntity);
		}
	}
	
	/**
	 * 获取表的完整的
	 * @param singleTableEntity
	 * @return
	 */
	public List<String> getTableColumns2(SingleTableEntity singleTableEntity){
		String toTableName = singleTableEntity.getToTableName();
		List<String> columns = tableColumn.get(toTableName);
		
		if(columns != null && columns.size()>0) {
			return columns;
		}
		
		DataSource dataSource = singleTableEntity.getToTemplate().getDataSource();
		try (Connection connection = dataSource.getConnection()){
			columns = getColumnNames(singleTableEntity, connection, toTableName, null);
			tableColumn.put(toTableName, columns);
		} catch (Exception e) {
			logger.error("获取 "+toTableName+" 表的列失败!");
			throw new RuntimeException("获取 "+toTableName+" 表的列失败!",e);
		}
		return columns;
	}
	
	/**
	 * 将行参数转大写
	 * @param syncLogicEntity
	 * @param rowParam
	 * @return
	 */
	private Map<String,Object> rowParamToUpperCase(Map<String, Object> rowParam){
		Map<String,Object> newRowParams = new LinkedHashMap<String,Object>();
		Set<Entry<String, Object>> entrySet = rowParam.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			newRowParams.put(entry.getKey().toUpperCase(), entry.getValue());
		}
		return newRowParams;
	}
	
	/**
	 * 得到对应数据表的列名
	 * @param conn
	 * @param tableName
	 * @return
	 */
	public List<String> getColumnNames(SingleTableEntity singleTableEntity,Connection conn, String tableName,String sql){
		String selectSql = "SELECT * FROM " + tableName;
		if(sql != null){
			selectSql = sql;
		}
		
		selectSql = "select * from ("+selectSql+") a where 1=2";
		List<String> columnNames = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(selectSql);
				ResultSet rs = pst.executeQuery();){
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				columnNames.add(rsmd.getColumnLabel(i).toUpperCase());
			}
		} catch (SQLException e) {
			
			String dataSourceName = singleTableEntity.getToTemplate().getConnectConfig().getDataSourceName();
			String dataSourceNumber = singleTableEntity.getToTemplate().getConnectConfig().getDataSourceNumber();
			String toTableName = singleTableEntity.getToTableName();
			
			logger.error("执行的语句是:"+selectSql+"表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber+" , 错误信息  : "+e.getMessage());
			throw new RuntimeException(e);
		}
		return columnNames;
	}
}
