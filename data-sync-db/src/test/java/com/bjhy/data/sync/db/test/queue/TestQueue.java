package com.bjhy.data.sync.db.test.queue;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

public class TestQueue {
	
	/**
	 * 默认5万条任务节点,TODO 后期可修改为可配置
	 */
	private BlockingQueue<Integer> asyncTask = new LinkedBlockingQueue<Integer>(5);
	
	@Test
	public void producerAndConsumerTest() {
		Thread producer = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					producer();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		producer.start();
		
		Thread consumer = new Thread(new Runnable() {
			@Override
			public void run() {
				consumer();
			}
		});
		
		consumer.start();
		System.out.println("启动成功!");
		
	}
	
	private void producer() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			asyncTask.put(i);
		}
	}
	
	private void consumer() {
		for (int i = 2; i < 20; i++) {
			Integer task = getTask(i);
			System.out.println("消费:"+task);
		}
	}
	
	private Integer getTask(Integer i) {
		Iterator<Integer> iterator = asyncTask.iterator();
		
		while(iterator.hasNext()) {
			Integer next = iterator.next();
			if(i == next) {
				asyncTask.remove(i);
				return next;
			}
		}
		return null;
	}

}
