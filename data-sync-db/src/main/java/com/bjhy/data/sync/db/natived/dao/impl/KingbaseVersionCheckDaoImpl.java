package com.bjhy.data.sync.db.natived.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.bjhy.data.sync.db.natived.dao.BaseVersionCheckDao;
import com.bjhy.data.sync.db.natived.dao.VersionCheckDao;
import com.bjhy.data.sync.db.natived.domain.VersionCheckEntity;
import com.bjhy.data.sync.db.util.LoggerUtils;

public class KingbaseVersionCheckDaoImpl extends BaseVersionCheckDao implements VersionCheckDao{
	

	@Override
	public void store(VersionCheckEntity versionCheckEntity) {
		String sql = "insert into version_check(id,fromDataSourceName, fromDataSourceNumber,toDataSourceName,toDataSourceNumber,fromTask,toTask,toTableName,stepUniquelyIdentifies,currentCheckVersion,beforeCheckVersion)"
				+ " values (:id,:fromDataSourceName, :fromDataSourceNumber,:toDataSourceName,:toDataSourceNumber,:fromTask,:toTask,:toTableName,:stepUniquelyIdentifies,:currentCheckVersion,:beforeCheckVersion) ";//：后的命名要与列名一致  
		SqlParameterSource ps=new BeanPropertySqlParameterSource(versionCheckEntity);//从versionCheckEntity中取出数据，与sql语句中一一对应将数据换进去  
		KeyHolder keyHolder=new GeneratedKeyHolder();  
		namedNativeTemplate.update(sql, ps, keyHolder); 
	}

	@Override
	public void update(VersionCheckEntity versionCheckEntity) {
		String sql = "update version_check set fromDataSourceName=:fromDataSourceName, fromDataSourceNumber=:fromDataSourceNumber, toDataSourceName=:toDataSourceName, toDataSourceNumber=:toDataSourceNumber,fromTask=:fromTask,toTask=:toTask,toTableName=:toTableName,stepUniquelyIdentifies=:stepUniquelyIdentifies,currentCheckVersion=:currentCheckVersion,beforeCheckVersion=:beforeCheckVersion where id=:id";
		
		SqlParameterSource ps=new BeanPropertySqlParameterSource(versionCheckEntity);//从versionCheckEntity中取出数据，与sql语句中一一对应将数据换进去  
		namedNativeTemplate.update(sql, ps);
	}

	@Override
	public VersionCheckEntity findOneByTaskAndTableName(VersionCheckEntity versionCheckEntity) {
//		String fromTask, String toTask,String stepUniquelyIdentifies, String toTableName
		
		 String sql = "select *  from version_check where fromDataSourceNumber=:fromDataSourceNumber and toDataSourceNumber=:toDataSourceNumber and fromTask=:fromTask and toTask=:toTask and stepUniquelyIdentifies=:stepUniquelyIdentifies";  
		 
		 SqlParameterSource ps=new BeanPropertySqlParameterSource(versionCheckEntity);  
		 VersionCheckEntity newVersionCheckEntity = null;
		 try {
			newVersionCheckEntity = namedNativeTemplate.queryForObject(sql, ps, new BeanPropertyRowMapper<VersionCheckEntity>(VersionCheckEntity.class));
		} catch (DataAccessException e) {
			LoggerUtils.warn("当前查出的数据库位空! 错误信息:"+e.getMessage());
		}  
		return newVersionCheckEntity;
	}
}
