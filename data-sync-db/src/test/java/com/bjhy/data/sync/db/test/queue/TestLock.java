package com.bjhy.data.sync.db.test.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

public class TestLock {
	private ReentrantLock lock = new ReentrantLock();
	private final BlockingQueue<LockTask> consumeRequestQueue = new LinkedBlockingQueue<LockTask>(5);
	
	@Test
	public void testProducerAndConsumer() throws InterruptedException {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				consumer();
			}
		}) ;
		thread.start();
		
		
		Thread thread2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					producer();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}) ;
		thread2.start();
		producer();
		
		System.out.println();
		
		
	}
	
	private void producer() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			LockTask lockTask = new LockTask(lock.newCondition());
			
			try {
				lock.lock();
				consumeRequestQueue.put(lockTask);
				lockTask.getCondition().await();
			} finally {
				if(lock.isLocked()) {
					lock.unlock();
				}
			}
		}
	}
	
	private void consumer() {
		while(true) {
			System.out.println("运行生产者进行!");
			
			try {
				LockTask poll = consumeRequestQueue.take();
				lock.lock();
				poll.getCondition().signal();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(lock.isLocked()) {
					lock.unlock();
				}
			}
			
		}
	}
	
	private static class LockTask{
		private Condition condition;
		
		public LockTask(Condition condition) {
			super();
			this.condition = condition;
		}

		public Condition getCondition() {
			return condition;
		}
		
	}
	

}
