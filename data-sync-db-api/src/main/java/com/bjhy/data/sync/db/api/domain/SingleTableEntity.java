package com.bjhy.data.sync.db.api.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.domain.SyncTemplate;

/**
 * 单表配置的entity
 * @author wulin
 *
 */
public class SingleTableEntity {
	public static final String DDL_INSERT = "insert";
	public static final String DDL_UPDATE = "update";
	public static final String DDL_DELETE = "delete";
	
	/**
	 * insert/update/delete
	 */
	private String ddlType;
	
	/**
	 * 传入的sql,若该sql不为空则直接使用该sql
	 */
	private String sql;
	
	/**
	 * 检测语句
	 */
	private String checkSql;
	
	/**
	 * 主键key
	 */
	private String primaryKey;
	
	/**
	 * 目标表表名
	 */
	private String toTableName;
	
	/**
	 * 目标数据源
	 */
	private SyncTemplate toTemplate;
	
	/**
	 * 行数据
	 */
	private Map<String,Object> rowParam  = new LinkedHashMap<String,Object>();
	/*
	 * 得到Named式的toTemplate
	 * @return
	 */
	public NamedParameterJdbcTemplate getNamedToTemplate(){
		NamedParameterJdbcTemplate jdbcTemplate  = new NamedParameterJdbcTemplate(toTemplate);  
		return jdbcTemplate;
	}

	public SyncTemplate getToTemplate() {
		return toTemplate;
	}

	public void setToTemplate(SyncTemplate toTemplate) {
		this.toTemplate = toTemplate;
	}

	public String getDdlType() {
		return ddlType;
	}

	public void setDdlType(String ddlType) {
		this.ddlType = ddlType;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getCheckSql() {
		return checkSql;
	}

	public void setCheckSql(String checkSql) {
		this.checkSql = checkSql;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getToTableName() {
		return toTableName;
	}

	public void setToTableName(String toTableName) {
		this.toTableName = toTableName;
	}

	public Map<String, Object> getRowParam() {
		return rowParam;
	}

	public void setRowParam(Map<String, Object> rowParam) {
		this.rowParam = rowParam;
	}
}
