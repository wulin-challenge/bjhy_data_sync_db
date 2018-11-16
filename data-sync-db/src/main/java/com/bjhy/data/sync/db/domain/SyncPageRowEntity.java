package com.bjhy.data.sync.db.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步分行实体:
 * 将同步字段量非常多的实体进行拆分,先插入主字段后采用多线程的方式进行进行更新后面的字段值
 * 例如:user(column1,column2,...,column300)
 * 假设 column1,column2是主字段,而数据库中又不存在改行的值,就先进行查询,然后采用多线程的方式更新其他值
 * @author wubo
 */
public class SyncPageRowEntity {
	
	/**
	 * 分行的列数
	 */
	private int pageRowColumns = 50;
	
	/**
	 * 用于分行的insert分行列
	 */
	private String pageRowInsertColumnSql;
	
	/**
	 * 用于分行的update分行列
	 * 当前语句包含了insert语句的字段,以降低复杂度
	 */
	private List<String> pageRowUpdateColumnSqlList = new ArrayList<String>();
	
	/**
	 * 用于分行的insert列,这些列包括主键列和唯一约束列,当然如果唯一约束被禁用,可以不用考虑唯一约束列
	 */
	private List<String> pageRowInsertColumns = new ArrayList<String>();

	public int getPageRowColumns() {
		return pageRowColumns;
	}

	public void setPageRowColumns(int pageRowColumns) {
		this.pageRowColumns = pageRowColumns;
	}

	public String getPageRowInsertColumnSql() {
		return pageRowInsertColumnSql;
	}

	public void setPageRowInsertColumnSql(String pageRowInsertColumnSql) {
		this.pageRowInsertColumnSql = pageRowInsertColumnSql;
	}

	public List<String> getPageRowUpdateColumnSqlList() {
		return pageRowUpdateColumnSqlList;
	}

	public void setPageRowUpdateColumnSqlList(
			List<String> pageRowUpdateColumnSqlList) {
		this.pageRowUpdateColumnSqlList = pageRowUpdateColumnSqlList;
	}

	public List<String> getPageRowInsertColumns() {
		return pageRowInsertColumns;
	}

	public void setPageRowInsertColumns(List<String> pageRowInsertColumns) {
		this.pageRowInsertColumns = pageRowInsertColumns;
	}
}
