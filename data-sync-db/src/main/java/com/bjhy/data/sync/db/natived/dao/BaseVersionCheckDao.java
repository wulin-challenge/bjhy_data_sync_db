package com.bjhy.data.sync.db.natived.dao;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.util.DataSourceUtil;

/**
 * 验证dao的抽象实现类
 * @author wubo
 *
 */
public abstract class BaseVersionCheckDao implements VersionCheckDao {

	protected NamedParameterJdbcTemplate namedNativeTemplate;

	public BaseVersionCheckDao() {
		SyncTemplate enableNativeSyncTemplate = DataSourceUtil.getInstance().getEnableNativeSyncTemplate();
		NamedParameterJdbcTemplate jdbcTemplate  = new NamedParameterJdbcTemplate(enableNativeSyncTemplate);  
		this.namedNativeTemplate = jdbcTemplate;
	}
}
