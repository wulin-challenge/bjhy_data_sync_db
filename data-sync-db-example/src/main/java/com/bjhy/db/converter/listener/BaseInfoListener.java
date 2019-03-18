package com.bjhy.db.converter.listener;

import java.util.Map;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;

/**
 * baseInfo步骤监听
 * @author ThinkPad
 *
 */
public class BaseInfoListener extends SingleStepListener{
	
	@Override
	public Map<String, Object> rowCall(SyncLogicEntity syncLogicEntity, Map<String, Object> rowParam) {
		String sexName = (String) rowParam.get("SEX_CODE");
		
		if(sexName != null && sexName.equals("MALE")){
			sexName = "男";
		}else if(sexName != null && sexName.equals("FEMALE")){
			sexName = "女";
		}
		rowParam.put("SEX_NAME", sexName);
		
		return rowParam;
	}

}
