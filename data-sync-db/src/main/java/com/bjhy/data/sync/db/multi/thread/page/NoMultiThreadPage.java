package com.bjhy.data.sync.db.multi.thread.page;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.MultiThreadPage;
import com.bjhy.data.sync.db.util.LoggerUtils;

/**
 * 没有多线程的实现
 * @author wubo
 *
 */
@SuppressWarnings("unchecked")
public class NoMultiThreadPage implements MultiThreadPage{
	
	private SyncLogicEntity syncLogicEntity;
	
	public NoMultiThreadPage(SyncLogicEntity syncLogicEntity) {
		this.syncLogicEntity = syncLogicEntity;
	}

	/**
	 * 迭代次数为1次
	 */
	@Override
	public Integer pageIterations() {
		return 1;
	}

	@Override
	public Integer stepMaxThreadNumber() {
		return 1;
	}

	@Override
	public List<Map<String, Object>> pageData(int index) {
		String fromSql = syncLogicEntity.getSingleStepSyncConfig().getFromSql();
		List<Map<String, Object>> pageFromData = null;
				
		try {
			pageFromData = syncLogicEntity.getNamedFromTemplate().queryForList(fromSql, Collections.EMPTY_MAP);
		} catch (DataAccessException e) {
			LoggerUtils.error(e.getMessage());
			e.printStackTrace();
		}
		return pageFromData;
	}

}
