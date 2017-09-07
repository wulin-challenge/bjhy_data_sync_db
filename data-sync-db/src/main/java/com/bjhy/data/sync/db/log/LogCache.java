package com.bjhy.data.sync.db.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日志缓存
 * @author wubo
 */
public class LogCache {
	
	/**
	 * 启动日志缓存
	 */
	private static List<String> startLog = new ArrayList<String>();
	
	/**
	 * 数据源日志缓存
	 */
	private static List<String> dataSourceLog = new ArrayList<String>();
	
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 添加启动日志
	 * @param message
	 */
	public static void addStartLog(String message) {
		String format = sdf.format(new Date());
		startLog.add(format+"> start log | "+message);
	}
	
	/**
	 * 添加数据源日志
	 * @param message
	 */
	public static void addDataSourceLog(String message) {
		String format = sdf.format(new Date());
		startLog.add(format+"> dataSource log | "+message);
	}

	public static List<String> getStartLog() {
		return startLog;
	}

	public static List<String> getDataSourceLog() {
		return dataSourceLog;
	}
	
}
