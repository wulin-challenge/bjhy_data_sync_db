package com.bjhy.data.sync.db.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 多线程消费任务
 * @author wulin
 *
 */
public class MultiThreadConsumerTask<T> {
	
	/**
	 * 线程数
	 */
	private int threadNumber = 1;
	
	private int sleepTime;
	
	private BlockingQueue<Object> block = new LinkedBlockingDeque<Object>();
	
	private TimeUnit unit;
	
	/**
	 * 开启的线程
	 */
	private Thread[] threads;
	
	private ThreadFactory threadFactory;
	
	/**
	 * 单一消费线程
	 */
	private volatile Thread singleConsumerThread;
	
	public MultiThreadConsumerTask(int threadNumber,int sleepTime,TimeUnit unit, ThreadFactory threadFactory) {
		if(threadNumber <=0) {
			threadNumber = 1;
		}
		this.threadNumber = threadNumber;
		threads = new Thread[threadNumber];
		this.sleepTime = sleepTime;
		this.unit = unit;
		this.threadFactory = threadFactory;
		
	}
	
	public void run(final ConsumerTask<T> task) {
		for (int i = 0; i < threadNumber; i++) {
			threads[i] = threadFactory.newThread(new Runnable() {
				public void run() {
					while(true) {
						try {
							T task2 = task.getTask();
							if(task2 != null) {
								task.execute(task2);
								continue;
							}
						} catch (Throwable e1) {
							LoggerUtils.error("ConsumerTask : "+e1.getMessage());
						}
						try {
							block.poll(sleepTime, unit);
						} catch (InterruptedException e) {
							LoggerUtils.error("MultiThreadConsumerTask : "+e.getMessage());
						}
					}
				}
			});
			
			//标记单一消费线程
			if(singleConsumerThread == null) {
				singleConsumerThread = threads[i];
			}
			
			threads[i].start();
		}
	}

	/**
	 * 得到单一消费线程
	 * @return
	 */
	public Thread getSingleConsumerThread() {
		return singleConsumerThread;
	}

}
