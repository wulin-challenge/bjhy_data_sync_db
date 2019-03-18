package com.bjhy.db.converter.util;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 数据库约束辅助类
 * @author wubo
 *
 */
public class DataBaseConstraintHelper {
	
	/**
	 * 禁用oracle的相关约束
	 * @param jdbcTemplate
	 */
	public static void disableOracleConstrain(JdbcTemplate jdbcTemplate){
		String sql = "select constraint_name as CONSTRAINTNAME,table_name as TABLENAME,status as STATUS from user_constraints where constraint_type = 'R' or (constraint_type = 'U'  and constraint_name like 'UK_%')";
		List<Map<String,Object>> listMap = jdbcTemplate.queryForList(sql);
		
		for (Map<String, Object> map : listMap) {
			String alterSql = "alter table "+map.get("TABLENAME")+" disable constraint "+map.get("CONSTRAINTNAME")+"";
			try {
				jdbcTemplate.execute(alterSql);
			} catch (DataAccessException e) {
				LoggerUtils.error(e.getMessage());
			}
		}
	}
	
	/**
	 * 起用oracle的相关约束
	 * @param jdbcTemplate
	 */
	public static void enableOracleConstrain(JdbcTemplate jdbcTemplate){
		String sql = "select constraint_name as CONSTRAINTNAME,table_name as TABLENAME,status as STATUS from user_constraints where constraint_type = 'R' or (constraint_type = 'U'  and constraint_name like 'UK_%')";
		List<Map<String,Object>> listMap = jdbcTemplate.queryForList(sql);
		
		for (Map<String, Object> map : listMap) {
			String alterSql = "alter table "+map.get("TABLENAME")+" enable constraint "+map.get("CONSTRAINTNAME")+"";
			try {
				jdbcTemplate.execute(alterSql);
			} catch (DataAccessException e) {
				LoggerUtils.error(e.getMessage());
			}
		}
	}
}
