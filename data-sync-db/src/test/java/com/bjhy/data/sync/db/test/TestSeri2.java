package com.bjhy.data.sync.db.test;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.core.BaseRun;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;
import com.bjhy.data.sync.db.util.SeriUtil;
import com.bjhy.data.sync.db.util.StepObjectUtil;
import com.bjhy.data.sync.db.validation.SyncStepValidationStore;

public class TestSeri2 {
	


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
				
				for(int i=0;i<10;i++){
					
//					
//				
				
					SingleStepSyncConfig copyStepObject = StepObjectUtil.copyStepObject(xx(singleRunEntity));
					byte[] serializeProtoStuffTobyteArray = SeriUtil.serializeProtoStuffTobyteArray(copyStepObject, SingleStepSyncConfig.class);
					System.out.println(i+"大小  "+serializeProtoStuffTobyteArray.length);
					
					
					SingleStepSyncConfig unserializeProtoStuffToObj = SeriUtil.unserializeProtoStuffToObj(serializeProtoStuffTobyteArray, SingleStepSyncConfig.class);
////					//恢复 SingleStepSyncConfig 对象
					unserializeProtoStuffToObj = StepObjectUtil.recoverStepObject(unserializeProtoStuffToObj);
					String dataSourceName = unserializeProtoStuffToObj.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
					System.out.println(dataSourceName);
					
			
			}
			}
			
		});
	}
	
	
	private static SingleStepSyncConfig xx(SingleRunEntity singleRunEntity){
		String fromSelectPart = "select u.id,u.username,u.password,d.id userid,d.name,d.describle ";
		String fromFromPart = "from wulin_sync_user u left join wulin_sync_dept d on u.id = d.id";
		SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		singleStepSyncConfig.setStepUniquelyIdentifies("wulin_sync_user_wulin_sync_dept");
		singleStepSyncConfig.setSingleRunEntity(singleRunEntity);
		singleStepSyncConfig.setFromFromPart(fromFromPart);
		singleStepSyncConfig.setFromSelectPart(fromSelectPart);
		singleStepSyncConfig.setToTableName("wulin_sync_dept_user");
		singleStepSyncConfig.setUpdateColumn("id");
		singleStepSyncConfig.setIsSyncNullValue(false);
		singleStepSyncConfig.setIsAddVersionCheckFilter(false);
		singleStepSyncConfig.setIsMultiThreadPage(true);
		singleStepSyncConfig.setHighPerformancePageColumn("id");
		singleStepSyncConfig.setIsSyncNullValue(false);
		singleStepSyncConfig.setSingleStepListenerName("com.bjhy.data.sync.db.test.listener.DeptListener");
		singleStepSyncConfig.getAddStaticFromColumns().put("jybh", "123");
//		singleStepSyncConfig.getRemoveFromColumns().add("age");
		return singleStepSyncConfig;
	}

}
