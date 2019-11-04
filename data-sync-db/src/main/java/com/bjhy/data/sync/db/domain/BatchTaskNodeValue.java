package com.bjhy.data.sync.db.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量任务节点值
 * @author wulin
 *
 */
public class BatchTaskNodeValue extends BatchTaskNodeKey{
	
	/**
	 * 任务节点Id,主要用于调试时使用
	 */
	private Long taskNodeId;
	
	/**
	 * 是否为步骤的结束任务节点
	 */
	private Boolean isStepEndTaskNode = false;
	
	/**
	 * 数据
	 */
	private List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
	
	private SyncLogicEntity syncLogicEntity;
	
	/**
	 * 表示这是一个新的对象,用于重写hashcode和equals方法
	 */
	private Object currentAddress = new Object();
	
	public BatchTaskNodeValue(SyncLogicEntity syncLogicEntity,String insertSql, List<String> updateSqlList) {
		super(syncLogicEntity.getSyncStepId(),insertSql, updateSqlList);
		this.syncLogicEntity = syncLogicEntity;
	}
	
	public Long getTaskNodeId() {
		return taskNodeId;
	}

	public void setTaskNodeId(Long taskNodeId) {
		this.taskNodeId = taskNodeId;
	}

	public Boolean getIsStepEndTaskNode() {
		return isStepEndTaskNode;
	}

	public void setIsStepEndTaskNode(Boolean isStepEndTaskNode) {
		this.isStepEndTaskNode = isStepEndTaskNode;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}
	
	public SyncLogicEntity getSyncLogicEntity() {
		return syncLogicEntity;
	}

	public void setSyncLogicEntity(SyncLogicEntity syncLogicEntity) {
		this.syncLogicEntity = syncLogicEntity;
	}

	@SuppressWarnings("unchecked")
	public Map<String,Object>[] getArrayData(){
		return data.toArray(new HashMap[data.size()]);
	}

	@Override
	public int hashCode() {
		return this.currentAddress.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;
		if(object == null) return false;
		if(getClass() != object.getClass()) return false;
		
		BatchTaskNodeValue other = (BatchTaskNodeValue)object;
		
		if (currentAddress == null) {
			if (other.currentAddress != null)
				return false;
		} else if (!currentAddress.equals(other.currentAddress))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return currentAddress.toString();
	}
	
}