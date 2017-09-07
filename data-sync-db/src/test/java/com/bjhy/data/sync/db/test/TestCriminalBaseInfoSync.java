package com.bjhy.data.sync.db.test;

import java.util.Map;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.core.BaseRun;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;

public class TestCriminalBaseInfoSync {

	public static void main(String[] args) {
		BaseRun baseBun = BaseRun.getInstance();
		BaseRunEntity baseRunEntity = new BaseRunEntity();
		baseRunEntity.setToMaxThreadNum(1);
		baseRunEntity.setFromMaxThreadNum(1);
		baseRunEntity.setTablePageMaxThreadNum(5);
		baseRunEntity.setFromTask("task1");
		baseRunEntity.setToTask("task5");
		baseRunEntity.setInsertOrUpdateMaxThreadNum(10);
		
		baseBun.baseForRun(baseRunEntity, new ForRunSync(){

			@Override
			public void singleRun(SingleRunEntity singleRunEntity) {
				String fromSelectPart = "select * ";
				String fromFromPart = "from criminal_base_info";
				BaseCore baseCore = new BaseCore();
				SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
				singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
				singleStepSyncConfig.setFromFromPart(fromFromPart);
				singleStepSyncConfig.setFromSelectPart(fromSelectPart);
				singleStepSyncConfig.setToTableName("criminal_base_info");
				singleStepSyncConfig.setUpdateColumn("id");
//				singleStepSyncConfig.setHighPerformancePageColumn("id");
				singleStepSyncConfig.setIsSyncNullValue(false);
				singleStepSyncConfig.setIsMultiThreadPage(false);
//				singleStepSyncConfig.getAddStaticFromColumns().put("jybh", "123");
//				singleStepSyncConfig.getRemoveFromColumns().add("age");
//				singleStepSyncConfig.setSingleStepListener(new SingleStepListener(){
//
//					@Override
//					public Map<String, Object> rowCall(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam) {
//						rowParam.put("password", "xxx");
//						return rowParam;
//					}
//
//					
//					
//				});
//				
				baseCore.syncEntry(singleStepSyncConfig);
			}
			
		});
	}
}
