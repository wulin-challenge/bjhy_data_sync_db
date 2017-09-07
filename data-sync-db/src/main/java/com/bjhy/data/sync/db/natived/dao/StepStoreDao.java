package com.bjhy.data.sync.db.natived.dao;

import java.util.List;

import com.bjhy.data.sync.db.natived.domain.StepStoreEntity;

/**
 * 版本检测dao
 * @author wubo
 *
 */
public interface StepStoreDao {
	
	/**
	 * 保存
	 * @param StepStoreEntity 版本检测实体
	 */
	public void store(StepStoreEntity stepStoreEntity);
	
	/**
	 * 更新
	 * @param StepStoreEntity 版本检测实体
	 */
	public void update(StepStoreEntity stepStoreEntity);
	
	/**
	 *  更新来源数据和目标数据
	 * @param StepStoreEntity 版本检测实体
	 */
	public void updateByDataNumber(StepStoreEntity stepStoreEntity);
	
	/**
	 *  更新修复同步标示
	 * @param StepStoreEntity 版本检测实体
	 */
	public void updateByRepirSync(StepStoreEntity stepStoreEntity);
	
	/**
	 * 通过id查找StepStoreEntity
	 * @param StepStoreEntity
	 * @return StepStoreEntity
	 */
	public StepStoreEntity findOneById(StepStoreEntity stepStoreEntity);
	
	/**
	 * 通过 fromTask,toTask,toTableName,stepUniquelyIdentifies 查询数据
	 * @param stepStoreEntity
	 * @return
	 */
	public StepStoreEntity findOneByTaskAndName(StepStoreEntity stepStoreEntity);
	
	/**
	 * 通过 fromTask,toTask,toTableName,stepUniquelyIdentifies,isReqairSync 查询数据
	 * @param stepStoreEntity
	 * @return
	 */
	public StepStoreEntity findOneByTaskAndNameAndIsRepairSync(StepStoreEntity stepStoreEntity);
	/**
	 * 查找所有数据,除了  singleStepByte 字段
	 * @return
	 */
	public List<StepStoreEntity> findAll();

}
