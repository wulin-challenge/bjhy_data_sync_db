package com.bjhy.data.sync.db.test;

import com.bjhy.data.sync.db.core.BaseRun;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;

public class TestBaseRun {
	
	public static void main(String[] args) {
		BaseRun baseBun = BaseRun.getInstance();
		BaseRunEntity baseRunEntity = new BaseRunEntity();
		baseRunEntity.setToMaxThreadNum(1);
		baseRunEntity.setFromMaxThreadNum(2);
		baseRunEntity.setFromTask("task1");
		baseRunEntity.setToTask("task3");
		
		baseBun.baseForRun(baseRunEntity, new ForRunSync(){
			
			@Override
			public void allRunBefore() {
				System.out.println("allRunBefore");
			}
			
			@Override
			public void toTempleateRunBefore(SyncTemplate nativeTemplate,SyncTemplate toTemplate, int toIndex) {
				System.out.println("toTempleateRunBefore-->"+toIndex);
			}

			@Override
			public void currentRunBefore(SingleRunEntity singleRunEntity) {
				System.out.println("currentRunBefore-->"+singleRunEntity.getToIndex()+"-->"+singleRunEntity.getFromIndex());
			}

			@Override
			public void singleRun(SingleRunEntity singleRunEntity) {
				System.out.println("singleRun-->"+singleRunEntity.getToIndex()+"-->"+singleRunEntity.getFromIndex());
			}
			
			@Override
			public void currentRunAfter(SingleRunEntity singleRunEntity) {
				System.out.println("currentRunAfter-->"+singleRunEntity.getToIndex()+"-->"+singleRunEntity.getFromIndex());
			}
			
			@Override
			public void toTempleateRunAfter(SyncTemplate nativeTemplate,SyncTemplate toTemplate, int toIndex) {
				System.out.println("toTempleateRunAfter-->"+toIndex);
			}

			@Override
			public void allRunAfter() {
				System.out.println("allRunAfter");
			}
			
			
		});
	}

}
