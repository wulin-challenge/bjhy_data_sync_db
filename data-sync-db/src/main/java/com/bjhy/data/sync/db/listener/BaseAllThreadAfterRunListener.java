package com.bjhy.data.sync.db.listener;

import com.bjhy.data.sync.db.inter.face.OwnInterface.AllThreadAfterRunEndListener;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;
import com.bjhy.data.sync.db.thread.ThreadFactoryImpl;
import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 所有同步运行完后执行监听器
 * @author wulin
 *
 */
public class BaseAllThreadAfterRunListener implements AllThreadAfterRunEndListener{
	private ForRunSync forRunSync;
	
	public BaseAllThreadAfterRunListener(ForRunSync forRunSync) {
		super();
		this.forRunSync = forRunSync;
	}

	@Override
	public void allThreadAfterRun() {
		ThreadFactoryImpl threadFactory = new ThreadFactoryImpl("BaseAllThreadAfterRunListener");
		threadFactory.newThread(new Runnable() {
			
			@Override
			public void run() {
				if(forRunSync != null) {
					try {
						forRunSync.allRunAfter();
					} catch (Exception e) {
						LoggerUtils.error("运行 forRunSync.allRunAfter()出错! "+e.getMessage());
					}
				}
			}
		}).start();
		
	}

}
