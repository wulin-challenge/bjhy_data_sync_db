package com.bjhy.data.sync.db.natived.dao.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.natived.dao.StepStoreDao;
import com.bjhy.data.sync.db.natived.domain.StepStoreEntity;
import com.bjhy.data.sync.db.util.DataSourceUtil;
import com.bjhy.data.sync.db.util.LoggerUtils;

public class StepStoreDaoImpl implements StepStoreDao{
	
	private NamedParameterJdbcTemplate namedNativeTemplate;
	
	public StepStoreDaoImpl() {
		SyncTemplate enableNativeSyncTemplate = DataSourceUtil.getInstance().getEnableNativeSyncTemplate();
		NamedParameterJdbcTemplate jdbcTemplate  = new NamedParameterJdbcTemplate(enableNativeSyncTemplate);  
		this.namedNativeTemplate = jdbcTemplate;
	}

	@Override
	public void store(StepStoreEntity stepStoreEntity) {
		
		String insertSql = "insert into step_store(id,isRepairSync,fromDataSourceName, fromDataSourceNumber,toDataSourceName,toDataSourceNumber,fromTask,toTask,toTableName,stepUniquelyIdentifies,fromDataNumber,toDataNumber,singleStepByte)"
				+ " values (:id,:isRepairSync,:fromDataSourceName, :fromDataSourceNumber,:toDataSourceName,:toDataSourceNumber,:fromTask,:toTask,:toTableName,:stepUniquelyIdentifies,:fromDataNumber,:toDataNumber,:singleStepByte) ";//：后的命名要与列名一致  
		
		SqlParameterSource ps=new BeanPropertySqlParameterSource(stepStoreEntity);//从stepStoreEntity中取出数据，与sql语句中一一对应将数据换进去  
		KeyHolder keyHolder=new GeneratedKeyHolder();  
		namedNativeTemplate.update(insertSql, ps, keyHolder); 
		
	}

	@Override
	public void update(StepStoreEntity stepStoreEntity) {
		String updateSql = "update step_store set isRepairSync=:isRepairSync,fromDataSourceName=:fromDataSourceName, fromDataSourceNumber=:fromDataSourceNumber, toDataSourceName=:toDataSourceName, toDataSourceNumber=:toDataSourceNumber,fromTask=:fromTask,toTask=:toTask,toTableName=:toTableName,stepUniquelyIdentifies=:stepUniquelyIdentifies,fromDataNumber=:fromDataNumber,toDataNumber=:toDataNumber,singleStepByte=:singleStepByte where id=:id";
		
		SqlParameterSource ps=new BeanPropertySqlParameterSource(stepStoreEntity);//从stepStoreEntity中取出数据，与sql语句中一一对应将数据换进去  
		namedNativeTemplate.update(updateSql, ps);
	}
	
	@Override
	public void updateByDataNumber(StepStoreEntity stepStoreEntity) {
		String updateSql = "update step_store set fromDataNumber=:fromDataNumber,toDataNumber=:toDataNumber where id=:id";
		
		SqlParameterSource ps=new BeanPropertySqlParameterSource(stepStoreEntity);//从stepStoreEntity中取出数据，与sql语句中一一对应将数据换进去  
		namedNativeTemplate.update(updateSql, ps);
	}

	@Override
	public void updateByRepirSync(StepStoreEntity stepStoreEntity) {
		String updateSql = "update step_store set isRepairSync=:isRepairSync where id=:id";
		
		SqlParameterSource ps=new BeanPropertySqlParameterSource(stepStoreEntity);//从stepStoreEntity中取出数据，与sql语句中一一对应将数据换进去  
		namedNativeTemplate.update(updateSql, ps);
	}

	@Override
	public StepStoreEntity findOneById(StepStoreEntity stepStoreEntity) {

		 String sql = "select *  from step_store where id=:id";  
		 
		 SqlParameterSource ps=new BeanPropertySqlParameterSource(stepStoreEntity);  
		 StepStoreEntity newStepStoreEntity = null;
		 try {
			 newStepStoreEntity = namedNativeTemplate.queryForObject(sql, ps, new BeanPropertyRowMapper<StepStoreEntity>(StepStoreEntity.class));
		} catch (DataAccessException e) {
			LoggerUtils.warn("当前查出的数据库位空! 错误信息:"+e.getMessage());
		}  
		return newStepStoreEntity;
	}
	
	@Override
	public StepStoreEntity findOneByTaskAndName(StepStoreEntity stepStoreEntity) {
		String selectSql = "select id,fromDataSourceName, fromDataSourceNumber,toDataSourceName,toDataSourceNumber,fromTask,toTask,toTableName,stepUniquelyIdentifies,fromDataNumber,toDataNumber from step_store "
				+ " where fromDataSourceNumber=:fromDataSourceNumber and toDataSourceNumber=:toDataSourceNumber and fromTask=:fromTask and toTask=:toTask and toTableName=:toTableName and stepUniquelyIdentifies=:stepUniquelyIdentifies";
		
		
		 SqlParameterSource ps=new BeanPropertySqlParameterSource(stepStoreEntity);  
		 StepStoreEntity newStepStoreEntity = null;
		 try {
			 newStepStoreEntity = namedNativeTemplate.queryForObject(selectSql, ps, new BeanPropertyRowMapper<StepStoreEntity>(StepStoreEntity.class));
		} catch (DataAccessException e) {
			LoggerUtils.warn("当前查出的数据库位空! 错误信息:"+e.getMessage());
		}  
		return newStepStoreEntity;
	}
	
	@Override
	public StepStoreEntity findOneByTaskAndNameAndIsRepairSync(StepStoreEntity stepStoreEntity) {
		String selectSql = "select id,isRepairSync,fromDataSourceName, fromDataSourceNumber,toDataSourceName,toDataSourceNumber,fromTask,toTask,toTableName,stepUniquelyIdentifies,fromDataNumber,toDataNumber from step_store "
				+ " where isRepairSync=:isRepairSync and fromDataSourceNumber=:fromDataSourceNumber and toDataSourceNumber=:toDataSourceNumber and fromTask=:fromTask and toTask=:toTask and toTableName=:toTableName and stepUniquelyIdentifies=:stepUniquelyIdentifies";
		
		 SqlParameterSource ps=new BeanPropertySqlParameterSource(stepStoreEntity);  
		 StepStoreEntity newStepStoreEntity = null;
		 try {
			 newStepStoreEntity = namedNativeTemplate.queryForObject(selectSql, ps, new BeanPropertyRowMapper<StepStoreEntity>(StepStoreEntity.class));
		} catch (DataAccessException e) {
			LoggerUtils.warn("当前查出的数据库位空! 错误信息:"+e.getMessage());
		}  
		return newStepStoreEntity;
	}

	@Override
	public List<StepStoreEntity> findAll() {
		String selectSql = "select id,isRepairSync,fromDataSourceName, fromDataSourceNumber,toDataSourceName,toDataSourceNumber,fromTask,toTask,toTableName,stepUniquelyIdentifies,fromDataNumber,toDataNumber,singleStepByte from step_store";
		
		 List<StepStoreEntity> newStepStoreEntityList = null;
		 try {
			 newStepStoreEntityList = namedNativeTemplate.query(selectSql, new BeanPropertyRowMapper<StepStoreEntity>(StepStoreEntity.class));
		} catch (DataAccessException e) {
			LoggerUtils.warn("当前查出的数据库位空! 错误信息:"+e.getMessage());
		}  
		return newStepStoreEntityList;
	}
}
