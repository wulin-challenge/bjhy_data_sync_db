package com.bjhy.data.sync.db.util;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 加载 db.properties配置文件
 * @author wubo
 */
public class DBPropertiesUtil {

	public static final String CHAT_CONFIG_PROPERTY_LOC = "/config/db.properties";
	
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
	public static String getProperty(String key){
		if(properties.getProperty(key) != null){
			return (String) properties.getProperty(key);
		}
		return null;
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

