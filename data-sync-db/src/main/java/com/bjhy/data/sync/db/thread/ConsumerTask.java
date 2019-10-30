package com.bjhy.data.sync.db.thread;

/**
 * 消费者认为
 * @author wulin
 *
 * @param <T> 任务
 */
public interface ConsumerTask<T> {
	T getTask();
	
	void execute(T task);
}
