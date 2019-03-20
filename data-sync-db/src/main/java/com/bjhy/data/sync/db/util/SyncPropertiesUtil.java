package com.bjhy.data.sync.db.util;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

/**
 * 加载 db.properties配置文件
 * @author wubo
 */
public class SyncPropertiesUtil {

	public static final String CHAT_CONFIG_PROPERTY_LOC = "/config/sync.properties";
	
	private static PropertiesConfiguration properties;
	
	static{
		try {
			properties = new PropertiesConfiguration(new File(System.getProperty("user.dir") + CHAT_CONFIG_PROPERTY_LOC));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 随机拿到第一个，该方法一般很少使用，除非确定当前的key值是全系统唯一的
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getProperty(String key){
		if(properties.getProperty(key) != null){
			String value = null;
			Object property = properties.getProperty(key);
			if(property instanceof List){
				List<String> propertyArray =  (List<String>) property;
				int i=0;
				for (String s : propertyArray) {
					if(i==0){
						value = s;
					}else{
						value +=","+s;
					}
					i++;
				}
			}else{
				value = (String) properties.getProperty(key);
			}
			
			return value;
		}
		return null;
	}
	
	public static boolean getProperty(String key,boolean defaultValue){
		String value = getProperty(key);
		if(StringUtils.isBlank(value)){
			return defaultValue;
		}
		if("true".equals(value)){
			return true;
		}
		return false;
	}
	
	/**
	 * 随机设置到第一个，该方法一般很少使用，除非确定当前的key值是全系统唯一的
	 * @param key
	 * @param value
	 * @return 返回true表示设置成功,false则反之
	 */
	public static Boolean setProperty(String key,String value){
		Boolean flag = false;
		try {
			if(properties.getProperty(key) != null){
				properties.setProperty(key, value);
				properties.save();
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		return flag;
	}
}

