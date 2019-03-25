package com.bjhy.data.sync.db.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.util.JdbcUtils;
import com.bjhy.data.sync.db.util.DBJdbcUtil;

/**
 * 重写 OracleValidConnectionChecker#isValidConnection(...) 方法
 * @author wubo
 *
 */
public class DBOracleValidConnectionChecker {
	
	private int               timeout              = 1;

    private String            defaultValidateQuery = "SELECT 'x' FROM DUAL";

	public boolean isValidConnection(Connection conn, String validationQuery, int validationQueryTimeout) throws Exception {
        if (validationQuery == null || validationQuery.isEmpty()) {
        	validationQuery = this.defaultValidateQuery;
        }

        if (conn.isClosed()) {
            return false;
        }

        if (conn instanceof DruidPooledConnection) {
            conn = ((DruidPooledConnection) conn).getConnection();
        }

        if (conn instanceof ConnectionProxy) {
            conn = ((ConnectionProxy) conn).getRawObject();
        }

        if (validationQuery == null || validationQuery.isEmpty()) {
            return true;
        }

        int queryTimeout = validationQueryTimeout < 0 ? timeout : validationQueryTimeout;

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            stmt.setQueryTimeout(queryTimeout);
            rs = stmt.executeQuery(validationQuery);
            return true;
        } finally {
        	DBJdbcUtil.close(rs);
        	DBJdbcUtil.close(stmt);
        }
    }

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getDefaultValidateQuery() {
		return defaultValidateQuery;
	}

	public void setDefaultValidateQuery(String defaultValidateQuery) {
		this.defaultValidateQuery = defaultValidateQuery;
	}
}
