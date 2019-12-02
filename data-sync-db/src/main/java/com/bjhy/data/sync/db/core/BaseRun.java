package com.bjhy.data.sync.db.core;

import java.util.List;

import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunThread;
import com.bjhy.data.sync.db.thread.ThreadControl;
import com.bjhy.data.sync.db.util.SyncPropertiesUtil;
import com.bjhy.data.sync.db.validation.SyncStepValidationRepair;

/**
 * 同步基本架构支持类
 * @author wubo
 *
 */
public class BaseRun {
	
	private static BaseRun baseRun;
	
	/**
	 * 修复的标记(表示只执行一次的标记)
	 */
	private Boolean repairFlagOnlyOne = true;
	
	private BaseRun() {
		
		//加载核心资源
		BaseLoaderCore.getInstance().coreLoader();
		
	}
	
	public void baseForRun(final BaseRunEntity baseRunEntity,final ForRunSync forRunSync){
		//自动//校验修复
		if(baseRunEntity.getIsAutoRepair() && repairFlagOnlyOne){
			repairFlagOnlyOne = false;
			SyncStepValidationRepair.getInstance().validationRepairStart();
		}
		//得到能用的 来源 同步Template 通过 fromTask
		final List<SyncTemplate> enableFromSyncTemplateList = baseRunEntity.enableFromSyncTemplateList();
		//得到能用的 目标 同步Template 通过 toTask
		final List<SyncTemplate> enableToSyncTemplateList = baseRunEntity.enableToSyncTemplateList();
		//得到能用的 本地 存储Template
		final SyncTemplate enableNativeSyncTemplate = baseRunEntity.enableNativeSyncTemplate();
		
		//所有同步开始前运行
		ThreadControl toThreadControl = new ThreadControl(baseRunEntity.getToMaxThreadNum());
		toThreadControl.forRunStart(enableToSyncTemplateList.size(), new ForRunThread(){

			/**
			 * 所有同步开始前运行
			 */
			@Override
			public void allThreadBeforeRun(int iterations) {
				forRunSync.allRunBefore();
			}
			
			/**
			 * 目标源开运行前
			 */
			@Override
			public void currentThreadBeforeRun(int iterations, int toIndex) {
				forRunSync.toTempleateRunBefore(enableNativeSyncTemplate, enableToSyncTemplateList.get(toIndex), toIndex);
			}

			@Override
			public void currentThreadRunning(int iterations,final int toIndex) {
				
				//嵌套的第二层fromTemplate循环
				ThreadControl fromThreadControl = new ThreadControl(baseRunEntity.getFromMaxThreadNum());
				fromThreadControl.forRunStart(enableFromSyncTemplateList.size(), new ForRunThread(){
					
//					//目标源开运行前(暂时不用这个实现)
//					@Override
//					public void allThreadBeforeRun(int iterations) {
//						//forRunSync.toTempleateRunBefore(enableNativeSyncTemplate, enableToSyncTemplateList.get(toIndex), toIndex);
//					}

					//当前数据源运行前执行
					@Override
					public void currentThreadBeforeRun(int iterations, int fromIndex) {
						SingleRunEntity singleRunEntity = new SingleRunEntity();
						singleRunEntity.setNativeTemplate(enableNativeSyncTemplate);
						singleRunEntity.setToTemplate(enableToSyncTemplateList.get(toIndex));
						singleRunEntity.setFromTemplate(enableFromSyncTemplateList.get(fromIndex));
						singleRunEntity.setFromIndex(fromIndex);
						singleRunEntity.setToIndex(toIndex);
						singleRunEntity.setBaseRunEntity(baseRunEntity);
						
						forRunSync.currentRunBefore(singleRunEntity);
					}

					@Override
					public void currentThreadRunning(int iterations, int fromIndex) {
						SingleRunEntity singleRunEntity = new SingleRunEntity();
						singleRunEntity.setNativeTemplate(enableNativeSyncTemplate);
						singleRunEntity.setToTemplate(enableToSyncTemplateList.get(toIndex));
						singleRunEntity.setFromTemplate(enableFromSyncTemplateList.get(fromIndex));
						singleRunEntity.setFromIndex(fromIndex);
						singleRunEntity.setToIndex(toIndex);
						singleRunEntity.setBaseRunEntity(baseRunEntity);
						
						forRunSync.singleRun(singleRunEntity);
					}
					
					//当前数据源运行后执行
					@Override
					public void currentThreadAfterRun(int iterations, int fromIndex) {
						SingleRunEntity singleRunEntity = new SingleRunEntity();
						singleRunEntity.setNativeTemplate(enableNativeSyncTemplate);
						singleRunEntity.setToTemplate(enableToSyncTemplateList.get(toIndex));
						singleRunEntity.setFromTemplate(enableFromSyncTemplateList.get(fromIndex));
						singleRunEntity.setFromIndex(fromIndex);
						singleRunEntity.setToIndex(toIndex);
						singleRunEntity.setBaseRunEntity(baseRunEntity);
						
						forRunSync.currentRunAfter(singleRunEntity);
					}
					
//					//目标源运行结束后(暂时不用这个方式实现)
//					@Override
//					public void allThreadAfterRun(int iterations) {
//						//forRunSync.toTempleateRunAfter(enableNativeSyncTemplate, enableToSyncTemplateList.get(toIndex), toIndex);
//					}
				});
				
			}
			
			/**
			 * 目标源运行结束后
			 */
			@Override
			public void currentThreadAfterRun(int iterations, int toIndex) {
				forRunSync.toTempleateRunAfter(enableNativeSyncTemplate, enableToSyncTemplateList.get(toIndex), toIndex);
			}
			
			/**
			 * 所有同步运行完后执行
			 */
			@Override
			public void allThreadAfterRun(int iterations) {
				//保证执行下一个任务之前的所有任务都已经执行完成
//				forRunSync.allRunAfter();
				
				//forRunSync.allRunAfter()的功能有下面这个语句完成了 
				BaseAsynchronousBatchCommitCode.getInstance().addEndEmptyOrderTask(forRunSync);
				
				//是否为只同步一次步骤
				if(baseRunEntity.getIsThisOnlyOne()){
					SyncPropertiesUtil.setProperty("sync.is.this.only.one", "false");
				}
			}
		});
	}
	
	public static BaseRun getInstance(){
		if(baseRun == null){
			baseRun = new BaseRun();
		}
		return baseRun;
	}
}
