package com.bjhy.data.sync.db.test;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.core.BaseRun;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;

public class TestDaMeng {
	


	public static void main(String[] args) {
		BaseRun baseBun = BaseRun.getInstance();
		BaseRunEntity baseRunEntity = new BaseRunEntity();
		baseRunEntity.setToMaxThreadNum(1);
		baseRunEntity.setFromMaxThreadNum(1);
		baseRunEntity.setTablePageMaxThreadNum(10);
		baseRunEntity.setFromTask("task_dm_from");
		baseRunEntity.setToTask("task_dm_to");
		baseRunEntity.setInsertOrUpdateMaxThreadNum(3);
		
		baseBun.baseForRun(baseRunEntity, new ForRunSync(){
			
			

			@Override
			public void allRunAfter() {
				super.allRunAfter();
			}

			@Override
			public void singleRun(SingleRunEntity singleRunEntity) {
				String fromSelectPart = "select u.id,u.username,u.password,d.id userid,d.name,d.describle ";
				String fromFromPart = "from wulin_sync_user u left join wulin_sync_dept d on u.id = d.id";
				BaseCore baseCore = new BaseCore();
				SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
				singleStepSyncConfig.setStepUniquelyIdentifies("wulin_sync_user_wulin_sync_dept");
				singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
				singleStepSyncConfig.setFromFromPart(fromFromPart);
				singleStepSyncConfig.setFromSelectPart(fromSelectPart);
				singleStepSyncConfig.setToTableName("wulin_sync_dept_user");
				singleStepSyncConfig.setUpdateColumn("id");
				singleStepSyncConfig.setIsAddVersionCheckFilter(true);
				singleStepSyncConfig.setIsMultiThreadPage(false);
				singleStepSyncConfig.setHighPerformancePageColumn("id");
				singleStepSyncConfig.setIsSyncNullValue(true);
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
