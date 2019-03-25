package com.bjhy.data.sync.db.util;

import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * 重新Druid的JdbcUtils
 * @author wubo
 *
 */
public class DBJdbcUtil {
	private static Logger logger = Logger.getLogger(ReflectUtil.class);

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
}
