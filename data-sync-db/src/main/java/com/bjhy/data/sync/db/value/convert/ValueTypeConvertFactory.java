package com.bjhy.data.sync.db.value.convert;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 值类型转换工厂
 * @author wubo
 *
 */
public class ValueTypeConvertFactory {
	
	/**
	 * 转换名称映射
	 */
	private Map<Class<?>,Set<String>> convertNameMapping = new ConcurrentHashMap<Class<?>,Set<String>>();
	
	/**
	 * 转换工厂
	 */
	private Map<String,ValueTypeConvert> convertFactory = new ConcurrentHashMap<String,ValueTypeConvert>();

	/**
	 * 得到转换名称映射
	 * @return
	 */
	public Map<Class<?>,Set<String>> getConvertNameMapping() {
		return convertNameMapping;
	}

	/**
	 * 得到转换工厂
	 * @return
	 */
	public Map<String,ValueTypeConvert> getConvertFactory() {
		return convertFactory;
	}
	
	/**
	 * 得到映射名称key
	 * @param originalClass 原来的class
	 * @param returnClass 转换后返回的class
	 * @return
	 */
	public String getMappingNameKey(Class<?> originalClass,Class<?> returnClass){
		return originalClass.getName()+"_"+returnClass.getName();
	}
	
}
