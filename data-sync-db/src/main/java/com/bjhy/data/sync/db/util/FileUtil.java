package com.bjhy.data.sync.db.util;

import org.apache.commons.lang3.StringUtils;

public class FileUtil {
	
	/**
	 * 替换反斜杠,解决在windows,linux下的路径问题
	 * @param path
	 * @return
	 */
	public static String replaceSprit(String path){
		if(StringUtils.isEmpty(path)){
			return "";
		}
		
		path = path.replace("\\\\", "/"); //// Java中4个反斜杠表示一个反斜杠
		path = path.replace("\\", "/"); 
		path = path.trim();
		return path;
	}
	
	/**
	 * 替换反斜杠且以正斜杠结束,解决在windows,linux下的路径问题
	 * @param path
	 */
	public static String replaceSpritAndEnd(String path){
		path = replaceSprit(path);
		if(!path.endsWith("/")){
			path = path+"/";
		}
		return path;
	}
	
	/**
	 * 替换反斜杠且以正斜杠开始,解决在windows,linux下的路径问题
	 * @param path
	 */
	public static String replaceSpritAndStart(String path){
		path = replaceSprit(path);
		if(!path.startsWith("/")){
			path = "/"+path;
		}
		return path;
	}
	
	/**
	 * 替换反斜杠且不以正斜杠开始,解决在windows,linux下的路径问题
	 * @param path
	 */
	public static String replaceSpritAndNotStart(String path){
		path = replaceSprit(path);
		if(path.startsWith("/")){
			path = path.substring(1,path.length());
		}
		return path;
	}
	
}
