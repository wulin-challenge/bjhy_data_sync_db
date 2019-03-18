package com.bjhy.db.converter.service.impl;

import org.springframework.stereotype.Service;

import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.db.converter.listener.BaseInfoListener;
import com.bjhy.db.converter.service.BaseSyncService;
import com.bjhy.db.converter.util.StepUtil;
import com.bjhy.db.converter.util.YzColumnMappingUtil;

/**
 * 同步实现类
 * @author wubo
 *
 */
@Service
public class SyncServiceImpl extends BaseSyncService{

	@Override
	public void doRunSync(SingleRunEntity singleRunEntity) {
		StepUtil.checkSyncNotNullWhere2(singleRunEntity, "CRIMINAL_BASE_INFO_CRIMINAL_BASEINFO", "select * ", "from CRIMINAL_BASE_INFO", "CRIMINAL_BASEINFO", "ID", BaseInfoListener.class.getName(), YzColumnMappingUtil.baseInfoMapping());
	}

	@Override
	public int order() {
		return 0;
	}
}
