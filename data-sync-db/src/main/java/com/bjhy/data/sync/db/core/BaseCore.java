package com.bjhy.data.sync.db.core;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.domain.SyncPageRowEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;
import com.bjhy.data.sync.db.loader.DataSourceLoader;
import com.bjhy.data.sync.db.util.LoggerUtils;
import com.bjhy.data.sync.db.util.SyncPropertiesUtil;
import com.bjhy.data.sync.db.validation.SyncStepValidationStore;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

/**
 * 这是核心BaseCore,真正的逻辑都在这里面
 * @author wubo
 *
 */
public class BaseCore {
	
	/**
	 * 同步入口
	 */
	public void syncEntry(SingleStepSyncConfig singleStepSyncConfig){
		try {
			//是否自动修复
			Boolean isAutoRepair = singleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().getIsAutoRepair();
			if(isAutoRepair){
				//同步步骤拦截器
				SyncStepValidationStore.getInstance().intercept(singleStepSyncConfig);
			}
			
			//是否为只同步一次步骤
			isRunOnlyOne(singleStepSyncConfig);
		} catch (Exception e) {
			String dataSourceName = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
			String dataSourceNumber = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
			String toTableName =singleStepSyncConfig.getToTableName();
			e.printStackTrace();
			LoggerUtils.error("这是同步入口最外层  BaseCore ,最后向外抛出的 RuntimeException 异常. 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber+" 第49 行 抛出的错误信息: "+e.getMessage());
		}
	}
	
	/**
	 * 是否为只同步一次步骤
	 * @param singleStepSyncConfig
	 */
	private void isRunOnlyOne(SingleStepSyncConfig singleStepSyncConfig){
		Boolean isThisOnlyOneSync = singleStepSyncConfig.getIsThisOnlyOneSync();
		
		if(isThisOnlyOneSync){
			Boolean isThisOnlyOneConfigFile = singleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().getIsThisOnlyOne();
			if(isThisOnlyOneConfigFile){
				isSyncNullValue(singleStepSyncConfig);
			}else{
				String dataSourceName = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
				String dataSourceNumber = singleStepSyncConfig.getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
				String toTableName = singleStepSyncConfig.getToTableName();
				LoggerUtils.info("[当前为只同步一次步骤,该步骤已经被同步一次,当前不在同步] 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber);
			}
			
		}else{
			isSyncNullValue(singleStepSyncConfig);
		}
		
	}
	
	/**
	 * 是否同步空值
	 * @param singleStepSyncConfig
	 */
	private void isSyncNullValue(SingleStepSyncConfig singleStepSyncConfig){
		Boolean isSyncNullValue = singleStepSyncConfig.getSingleRunEntity().getBaseRunEntity().getIsSyncNullValue();
		
		if(isSyncNullValue){
			singleStepSyncConfig.setIsSyncNullValue(true);
		}
		
		reflectGenerationListener(singleStepSyncConfig);//反射生成监听器
		assembleStepParamsLogic(singleStepSyncConfig);
	}
	
	/**
	 * 反射生成监听器
	 */
	@SuppressWarnings("unchecked")
	private void reflectGenerationListener(SingleStepSyncConfig singleStepSyncConfig){
		String singleStepListenerName = singleStepSyncConfig.getSingleStepListenerName();
		
		if(StringUtils.isNotEmpty(singleStepListenerName)){
			try {
				Class<SingleStepListener> forName = (Class<SingleStepListener>) Class.forName(singleStepListenerName);
				SingleStepListener newInstance = forName.newInstance();
				singleStepSyncConfig.setSingleStepListener(newInstance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 组装同步参数配置
	 * @param singleStepSyncConfig
	 */
	private void assembleStepParamsLogic(SingleStepSyncConfig singleStepSyncConfig){
		SyncLogicEntity syncLogicEntity = new SyncLogicEntity();
		syncLogicEntity.setSingleStepSyncConfig(singleStepSyncConfig);
		syncLogicEntity.setCheckVersion(DataSourceLoader.getUUID());
		parseStepSyncConfig(syncLogicEntity);//解析步骤同步参数,封装 SyncLogicEntity bean
		
		//baseMultiThreadCore
		BaseMultiThreadCore baseMultiThreadCore = new BaseMultiThreadCore();
		baseMultiThreadCore.threadSyncEvent(syncLogicEntity);
	}
	
	
	
	/**
	 * 解析步骤同步参数,封装 SyncLogicEntity bean
	 */
	public void parseStepSyncConfig(SyncLogicEntity syncLogicEntity){
		setSyncColumns(syncLogicEntity);//设置要同步的列
		versionCheck(syncLogicEntity);//版本检查
		buildDllSql(syncLogicEntity);//构建inserSql和updateSql和checkSql和DeleteSql语句(即dll)
	}
	
	/**
	 * 版本检查
	 * @param syncLogicEntity
	 */
	private void versionCheck(SyncLogicEntity syncLogicEntity){
		Boolean isAddVersionCheckFilter = syncLogicEntity.getSingleStepSyncConfig().getIsAddVersionCheckFilter();
		if(isAddVersionCheckFilter){
			String syncVersionCheck = VersionCheckCore.SYNC_VERSION_CHECK;
			syncLogicEntity.getSingleStepSyncConfig().getAddStaticFromColumns().put(syncVersionCheck, syncLogicEntity.getCheckVersion());
			VersionCheckCore versionCheckCore = new VersionCheckCore(syncLogicEntity);
			syncLogicEntity.setVersionCheckCore(versionCheckCore);
		}
	}
	
	/**
	 * 构建inserSql和updateSql和checkSql和DeleteSql语句(即dll)
	 * @param syncLogicEntity
	 * @param rowColumns 一行的列
	 */
	void buildDllSql(SyncLogicEntity syncLogicEntity){
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		
		String updateColumn = syncLogicEntity.getSingleStepSyncConfig().getUpdateColumn();
		String updateWhere = syncLogicEntity.getSingleStepSyncConfig().getUpdateWhere();
		//构建updateWhere
		if(updateWhere == null){
			updateWhere = "WHERE "+updateColumn+"=:"+updateColumn;
		}
		updateWhere = updateWhere.toUpperCase();
		
		Set<String> syncColumns = syncLogicEntity.getSyncColumns();
		
		StringBuffer insert = new StringBuffer("INSERT INTO "+toTableName+"(");
		StringBuffer values = new StringBuffer(" VALUES(");
		StringBuffer update = new StringBuffer("UPDATE "+toTableName+" SET ");
		
		//拆分行数据
		SyncPageRowEntity syncPageRowEntity = syncLogicEntity.getSingleStepSyncConfig().getSyncPageRowEntity();
		//分页行的updatesql
		StringBuffer pageRowUpdateSql =  new StringBuffer();
		
		//拆分行数据(insert语句拼装)
		if(syncPageRowEntity != null){
			syncPageRowEntity.getPageRowUpdateColumnSqlList().clear();
			buildPageRowInsertDll(toTableName,syncLogicEntity);
		}
		
		//当前列数
		int currentColumn = 1;
		int columnTotal = syncColumns.size();
		for (String column : syncColumns) {
			
			//拆分行数据(update语句拼装)
			if(syncPageRowEntity != null){
				buildPageRowUpdateDll(currentColumn,columnTotal, column, pageRowUpdateSql,toTableName,updateWhere, syncLogicEntity);
			}
			
			if(currentColumn == 1){
				insert.append(column);
				values.append(":"+column);
				update.append(column+"=:"+column);
			}else{
				insert.append(","+column);
				values.append(",:"+column);
				update.append(","+column+"=:"+column);
			}
			currentColumn++;
		}
		insert.append(")");
		values.append(")");
		
		update.append(" "+updateWhere);
		
		//设置dll语句
		syncLogicEntity.setUpdateSql(update.toString());
		syncLogicEntity.setInsertSql(insert.toString()+values.toString());
		syncLogicEntity.setCheckSql("SELECT COUNT(1) NUM_ FROM  "+toTableName+" "+updateWhere);
		syncLogicEntity.setDeleteSql("DELETE FROM  "+toTableName+" "+updateWhere);
	}
	
	/**
	 * 将一行数据拆分成多条数据然后采用多线程的方式进行插入(update语句拼装)
	 * @param currentColumn 当前字段列
	 * @param column字段列
	 * @param pageRowUpdateSql 分页的update语句
	 * @param syncLogicEntity 同步逻辑实体
	 */
	void buildPageRowUpdateDll(int currentColumn,int columnTotal,String column,StringBuffer pageRowUpdateSql,String toTableName,String updateWhere,SyncLogicEntity syncLogicEntity){
		//拆分行数据
		SyncPageRowEntity syncPageRowEntity = syncLogicEntity.getSingleStepSyncConfig().getSyncPageRowEntity();
		int pageRowColumns = syncPageRowEntity.getPageRowColumns();//分页的列数
		
		//开始
		if((currentColumn%pageRowColumns) ==1){
			pageRowUpdateSql.delete(0, pageRowUpdateSql.length() ==0?0:(pageRowUpdateSql.length()));
			pageRowUpdateSql.append("UPDATE "+toTableName+" SET ");
			pageRowUpdateSql.append(column+"=:"+column);
		}else{
			pageRowUpdateSql.append(","+column+"=:"+column);
		}
		//结束
		if((currentColumn%pageRowColumns) ==0 || columnTotal == currentColumn){
			String pageRowUpdateSql2 = pageRowUpdateSql.append(" "+updateWhere).toString();
			syncPageRowEntity.getPageRowUpdateColumnSqlList().add(pageRowUpdateSql2);
		}
	}
	
	/**
	 * 将一行数据拆分成多条数据然后采用多线程的方式进行插入(insert语句拼装)
	 * @param syncLogicEntity
	 */
	void buildPageRowInsertDll(String toTableName,SyncLogicEntity syncLogicEntity){
		//拆分行数据
		SyncPageRowEntity syncPageRowEntity = syncLogicEntity.getSingleStepSyncConfig().getSyncPageRowEntity();
		List<String> pageRowInsertColumns = syncPageRowEntity.getPageRowInsertColumns();
		
		StringBuffer pageRowInsertSql = new StringBuffer("INSERT INTO "+toTableName+"(");
		StringBuffer pageRowInsertValuesSql = new StringBuffer(" VALUES(");
		
		//当前列数
		int currentColumn = 1;
		for (String column : pageRowInsertColumns) {
			column = column.toUpperCase();//将当前列传为大写
			if(currentColumn == 1){
				pageRowInsertSql.append(column);
				pageRowInsertValuesSql.append(":"+column);
			}else{
				pageRowInsertSql.append(","+column);
				pageRowInsertValuesSql.append(",:"+column);
			}
			currentColumn++;
		}
		
		pageRowInsertSql.append(")");
		pageRowInsertValuesSql.append(")");
		
		syncPageRowEntity.setPageRowInsertColumnSql(pageRowInsertSql.toString()+pageRowInsertValuesSql.toString());
	}
	
	/**
	 * 设置要同步的列
	 * @param syncLogicEntity
	 */
	private void setSyncColumns(SyncLogicEntity syncLogicEntity){
		DataSource fromDataSource = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getDataSource();
		DataSource toDataSource = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getToTemplate().getDataSource();
		try (Connection fromconnection = fromDataSource.getConnection();Connection toConnection = toDataSource.getConnection()){
			List<String> fromColumns = getColumnNames(syncLogicEntity,fromconnection, null, syncLogicEntity.getSingleStepSyncConfig().getFromSql());
			
			List<String> toColumns = getColumnNames(syncLogicEntity,toConnection, syncLogicEntity.getSingleStepSyncConfig().getToTableName(), null);
			setToColumns(syncLogicEntity, toColumns);;
			refreshSyncColumns(syncLogicEntity, fromColumns,null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		addStaticColumns(syncLogicEntity,null);//添加静态列
		removeFromColumns(syncLogicEntity);// 剔除不需要的列
	}
	
	/**
	 * 设置目标表的所有列并转大写
	 * @param syncLogicEntity
	 * @param toColumns
	 */
	private void setToColumns(SyncLogicEntity syncLogicEntity,List<String> toColumns){
		for (String toColumn : toColumns) {
			syncLogicEntity.getToColumns().add(toColumn.toUpperCase());
		}
	}
	
	/**
	 * 刷新要同步的列
	 * @param syncLogicEntity
	 * @param fromColumns
	 */
	void refreshSyncColumns(SyncLogicEntity syncLogicEntity,List<String> fromColumns,Map<String, Object> rowParam){
		//添加目标表中存在的列
		Set<String> toColumns = syncLogicEntity.getToColumns();
		for (String column : fromColumns) {
			if(toColumns.contains(column.toUpperCase())){
				if(!syncLogicEntity.getSyncColumns().contains(column.toUpperCase())){
					syncLogicEntity.getSyncColumns().add(column.toUpperCase());
				}
			}	
		}
		
		//必须保证行参数中也存在同步的列
		if(rowParam != null && !rowParam.isEmpty()){
			List<String> newSyncColumn = new ArrayList<String>();
			for (String column : syncLogicEntity.getSyncColumns()) {
				if(rowParam.containsKey(column)){
					newSyncColumn.add(column);
				}
			}
			
			syncLogicEntity.getSyncColumns().clear();
			syncLogicEntity.getSyncColumns().addAll(newSyncColumn);
		}
	}
	
	/**
	 * 剔除不需要的列
	 * @param syncLogicEntity
	 */
	void removeFromColumns(SyncLogicEntity syncLogicEntity){
		List<String> removeFromColumns = syncLogicEntity.getSingleStepSyncConfig().getRemoveFromColumns();
		for (String column : removeFromColumns) {
			column = column.trim().toUpperCase();
			if(syncLogicEntity.getSyncColumns().contains(column)){
				syncLogicEntity.getSyncColumns().remove(column);
			}
		}
		
	}
	
	/**
	 * 添加静态列
	 * @param syncLogicEntity
	 */
	void addStaticColumns(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		Map<String, Object> addStaticFromColumns = syncLogicEntity.getSingleStepSyncConfig().getAddStaticFromColumns();
		Set<Entry<String, Object>> entrySet = addStaticFromColumns.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			
			syncLogicEntity.getSyncColumns().add(entry.getKey().toUpperCase());
			
			if(rowParam != null && !rowParam.isEmpty() && !rowParam.containsKey(entry.getKey().toUpperCase())){
				rowParam.put(entry.getKey().toUpperCase(), entry.getValue());
			}
		}
		
	}
	
	/**
	 * 得到对应数据表的列名
	 * @param conn
	 * @param tableName
	 * @return
	 */
	public List<String> getColumnNames(SyncLogicEntity syncLogicEntity,Connection conn, String tableName,String sql){
		String selectSql = "SELECT * FROM " + tableName;
		if(sql != null){
			selectSql = sql;
		}
		
		selectSql = "select * from ("+selectSql+") a where 1=2";
		List<String> columnNames = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(selectSql);
				ResultSet rs = pst.executeQuery();){
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				columnNames.add(rsmd.getColumnLabel(i).toUpperCase());
			}
		} catch (SQLException e) {
			
			String dataSourceName = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
			String dataSourceNumber = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
			String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
			
			LoggerUtils.error("执行的语句是:"+selectSql+"表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber+" , 错误信息  : "+e.getMessage());
			throw new RuntimeException(e);
		}
		return columnNames;
	}
	
	

}
