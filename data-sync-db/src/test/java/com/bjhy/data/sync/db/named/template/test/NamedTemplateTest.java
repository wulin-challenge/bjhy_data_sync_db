package com.bjhy.data.sync.db.named.template.test;

import java.util.Collections;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.loader.DataSourceLoader;
import com.bjhy.data.sync.db.named.template.test.domain.User;


public class NamedTemplateTest {
	
	public static void main(String[] args) {
		NamedTemplateTest namedTemplateTest = new NamedTemplateTest();
		NamedParameterJdbcTemplate namedNativeTemplate = namedTemplateTest.getNamedNativeTemplate();
		
		String sql = "select u.id,u.username,u.password,d.id \"d.id\" ,d.name,d.describle from wulin_sync_user u "
				+ "left join wulin_sync_dept d on u.id = d.id where username = 'wulin_1'";  
		 
		
		 SqlParameterSource ps=new BeanPropertySqlParameterSource(Collections.EMPTY_MAP);  
		 User queryForObject = namedNativeTemplate.queryForObject(sql, ps, new BeanPropertyRowMapper<User>(User.class));
		System.out.println();
	}
	
//	select u.id,u.username,u.password,d.id "user.id" ,d.name,d.describle from wulin_sync_user u left join wulin_sync_dept d on u.id = d.id


	public SyncTemplate getSyncTemplate(){
		return DataSourceLoader.getInstance().getNativeStoreTemplate();
	}
	
	/**
	 * 得到Named式的fromTemplate
	 * @return
	 */
	public NamedParameterJdbcTemplate getNamedNativeTemplate(){
		SyncTemplate fromTemplate = getSyncTemplate();
		NamedParameterJdbcTemplate jdbcTemplate  = new NamedParameterJdbcTemplate(fromTemplate);  
		return jdbcTemplate;
	}
	
	

	
	
	
}
