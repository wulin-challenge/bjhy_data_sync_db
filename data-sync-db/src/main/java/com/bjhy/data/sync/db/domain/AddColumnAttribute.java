package com.bjhy.data.sync.db.domain;

/**
 * 添加列属性
 * @author wubo
 *
 */
public class AddColumnAttribute {
	
	/**
	 * 列的名称
	 */
	private String columnName;
	/**
	 * 对应的表名
	 */
	private String tableName;
	
	/**
	 * 长度
	 */
	private Integer length=255;
	
	public AddColumnAttribute(String columnName, String tableName) {
		super();
		this.columnName = columnName;
		this.tableName = tableName;
	}

	public AddColumnAttribute(String columnName, String tableName, Integer length) {
		super();
		this.columnName = columnName;
		this.tableName = tableName;
		this.length = length;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}
	
	//.....
}
