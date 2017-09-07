package com.bjhy.data.sync.db.test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.core.BaseRun;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;
import com.bjhy.data.sync.db.named.template.test.domain.SingleStepSyncConfig2;
import com.bjhy.data.sync.db.util.StepObjectUtil;
import com.bjhy.data.sync.db.validation.SyncStepValidationRepair;
import com.bjhy.data.sync.db.validation.SyncStepValidationStore;
import com.bjhy.platform.util.BeanUtils;
import com.fasterxml.jackson.databind.util.BeanUtil;

public class TestMySql {
	


	public static void main(String[] args) {
		BaseRun baseBun = BaseRun.getInstance();
		
//		
		BaseRunEntity baseRunEntity = new BaseRunEntity();
		baseRunEntity.setToMaxThreadNum(1);
		baseRunEntity.setFromMaxThreadNum(1);
		baseRunEntity.setTablePageMaxThreadNum(10);
		baseRunEntity.setFromTask("task_mySql_from");
		baseRunEntity.setToTask("task_mySql_to");
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
				singleStepSyncConfig.setIsSyncNullValue(false);
//				singleStepSyncConfig.setIsAddVersionCheckFilter(false);
				singleStepSyncConfig.setIsMultiThreadPage(true);
				singleStepSyncConfig.setHighPerformancePageColumn("id");
				singleStepSyncConfig.setIsSyncNullValue(false);
				singleStepSyncConfig.setSingleStepListenerName("com.bjhy.data.sync.db.test.listener.DeptListener");
//				singleStepSyncConfig.getAddStaticFromColumns().put("jybh", "123");
//				singleStepSyncConfig.getRemoveFromColumns().add("age");
//				singleStepSyncConfig.setSingleStepListener(new SingleStepListener(){
//
//					@Override
//					public Map<String, Object> rowCall(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam) {
////						rowParam.put("password", "xxx");
//						String sql = "select * from wulin_sync_dept";
//						List<Map<String, Object>> queryForList = syncLogicEntity.getNamedFromTemplate().queryForList(sql, Collections.EMPTY_MAP);
//						for (Map<String, Object> map : queryForList) {
//							String name = (String)map.get("name");
//							System.out.println(name);
//							
//						}
//						System.out.println("99999999999999-----------");
//						return rowParam;
//					}
//
//					
//					
//				});
				baseCore.syncEntry(singleStepSyncConfig);
			}
			
		});
	}


}
