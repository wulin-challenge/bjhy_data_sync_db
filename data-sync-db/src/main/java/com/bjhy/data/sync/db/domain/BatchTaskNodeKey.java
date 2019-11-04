package com.bjhy.data.sync.db.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量任务节点key
 * @author wulin
 *
 */
public class BatchTaskNodeKey{
	private Long syncStepId;
	/**
	 * insert sql
	 */
	private String insertSql;
	
	/**
	 * update sql 集合
	 */
	private List<String> updateSqlList = new ArrayList<String>();
	
	public BatchTaskNodeKey(Long syncStepId,String insertSql, List<String> updateSqlList) {
		super();
		this.syncStepId = syncStepId;
		this.insertSql = insertSql;
		if(updateSqlList != null) {
			this.updateSqlList.addAll(updateSqlList);
		}
	}
	

	public Long getSyncStepId() {
		return syncStepId;
	}

	public void setSyncStepId(Long syncStepId) {
		this.syncStepId = syncStepId;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	public List<String> getUpdateSqlList() {
		return updateSqlList;
	}

	public void setUpdateSqlList(List<String> updateSqlList) {
		this.updateSqlList = updateSqlList;
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((syncStepId == null)?0:syncStepId.hashCode());
		result = prime * result + ((insertSql == null)?0:insertSql.hashCode());
		result = prime * result + ((updateSqlList == null)?0:updateSqlList.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		
		BatchTaskNodeKey other = (BatchTaskNodeKey)obj;
		
		if (syncStepId == null) {
			if (other.syncStepId != null)
				return false;
		} else if (!syncStepId.equals(other.syncStepId))
			return false;
		
		if (insertSql == null) {
			if (other.insertSql != null)
				return false;
		} else if (!insertSql.equals(other.insertSql))
			return false;
		
		if (updateSqlList == null) {
            if (other.updateSqlList != null)
                return false;
        } else if (!updateSqlList.equals(other.updateSqlList))
            return false;
		
		return true;
	}

	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder();
		toString.append("insertSql : ").append(insertSql+" ,\n");
		toString.append("updateSqlList : [\n");
		
		for (int i=0;i<updateSqlList.size();i++) {
			if(i == (updateSqlList.size()-1)) {
				toString.append(updateSqlList.get(i)+"]\n");
			}else {
				toString.append(updateSqlList.get(i)+"\n");
			}
		}
		return super.toString();
	}
}