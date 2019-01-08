package com.bjhy.data.sync.db.value.convert;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 值类型转换注册器
 * @author wubo
 *
 */
public class ValueTypeConvertRegistry {
	
	/**
	 * 值类型转换工厂
	 */
	private ValueTypeConvertFactory valueTypeConvertFactory = new ValueTypeConvertFactory();
	
	private ReentrantLock lock = new ReentrantLock();
	
	public ValueTypeConvertRegistry() {
		//默认注册值类型转换器
		defaultRegisterValueTypeConvert();
	}

	/**
	 * 得到值类型转换工厂
	 * @return
	 */
	public ValueTypeConvertFactory getValueTypeConvertFactory() {
		return valueTypeConvertFactory;
	}
	
	/**
	 * 注册转换器
	 * @param originalClass 原来的class
	 * @param returnClass 转换后返回的class
	 * @param converter 转换器
	 */
	public void registerConverter(Class<?> originalClass,Class<?> returnClass,ValueTypeConvert converter){
		String mappingNameKey = getValueTypeConvertFactory().getMappingNameKey(originalClass, returnClass);
		try {
			lock.lock();
			getValueTypeConvertFactory().getConvertFactory().putIfAbsent(mappingNameKey, converter);
			
			Set<String> nameMappingSet = getValueTypeConvertFactory().getConvertNameMapping().get(converter.getClass());
			if(nameMappingSet == null){
				nameMappingSet = new HashSet<String>();
			}
			nameMappingSet.add(mappingNameKey);
			getValueTypeConvertFactory().getConvertNameMapping().putIfAbsent(converter.getClass(),nameMappingSet);
		} finally {
			if(lock.isLocked()){
				lock.unlock();
			}
		}
	}
	
	/**
	 * 得到值类型转换器
	 * @param originalClass 原来的class
	 * @param returnClass 转换后返回的class
	 * @return
	 */
	public ValueTypeConvert getValueTypeConvert(Class<?> originalClass,Class<?> returnClass){
		String mappingNameKey = getValueTypeConvertFactory().getMappingNameKey(originalClass, returnClass);
		return getValueTypeConvertFactory().getConvertFactory().get(mappingNameKey);
	}
	
	/**
	 * 默认注册值类型转换器
	 */
	private void defaultRegisterValueTypeConvert(){
		registerConverter(String.class,int.class,new StringConvertNumber(int.class));
		registerConverter(String.class,Integer.class,new StringConvertNumber(Integer.class));
		registerConverter(String.class,long.class,new StringConvertNumber(long.class));
		registerConverter(String.class,Long.class,new StringConvertNumber(Long.class));
		registerConverter(String.class,BigDecimal.class,new StringConvertNumber(BigDecimal.class));
		registerConverter(Null.class,StringBlank.class,new NullConvertStringBlank());
		registerConverter(StringBlank.class,Null.class,new StringBlankConvertNull());
	}
}
