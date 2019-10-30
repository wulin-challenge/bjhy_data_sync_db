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
	 * 是否为步骤的结束任务节点
	 */
	private Boolean isStepEndTaskNode = false;
	
	/**
	 * 数据
	 */
	private List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
	
	private SyncLogicEntity syncLogicEntity;
	
	public BatchTaskNodeValue(SyncLogicEntity syncLogicEntity,String insertSql, List<String> updateSqlList) {
		super(insertSql, updateSqlList);
		this.syncLogicEntity = syncLogicEntity;
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
		final int prime = 31;
		
		int result = super.hashCode();
		result = prime * result + ((isStepEndTaskNode == null)?0:isStepEndTaskNode.hashCode());
		result = prime * result + ((data == null)?0:data.hashCode());
		result = prime * result + ((syncLogicEntity == null)?0:syncLogicEntity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		
		BatchTaskNodeValue other = (BatchTaskNodeValue)obj;
		
		if(!super.equals(obj)) {
			return false;
		}
		
		if (isStepEndTaskNode == null) {
			if (other.isStepEndTaskNode != null)
				return false;
		} else if (!isStepEndTaskNode.equals(other.isStepEndTaskNode))
			return false;
		
		if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
		
		if (syncLogicEntity == null) {
			if (other.syncLogicEntity != null)
				return false;
		} else if (!syncLogicEntity.equals(other.syncLogicEntity))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
}