package com.bjhy.data.sync.db.domain;

import java.util.List;
import java.util.concurrent.locks.Condition;

/**
 * 顺序步骤任务节点值
 * <p> 表示消费线程只有等待队列中该节点之前的所有任务都执行结束后,才有一个唯一一个线程能够获取到该类型的任务进行执行(consumer端)
 * <p> 通过condition 保证两个连续步骤之间的顺序(produce端)
 * @author wulin
 *
 */
public class OrderStepTaskNodeValue extends OrderTaskNodeValue{
	
	private Condition condition;

	public OrderStepTaskNodeValue(SyncLogicEntity syncLogicEntity, String insertSql, List<String> updateSqlList) {
		super(syncLogicEntity, insertSql, updateSqlList);
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

}
