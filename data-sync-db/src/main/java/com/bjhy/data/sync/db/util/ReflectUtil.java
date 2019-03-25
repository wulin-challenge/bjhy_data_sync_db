package com.bjhy.data.sync.db.util;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

/**
 * 反射工具类
 * @author wubo
 *
 */
public class ReflectUtil {
	
	private static Logger logger = Logger.getLogger(ReflectUtil.class);
	
	/**
	 * 设置属性值 
	 * @param obj 要修改的对象
	 * @param fieldName 属性名称
	 * @param Value 要修改的属性值
	 */
	public static void setFieldValue(Object obj,String fieldName,Object Value){
		
		Field declaredField;
		try {
			declaredField = obj.getClass().getDeclaredField(fieldName);
			declaredField.setAccessible(true);
	    	declaredField.set(obj, Value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			logger.error("reflect set value fail!", e);
		}
	}

}
