package com.bjhy.data.sync.db.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunThread;
import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 线程控制类(需要手动关闭线程池)
 * @author wubo
 *
 */
public class ThreadControl2 {
	
	private int maxThreadNumber;//最大线程数
	
	private ExecutorService threadPool;
	
	public ThreadControl2(int maxThreadNumber){
		if(maxThreadNumber == 0){
			maxThreadNumber = 1;//最少有一个线程
		}
		this.maxThreadNumber = maxThreadNumber;
		threadPool =  Executors.newFixedThreadPool(maxThreadNumber);
	}
	
	/**
	 * 这个方式谨慎使用,因为最后线程池是没有被关闭的
	 * @param iterations
	 * @param forRunThread
	 */
	public void forRunStart2(final int iterations,final ForRunThread forRunThread){
		forRunThread.allThreadBeforeRun(iterations);
		List<Callable<String>> callList = new ArrayList<>();
		for (int m = 0; m < iterations; m++) {
			final int n = m;
			Callable<String> call = new Callable<String>() {
				@Override
				public String call() throws Exception {
					try {
						forRunThread.currentThreadRunning(iterations, n);
					} catch (Exception e) {
						e.printStackTrace();
						LoggerUtils.error("错误信息: "+e.getMessage());
					}
					//暂时不需要任何返回值
					return null;
				}
			}; 
			callList.add(call); //收集
		}
		
		try {
			if(callList.isEmpty()){
				LoggerUtils.info("当前没有任何任务！！");
			}
			
			//判断线程池是否被关闭了
			if (!threadPool.isShutdown()) {
				threadPool.invokeAll(callList);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到最大线程数
	 * @return
	 */
	public int getMaxThreadNumber() {
		return maxThreadNumber;
	}
	
}
