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

public class TestBaseCore {
	

	public static void main(String[] args) {
		BaseRun baseBun = BaseRun.getInstance();
		BaseRunEntity baseRunEntity = new BaseRunEntity();
		baseRunEntity.setToMaxThreadNum(1);
		baseRunEntity.setFromMaxThreadNum(1);
		baseRunEntity.setTablePageMaxThreadNum(10);
		baseRunEntity.setFromTask("task2");
		baseRunEntity.setToTask("task3");
		baseRunEntity.setInsertOrUpdateMaxThreadNum(3);
		
		baseBun.baseForRun(baseRunEntity, new ForRunSync(){
			
			

			@Override
			public void allRunAfter() {
				super.allRunAfter();
			}

			@Override
			public void singleRun(SingleRunEntity singleRunEntity) {
				String fromSelectPart = "select 1 password1,t1.id,t1.username,t2.age,t2.gender ";
				String fromFromPart = "from t_user t1 left join t_user2 t2 on t1.id = t2.id";
				BaseCore baseCore = new BaseCore();
				SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
				singleStepSyncConfig.setStepUniquelyIdentifies("t_user_t_user2_t_user3");
				singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
				singleStepSyncConfig.setFromFromPart(fromFromPart);
				singleStepSyncConfig.setFromSelectPart(fromSelectPart);
				singleStepSyncConfig.setToTableName("t_user3");
				singleStepSyncConfig.setUpdateColumn("id");
				singleStepSyncConfig.setIsMultiThreadPage(false);
//				singleStepSyncConfig.setHighPerformancePageColumn("id");
//				singleStepSyncConfig.setIsSyncNullValue(false);
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
