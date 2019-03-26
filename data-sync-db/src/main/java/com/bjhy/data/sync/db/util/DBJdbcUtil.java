package com.bjhy.data.sync.db.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * 重新Druid的JdbcUtils
 * @author wubo
 *
 */
public class DBJdbcUtil {
	private static Logger logger = Logger.getLogger(DBJdbcUtil.class);

	public static void close(Statement x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
        	logger.debug("close statement error", e);
        }
    }

    public static void close(ResultSet x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
        	logger.debug("close result set error", e);
        }
    }
    
    public static void close(Connection x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
        	String userName = null;
        	String url = null;
        	try {
        		userName = x.getMetaData().getUserName();
        		url = x.getMetaData().getURL();
			} catch (SQLException e1) {
				logger.error("得到元数据信息错误"+e1.getMessage());
			}
        	logger.error("userName:"+userName+",url:"+url+",close connection error : "+e.getMessage());
        }
    }
}
