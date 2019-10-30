package com.bjhy.db.converter.listener;

import java.util.Map;

import com.bjhy.data.sync.db.collection.ConcurrentSet;
import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;
import com.bjhy.data.sync.db.loader.DataSourceLoader;

/**
 * 批量同步步骤监听
 * @author wulin
 *
 */
public class BatchSyncListener extends SingleStepListener{
	
	private ConcurrentSet<String> Ids = new ConcurrentSet<>();
	
	@Override
	public Map<String, Object> rowCall(SyncLogicEntity syncLogicEntity, Map<String, Object> rowParam) {
		String uuid = DataSourceLoader.getUUID();
		if(Ids.contains(uuid)) {
			System.out.println();
		}
		Ids.add(uuid);
		rowParam.put("ID", uuid);
		return rowParam;
	}

}
