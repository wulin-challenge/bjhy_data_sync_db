package com.bjhy.data.sync.db.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 当队列满了后,阻塞提交
 * @author wulin
 *
 */
public class SubmitBlockingPolicy implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		try {
			executor.getQueue().put(r);
		} catch (InterruptedException e) {
			LoggerUtils.error("SubmitBlockingPolicy: "+e.getMessage());
		}
	}

}
