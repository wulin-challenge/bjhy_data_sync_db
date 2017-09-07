package com.bjhy.data.sync.db.natived.domain;

/**
 * 版本检测实体
 * @author wubo
 *
 */
public class VersionCheckEntity{
	/**
	 * 主键Id
	 */
	private String id;
	
	/**
	 * 步骤唯一标示
	 */
	private String stepUniquelyIdentifies;
	
	/**
	 * 来源 数据源名称
	 */
	private String fromDataSourceName;
	
	/**
	 * 来源  数据源编号
	 */
	private String fromDataSourceNumber;
	
	/**
	 * 目标 数据源名称
	 */
	private String toDataSourceName;
	
	/**
	 * 目标 数据源编号
	 */
	private String toDataSourceNumber;
	
	/**
	 * 目标表名
	 */
	private String toTableName;
	
	/**
	 * 来源的task
	 */
	private String fromTask;
	
	/**
	 * 目标的task
	 */
	private String toTask;
	
	/**
	 * 当前检测版本
	 */
	private String currentCheckVersion;
	
	/**
	 * 上一个版本
	 */
	private String beforeCheckVersion;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getToTableName() {
		return toTableName;
	}

	public void setToTableName(String toTableName) {
		this.toTableName = toTableName;
	}

	public String getCurrentCheckVersion() {
		return currentCheckVersion;
	}

	public void setCurrentCheckVersion(String currentCheckVersion) {
		this.currentCheckVersion = currentCheckVersion;
	}

	public String getBeforeCheckVersion() {
		return beforeCheckVersion;
	}

	public void setBeforeCheckVersion(String beforeCheckVersion) {
		this.beforeCheckVersion = beforeCheckVersion;
	}

	public String getFromTask() {
		return fromTask;
	}

	public void setFromTask(String fromTask) {
		this.fromTask = fromTask;
	}

	public String getToTask() {
		return toTask;
	}

	public void setToTask(String toTask) {
		this.toTask = toTask;
	}

	public String getStepUniquelyIdentifies() {
		return stepUniquelyIdentifies;
	}

	public void setStepUniquelyIdentifies(String stepUniquelyIdentifies) {
		this.stepUniquelyIdentifies = stepUniquelyIdentifies;
	}

	public String getFromDataSourceName() {
		return fromDataSourceName;
	}

	public void setFromDataSourceName(String fromDataSourceName) {
		this.fromDataSourceName = fromDataSourceName;
	}

	public String getFromDataSourceNumber() {
		return fromDataSourceNumber;
	}

	public void setFromDataSourceNumber(String fromDataSourceNumber) {
		this.fromDataSourceNumber = fromDataSourceNumber;
	}

	public String getToDataSourceName() {
		return toDataSourceName;
	}

	public void setToDataSourceName(String toDataSourceName) {
		this.toDataSourceName = toDataSourceName;
	}

	public String getToDataSourceNumber() {
		return toDataSourceNumber;
	}

	public void setToDataSourceNumber(String toDataSourceNumber) {
		this.toDataSourceNumber = toDataSourceNumber;
	}
}
