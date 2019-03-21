package com.bjhy.data.sync.db.util;

import org.springframework.beans.BeanUtils;

import com.bjhy.data.sync.db.core.BaseLoaderCore;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.ConnectConfig;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncTemplate;

/**
 * 步骤对象拷贝以及恢复工具类
 * @author wubo
 *
 */
public class StepObjectUtil {
	
	/**
	 * 恢复  SingleStepSyncConfig 对象
	 * @param copySingleStepSyncConfig
	 * @return
	 */
	public static SingleStepSyncConfig recoverStepObject(SingleStepSyncConfig copySingleStepSyncConfig){
		ConnectConfig fromConnectConfig = copySingleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig();
		ConnectConfig toConnectConfig = copySingleStepSyncConfig.getSingleRunEntity().getToTemplate().getConnectConfig();
		ConnectConfig nativeConnectConfig = copySingleStepSyncConfig.getSingleRunEntity().getNativeTemplate().getConnectConfig();
		
		SyncTemplate fromSyncTemplate = DataSourceUtil.getInstance().getEnableFromSyncTemplateByConnectConfig(fromConnectConfig);
		SyncTemplate toSyncTemplate = DataSourceUtil.getInstance().getEnableToSyncTemplateByConnectConfig(toConnectConfig);
		SyncTemplate NativeSyncTemplate = DataSourceUtil.getInstance().getEnableNativeSyncTemplateByConnectConfig(nativeConnectConfig);
		
		copySingleStepSyncConfig.getSingleRunEntity().setFromTemplate(fromSyncTemplate);
		copySingleStepSyncConfig.getSingleRunEntity().setToTemplate(toSyncTemplate);
		copySingleStepSyncConfig.getSingleRunEntity().setNativeTemplate(NativeSyncTemplate);
		copySingleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().setSyncConfig(BaseLoaderCore.getInstance().getSyncConfig());
		copySingleStepSyncConfig.setStartStepSyncType(SingleStepSyncConfig.START_STEP_USER_SYNC);
		return copySingleStepSyncConfig;
	}
	
	/**
	 * 深度拷贝  SingleStepSyncConfig
	 * @param singleStepSyncConfig
	 * @return
	 */
	public static SingleStepSyncConfig copyStepObject(SingleStepSyncConfig singleStepSyncConfig){
		SingleRunEntity copySingleRunEntity = copySingleRunEntity(singleStepSyncConfig.getSingleRunEntity());
		
		SingleStepSyncConfig newSingleStepSyncConfig = new SingleStepSyncConfig();
		BeanUtils.copyProperties(singleStepSyncConfig, newSingleStepSyncConfig);
		
		newSingleStepSyncConfig.setSingleRunEntity(copySingleRunEntity);
		return newSingleStepSyncConfig;
	}
	
	/**
	 * 拷贝  当个数据源运行的Entity
	 * @param singleRunEntity
	 * @return
	 */
	private static SingleRunEntity copySingleRunEntity(SingleRunEntity singleRunEntity){
		
		SyncTemplate fromTemplate = copySyncTemplate(singleRunEntity.getFromTemplate());
		SyncTemplate toTemplate = copySyncTemplate(singleRunEntity.getToTemplate());
		SyncTemplate nativeTemplate = copySyncTemplate(singleRunEntity.getNativeTemplate());
		
		SingleRunEntity newSingleRunEntity = new SingleRunEntity();
		
		BeanUtils.copyProperties(singleRunEntity, newSingleRunEntity);
		
		BaseRunEntity copyBaseRunEntity = copyBaseRunEntity(singleRunEntity.getBaseRunEntity());
		
		newSingleRunEntity.setBaseRunEntity(copyBaseRunEntity);
		newSingleRunEntity.setFromTemplate(fromTemplate);
		newSingleRunEntity.setToTemplate(toTemplate);
		newSingleRunEntity.setNativeTemplate(nativeTemplate);
		
		return newSingleRunEntity;
	}
	
	/**
	 * 拷贝基本架构运行参数实体
	 * @param baseRunEntity
	 * @return
	 */
	private static BaseRunEntity copyBaseRunEntity(BaseRunEntity baseRunEntity){
		BaseRunEntity newBaseRunEntity = new BaseRunEntity();
		BeanUtils.copyProperties(baseRunEntity, newBaseRunEntity);
		newBaseRunEntity.setSyncConfig(null); //这里必须去空
		return newBaseRunEntity;
	}
	
	/**
	 * 复制  同步的template 对象,并去掉 两个dataSource
	 * @param syncTemplate
	 * @return
	 */
	private static SyncTemplate copySyncTemplate(SyncTemplate syncTemplate){
		SyncTemplate newSyncTemplate = new SyncTemplate();
		BeanUtils.copyProperties(syncTemplate, newSyncTemplate);
		
		newSyncTemplate.setDataSource(null);
		newSyncTemplate.setDriverManagerDataSource(null);
		return newSyncTemplate;
	}

}
