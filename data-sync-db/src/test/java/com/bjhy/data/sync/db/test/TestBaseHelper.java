package com.bjhy.data.sync.db.test;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.core.BaseHelper;
import com.bjhy.data.sync.db.core.BaseRun;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;

public class TestBaseHelper {
	
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
//				BaseHelper.checkSync(singleRunEntity, "wulin_sync_user", "wulin_sync_dept_user", "id");
				BaseHelper.checkOnlyOneSync(singleRunEntity, "wulin_sync_user", "wulin_sync_dept_user", "id");
			}
			
		});
	}

}
