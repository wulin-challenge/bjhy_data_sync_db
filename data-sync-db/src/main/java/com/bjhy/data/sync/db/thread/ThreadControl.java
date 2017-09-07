package com.bjhy.data.sync.db.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunThread;
import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 线程控制类
 * @author wubo
 *
 */
public class ThreadControl {
	
	private int maxThreadNumber;//最大线程数
	
	private ExecutorService threadPool;
	
	public ThreadControl(int maxThreadNumber){
		if(maxThreadNumber == 0){
			maxThreadNumber = 1;//最少有一个线程
		}
		this.maxThreadNumber = maxThreadNumber;
		threadPool =  Executors.newFixedThreadPool(maxThreadNumber);
	}
	
	
	public void forRunStart(final int iterations,final ForRunThread forRunThread){
		forRunThread.allThreadBeforeRun(iterations);
		List<Callable<String>> callList = new ArrayList<>();
		for (int m = 0; m < iterations; m++) {
			final int n = m;
			Callable<String> call = new Callable<String>() {
				@Override
				public String call() throws Exception {
					try {
						forRunThread.currentThreadBeforeRun(iterations, n);
						forRunThread.currentThreadRunning(iterations, n);
					} catch (Exception e) {
						e.printStackTrace();
						LoggerUtils.error("错误信息: "+e.getMessage());
						forRunThread.currentThreadErrorRun(iterations, n,e);
					}
					forRunThread.currentThreadAfterRun(iterations, n);
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
			threadPool.shutdown();
			forRunThread.allThreadAfterRun(iterations);
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
