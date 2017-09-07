package com.bjhy.data.sync.db.natived.domain;

import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.util.LoggerUtils;
import com.bjhy.data.sync.db.util.SeriUtil;
import com.bjhy.data.sync.db.util.StepObjectUtil;

/**
 * 步骤存储实体
 */
public class StepStoreEntity {
	
	/**
	 * 主键Id
	 */
	private String id;
	
	/**
	 * 是否修复同步判断
	 * 0:表示不同步,1:表示同步
	 */
	private String isRepairSync; 
	
	/**
	 * 来源任务
	 */
	private String fromTask;
	
	/**
	 * 目标任务
	 */
	private String toTask;
	
	/**
	 * 目标表名
	 */
	private String toTableName;
	
	/**
	 * 步骤唯一标示
	 */
	private String stepUniquelyIdentifies;
	
	/**
	 * 来源数据源名称
	 */
	private String fromDataSourceName;
	
	/**
	 * 来源数据源编号
	 */
	private String fromDataSourceNumber;
	
	/**
	 * 目标数据源名称
	 */
	private String toDataSourceName;
	
	/**
	 * 目标数据源编号
	 */
	private String toDataSourceNumber;
	
	/**
	 * 来源数据数量
	 */
	private Integer fromDataNumber;
	
	/**
	 * 目标数据数量
	 */
	private Integer toDataNumber;
	
	/**
	 * 单个步骤同步配置 的二进制对象
	 */
	private byte[] singleStepByte;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIsRepairSync() {
		return isRepairSync;
	}

	public void setIsRepairSync(String isRepairSync) {
		this.isRepairSync = isRepairSync;
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

	/**
	 * 得到从二进制反序列化回来的对象
	 * @return
	 */
	public SingleStepSyncConfig getSingleStepSyncConfig() {
		SingleStepSyncConfig unserializeProtoStuffToObj = SeriUtil.unserializeProtoStuffToObj(this.singleStepByte, SingleStepSyncConfig.class);
		//恢复 SingleStepSyncConfig 对象
		unserializeProtoStuffToObj = StepObjectUtil.recoverStepObject(unserializeProtoStuffToObj);
		return unserializeProtoStuffToObj;
	}
	
	/**
	 * 设置 SingleStepSyncConfig 对象,并将其转换为二进制
	 * @param singleStepSyncConfig
	 */
	public void setSingleStepSyncConfig(SingleStepSyncConfig singleStepSyncConfig) {
		SingleStepSyncConfig copyStepObject = StepObjectUtil.copyStepObject(singleStepSyncConfig);
		byte[] serializeProtoStuffTobyteArray = SeriUtil.serializeProtoStuffTobyteArray(copyStepObject, SingleStepSyncConfig.class);
		LoggerUtils.info("序列化后的字节大小  : "+serializeProtoStuffTobyteArray.length);
		this.singleStepByte = serializeProtoStuffTobyteArray;
	}

	public byte[] getSingleStepByte() {
		return singleStepByte;
	}

	public void setSingleStepByte(byte[] singleStepByte) {
		this.singleStepByte = singleStepByte;
	}

	public Integer getFromDataNumber() {
		return fromDataNumber;
	}

	public void setFromDataNumber(Integer fromDataNumber) {
		this.fromDataNumber = fromDataNumber;
	}

	public Integer getToDataNumber() {
		return toDataNumber;
	}

	public void setToDataNumber(Integer toDataNumber) {
		this.toDataNumber = toDataNumber;
	}

	public String getToTableName() {
		return toTableName;
	}

	public void setToTableName(String toTableName) {
		this.toTableName = toTableName;
	}
}
