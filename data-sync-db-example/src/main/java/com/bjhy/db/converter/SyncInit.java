package com.bjhy.db.converter;

import org.springframework.beans.factory.InitializingBean;

import com.bjhy.db.converter.core.SyncEntry;

/**
 * 程序启动就进行一次数据同步
 * @author wubo
 *
 */
public class SyncInit implements InitializingBean{ 
	
	/**
	 * 同步入口
	 */
	private SyncEntry syncEntry;
	
	/**
	 * 是否启用启动同步
	 */
	private Boolean syncInitEnable;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(syncInitEnable){
			syncEntry.run();
		}
	}

	public void setSyncEntry(SyncEntry syncEntry) {
		this.syncEntry = syncEntry;
	}

	public void setSyncInitEnable(Boolean syncInitEnable) {
		this.syncInitEnable = syncInitEnable;
	}
}
