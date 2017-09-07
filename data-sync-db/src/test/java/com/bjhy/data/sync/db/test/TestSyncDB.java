package com.bjhy.data.sync.db.test;

import com.bjhy.data.sync.db.loader.SyncConfigLoader;

public class TestSyncDB {
	
	public static void main(String[] args) {
		SyncConfigLoader syncConfigLoader = new SyncConfigLoader();
		syncConfigLoader.configLoader();
	}

}
