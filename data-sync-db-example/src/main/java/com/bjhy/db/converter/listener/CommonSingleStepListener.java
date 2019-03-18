package com.bjhy.db.converter.listener;

import java.util.Map;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;

/**
 * 公共步骤监听
 * @author ThinkPad
 *
 */
public class CommonSingleStepListener extends SingleStepListener{
	
	@Override
	public Map<String, Object> rowCall(SyncLogicEntity syncLogicEntity, Map<String, Object> rowParam) {
		return rowParam;
	}

}
