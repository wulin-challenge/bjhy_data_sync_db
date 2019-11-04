package com.bjhy.data.sync.db.domain;

import java.util.List;

/**
 * 普通顺序任务节点值
 * <p> 表示消费线程只有等待队列中该节点之前的所有任务都执行结束后,才有一个唯一一个线程能够获取到该类型的任务进行执行(consumer端)
 * @author wulin
 *
 */
public class OrderTaskNodeValue extends BatchTaskNodeValue{
	

	public OrderTaskNodeValue(SyncLogicEntity syncLogicEntity, String insertSql, List<String> updateSqlList) {
		super(syncLogicEntity, insertSql, updateSqlList);
	}

}
