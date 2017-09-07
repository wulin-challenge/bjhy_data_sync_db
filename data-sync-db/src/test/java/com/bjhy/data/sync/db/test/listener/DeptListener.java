package com.bjhy.data.sync.db.test.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;

public class DeptListener extends SingleStepListener{

	@Override
	public Map<String, Object> rowCall(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam) {
		String sql = "select * from wulin_sync_dept";
		List<Map<String, Object>> queryForList = syncLogicEntity.getNamedFromTemplate().queryForList(sql, Collections.EMPTY_MAP);
		for (Map<String, Object> map : queryForList) {
			String name = (String)map.get("name");
			System.out.println(name);
			
		}
		System.out.println("99999999999999-----------");
		return rowParam;
	}
	
	public String xx(String yy){
		System.out.println(yy);
		return "888"+yy;
	}
	
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> forName = Class.forName("com.bjhy.data.sync.db.test.listener.DeptListener");
		
		Object newInstance = forName.newInstance();
		Method declaredMethod = forName.getDeclaredMethod("xx", String.class);
		Object invoke = declaredMethod.invoke(newInstance, "nnnnnn");
		System.out.println(invoke.toString());
		
		
		
	}

}
