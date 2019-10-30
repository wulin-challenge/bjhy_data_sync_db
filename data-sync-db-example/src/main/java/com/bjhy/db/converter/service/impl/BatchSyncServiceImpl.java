package com.bjhy.db.converter.service.impl;

import org.springframework.stereotype.Service;

import com.bjhy.data.sync.db.core.BaseAsynchronousBatchCommitCode;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.db.converter.listener.BatchSyncListener;
import com.bjhy.db.converter.service.BaseSyncService;
import com.bjhy.db.converter.util.StepUtil;

/**
 * 批量同步实现类
 * @author wubo
 *
 */
@Service
public class BatchSyncServiceImpl extends BaseSyncService{

	@Override
	public void doRunSync(SingleRunEntity singleRunEntity) {
//		StepUtil.checkSyncNotNullWhere2(singleRunEntity, "sys_region_sys_region_new", "select * ", "from sys_region", "sys_region_new", "ID", BatchSyncListener.class.getName(), null);
//		BaseAsynchronousBatchCommitCode.getInstance().addEmptyOrderTask();
//		BaseAsynchronousBatchCommitCode.getInstance().addEmptyOrderTask();
//		BaseAsynchronousBatchCommitCode.getInstance().addEmptyOrderTask();
//		BaseAsynchronousBatchCommitCode.getInstance().addEmptyOrderTask();
//		
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		
		StepUtil.checkSyncNotNullWhere2(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere2(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere2(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere2(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere2(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere2(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
		StepUtil.checkSyncNotNullWhere3NoOrder(singleRunEntity, "sys_code_sys_code_new", "select * ", "from sys_code", "sys_code_new", "ID", BatchSyncListener.class.getName(), null);
	}

	@Override
	public int order() {
		return 0;
	}

	@Override
	public String toTask() {
		return "to_old_region_task";
	}

	@Override
	public String fromTask() {
		return "from_reverse_to_old_region_task";
	}
	
	
}
