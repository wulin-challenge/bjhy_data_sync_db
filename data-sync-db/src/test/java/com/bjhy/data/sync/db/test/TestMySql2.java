package com.bjhy.data.sync.db.test;

import com.bjhy.data.sync.db.core.BaseCore;
import com.bjhy.data.sync.db.core.BaseRun;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;
import com.bjhy.data.sync.db.loader.DataSourceLoader;

public class TestMySql2 {
	


	public static void main(String[] args) {
		BaseRun baseBun = BaseRun.getInstance();
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
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				xx(singleRunEntity,DataSourceLoader.getUUID());
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				
				
			}
			
		});
	}


	
	public static void xx(SingleRunEntity singleRunEntity,String unique){
		BaseCore baseCore = new BaseCore();
		String fromSelectPart = "select u.id,u.username,u.password,d.id userid,d.name,d.describle ";
		String fromFromPart = "from wulin_sync_user u left join wulin_sync_dept d on u.id = d.id";
		SingleStepSyncConfig singleStepSyncConfig = new SingleStepSyncConfig();
		singleStepSyncConfig.setStepUniquelyIdentifies(unique);
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
		baseCore.syncEntry(singleStepSyncConfig);
	}
}
