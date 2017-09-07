package com.bjhy.data.sync.db.test;


	import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;
	  
	public class BeanUtilsCopy { 
	  
	    /** 
	     * the beanCopierMap 
	     */  
	    private static final ConcurrentMap<String, BeanCopier> beanCopierMap = new ConcurrentHashMap<>();  
	  
	    /** 
	     * @description 两个类对象之间转换 
	     * @param source 
	     * @param target 
	     * @return 
	     * @return T 
	     */  
	    public static <T> T convert(Object source, Class<T> target) {  
	        T ret = null;  
	        if (source != null) {  
	            try {  
	                ret = target.newInstance();  
	            } catch (ReflectiveOperationException e) {  
	                throw new RuntimeException("create class[" + target.getName()  
	                        + "] instance error", e);  
	            }  
	            BeanCopier beanCopier = getBeanCopier(source.getClass(), target);  
	            beanCopier.copy(source, ret, new DeepCopyConverter(target));  
	        }  
	        return ret;  
	    }  
	  
	    public static class DeepCopyConverter implements Converter {  
	  
	        /** 
	         * The Target. 
	         */  
	        private Class<?> target;  
	  
	        /** 
	         * Instantiates a new Deep copy converter. 
	         * 
	         * @param target 
	         *            the target 
	         */  
	        public DeepCopyConverter(Class<?> target) {  
	            this.target = target;  
	        }  
	  
	        @Override  
	        public Object convert(Object value, Class targetClazz, Object methodName) {  
	            if (value instanceof List) {  
	                List values = (List) value;  
	                List retList = new ArrayList<>(values.size());  
	                for (final Object source : values) {  
	                    String tempFieldName = methodName.toString().replace("set",  
	                            "");  
	                    String fieldName = tempFieldName.substring(0, 1)  
	                            .toLowerCase() + tempFieldName.substring(1);  
	                    Class clazz = ClassUtils.getElementType(target, fieldName);  
	                    retList.add(BeanUtilsCopy.convert(source, clazz));  
	                }  
	                return retList;  
	            } else if (value instanceof Map) {  
	                // TODO 暂时用不到，后续有需要再补充  
	            } else if (!ClassUtils.isPrimitive(targetClazz)) {  
	                return BeanUtilsCopy.convert(value, targetClazz);  
	            }  
	            return value;  
	        }  
	    }  
	  
	    /** 
	     * @description 获取BeanCopier 
	     * @param source 
	     * @param target 
	     * @return 
	     * @return BeanCopier 
	     */  
	    public static BeanCopier getBeanCopier(Class<?> source, Class<?> target) {  
	        String beanCopierKey = generateBeanKey(source, target);  
	        if (beanCopierMap.containsKey(beanCopierKey)) {  
	            return beanCopierMap.get(beanCopierKey);  
	        } else {  
	            BeanCopier beanCopier = BeanCopier.create(source, target, true);  
	            beanCopierMap.putIfAbsent(beanCopierKey, beanCopier);  
	        }  
	        return beanCopierMap.get(beanCopierKey);  
	    }  
	  
	    /** 
	     * @description 生成两个类的key 
	     * @param source 
	     * @param target 
	     * @return 
	     * @return String 
	     */  
	    public static String generateBeanKey(Class<?> source, Class<?> target) {  
	        return source.getName() + "@" + target.getName();  
	    }  
	}  