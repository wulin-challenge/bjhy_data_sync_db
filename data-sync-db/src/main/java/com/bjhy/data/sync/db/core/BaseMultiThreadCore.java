package com.bjhy.data.sync.db.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.domain.SyncPageRowEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunThread;
import com.bjhy.data.sync.db.inter.face.OwnInterface.MultiThreadPage;
import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;
import com.bjhy.data.sync.db.multi.thread.page.DmMultiThreadPage;
import com.bjhy.data.sync.db.multi.thread.page.MySqlMultiThreadPage;
import com.bjhy.data.sync.db.multi.thread.page.NoMultiThreadPage;
import com.bjhy.data.sync.db.multi.thread.page.OracleMultiThreadPage;
import com.bjhy.data.sync.db.multi.thread.page.SqlServerMultiThreadPage;
import com.bjhy.data.sync.db.thread.ThreadControl;
import com.bjhy.data.sync.db.thread.ThreadControl2;
import com.bjhy.data.sync.db.util.LoggerUtils;
import com.bjhy.data.sync.db.validation.SyncStepValidationStore;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

/**
 * 线程安全的逻辑处理类,该类绝对不能出现任何操作成员变量的代码,任何变量操作都必须是方法内部的局部变量传递
 * @author wubo
 *
 */
public class BaseMultiThreadCore {
	
	/**
	 * 行拆分的线程数(必须是静态的,为了避开每次都实例化)
	 */
	private static final ThreadControl2 pageRowThreadControler = new ThreadControl2(15);//固定线程数为15
	
	/**
	 * 利用 ThreadControl 的事件机制进行控制同步事件的触发,同时控制是否开启多线程
	 * @param syncLogicEntity
	 */
	public void  threadSyncEvent(final SyncLogicEntity syncLogicEntity){
		//得到多线程分页的 MultiThreadPage 对象
		final MultiThreadPage multiThreadPage = getMultiThreadPage(syncLogicEntity);
		
		ThreadControl threadControl = new ThreadControl(multiThreadPage.stepMaxThreadNumber());
		threadControl.forRunStart(multiThreadPage.pageIterations(), new ForRunThread(){

			@Override
			public void allThreadBeforeRun(int iterations) {
				String dataSourceName = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
				String dataSourceNumber = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
				String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
				LoggerUtils.info("[开始同步..] 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber);
			}

			@Override
			public void currentThreadRunning(int iterations, int i) {
				List<Map<String, Object>> pageFromData = multiThreadPage.pageData(i);
				pageSyncLogic(syncLogicEntity,pageFromData);//分页同步逻辑
			}

			@Override
			public void allThreadAfterRun(int iterations) {
				stepSyncEndEvent(syncLogicEntity);//步骤同步结束事件
				
				//是否自动修复
				Boolean isAutoRepair = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getBaseRunEntity().getIsAutoRepair();
				if(isAutoRepair){
					//编辑修复同步标记
					SyncStepValidationStore.getInstance().editIsRepirSyncFlag(syncLogicEntity.getSingleStepSyncConfig());
				}
				
				String dataSourceName = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
				String dataSourceNumber = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
				String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
				LoggerUtils.info("[同步结束] 表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber);
			}
			
		});
	}
	
	/**
	 * 步骤同步结束事件
	 * @param syncLogicEntity
	 */
	private void stepSyncEndEvent(SyncLogicEntity syncLogicEntity){
		Boolean isAddVersionCheckFilter = syncLogicEntity.getSingleStepSyncConfig().getIsAddVersionCheckFilter();
		if(isAddVersionCheckFilter){
			VersionCheckCore versionCheckCore = syncLogicEntity.getVersionCheckCore();
			versionCheckCore.clearBeforeVertionData();//清楚上个版本或者检测类为null的数据
		}
	}
	
	/**
	 * 得到多线程分页的 MultiThreadPage 对象
	 * @param syncLogicEntity
	 * @return
	 */
	private MultiThreadPage getMultiThreadPage(SyncLogicEntity syncLogicEntity){
		MultiThreadPage multiThreadPage = null;
		
		Boolean isMultiThreadPage = syncLogicEntity.getSingleStepSyncConfig().getIsMultiThreadPage();
		String databaseType = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDatabaseType();
		if(isMultiThreadPage){
			if("Oracle".equalsIgnoreCase(databaseType)){
				multiThreadPage = new OracleMultiThreadPage(syncLogicEntity);
				
			}else if("SQlServer".equalsIgnoreCase(databaseType)){
				multiThreadPage = new SqlServerMultiThreadPage(syncLogicEntity);
				
			}else if("MySql".equalsIgnoreCase(databaseType)){
				multiThreadPage = new MySqlMultiThreadPage(syncLogicEntity);
				
			}else if("DM".equalsIgnoreCase(databaseType)){
				multiThreadPage = new DmMultiThreadPage(syncLogicEntity);
				
			}
			
		}else{
			multiThreadPage = new NoMultiThreadPage(syncLogicEntity);
		}
		
		return multiThreadPage;
	}
	
	/**
	 * 分页同步逻辑(也采用多线程的方式以提高插入或者更新的效率)
	 */
	
	public void pageSyncLogic(final SyncLogicEntity syncLogicEntity,final List<Map<String, Object>> pageFromData){
		Integer insertOrUpdateMaxThreadNum = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getBaseRunEntity().getInsertOrUpdateMaxThreadNum();
		
		ThreadControl threadControler = new ThreadControl(insertOrUpdateMaxThreadNum);
		threadControler.forRunStart(pageFromData.size(), new ForRunThread(){

			@Override
			public void currentThreadRunning(int iterations, int index) {
				//refreshBuildDllSql
				SyncLogicEntity newSyncLogicEntity = copyNewSyncLogicEntity(syncLogicEntity);
				
				Map<String, Object> rowParam = rowParamToUpperCase(syncLogicEntity, pageFromData.get(index));
				//判断是否有改变列数量的操作
				threadSafetyLogic(newSyncLogicEntity, rowParam);
			}
			
		});
		
	}
	
	/**
	 * 拷贝新的  SyncLogicEntity,保证多线程操作时是线程安全的
	 * @param syncLogicEntity
	 * @return
	 */
	private SyncLogicEntity copyNewSyncLogicEntity(SyncLogicEntity syncLogicEntity){
			
		SyncLogicEntity newSyncLogicEntity = new SyncLogicEntity();
	
		newSyncLogicEntity.setCheckSql(syncLogicEntity.getCheckSql());
		newSyncLogicEntity.setCheckVersion(syncLogicEntity.getCheckVersion());
		newSyncLogicEntity.setDeleteSql(syncLogicEntity.getDeleteSql());
		newSyncLogicEntity.setInsertSql(syncLogicEntity.getInsertSql());
		newSyncLogicEntity.setSingleStepSyncConfig(syncLogicEntity.getSingleStepSyncConfig());
		newSyncLogicEntity.setSyncColumns(new LinkedHashSet<String>(syncLogicEntity.getSyncColumns()));
		newSyncLogicEntity.setUpdateSql(syncLogicEntity.getUpdateSql());
		newSyncLogicEntity.setToColumns(syncLogicEntity.getToColumns());
		
		return newSyncLogicEntity;
	}
	
	/**
	 * 线程安全逻辑
	 * @param syncLogicEntity
	 * @param rowParam
	 */
	private void threadSafetyLogic(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		///分页中每一行的逻辑处理的方法
		pageRowLogicDealWith(syncLogicEntity, rowParam);
		SyncPageRowEntity syncPageRowEntity = syncLogicEntity.getSingleStepSyncConfig().getSyncPageRowEntity();
		
		if(syncPageRowEntity != null){
			pageRowInsertOrUpdate(syncLogicEntity, syncPageRowEntity, syncPageRowEntity.getPageRowInsertColumnSql(), rowParam);
		}else{
			insertOrUpdate(syncLogicEntity, syncLogicEntity.getInsertSql(), syncLogicEntity.getUpdateSql(), rowParam);
		}
	}
	
	/**
	 * 行拆分后数据的保存或者更新
	 * @param syncLogicEntity
	 * @param syncPageRowEntity
	 * @param insertSql
	 * @param rowParam
	 */
	private void pageRowInsertOrUpdate(SyncLogicEntity syncLogicEntity,SyncPageRowEntity syncPageRowEntity,String insertSql,final Map<String, Object> rowParam){
		final NamedParameterJdbcTemplate namedToTemplate = syncLogicEntity.getNamedToTemplate();
		
		//数据检查操作
		String checkSql = syncLogicEntity.getCheckSql();
		Boolean exis = isExis(syncLogicEntity,checkSql, rowParam);
		//数据保存或者更新
		try {
			if(!exis){
				namedToTemplate.update(insertSql, rowParam);
			}
			final List<String> pageRowUpdateColumnSqlList = syncPageRowEntity.getPageRowUpdateColumnSqlList();
			//这个方式谨慎使用,因为最后线程池是没有被关闭的
			pageRowThreadControler.forRunStart2(pageRowUpdateColumnSqlList.size(), new ForRunThread(){
				@Override
				public void currentThreadRunning(int iterations, int i) {
					namedToTemplate.update(pageRowUpdateColumnSqlList.get(i), rowParam);
				}
			});
		} catch (DataAccessException e) {
			Boolean isThisOnlyOneSync = syncLogicEntity.getSingleStepSyncConfig().getIsThisOnlyOneSync();
			//只同步一次的步骤,重复就不打印出来
			if(!isThisOnlyOneSync){
				LoggerUtils.error("当前这条数据已经存在或sql有错误,具体的错误信息:"+e.getMessage());
			}
			
		}
		
	}

	/**
	 * 数据的保存或者更新
	 * @param syncLogicEntity
	 * @param insertSql
	 * @param updateSql
	 * @param rowParam
	 */
	private void insertOrUpdate(SyncLogicEntity syncLogicEntity,String insertSql, String updateSql, Map<String, Object> rowParam) {
		NamedParameterJdbcTemplate namedToTemplate = syncLogicEntity.getNamedToTemplate();
		
		//数据检查操作
		String checkSql = syncLogicEntity.getCheckSql();
		Boolean exis = isExis(syncLogicEntity,checkSql, rowParam);
		
		//数据保存或者更新
		try {
			if(exis){
				namedToTemplate.update(updateSql, rowParam);
				
			}else{
				namedToTemplate.update(insertSql, rowParam);
			}
		} catch (DataAccessException e) {
			Boolean isThisOnlyOneSync = syncLogicEntity.getSingleStepSyncConfig().getIsThisOnlyOneSync();
			//只同步一次的步骤,重复就不打印出来
			if(!isThisOnlyOneSync){
				LoggerUtils.error("当前这条数据已经存在或sql有错误,具体的错误信息:"+e.getMessage());
			}
			
		}
	}
	
	/**
	 * 分页中每一行的逻辑处理的方法
	 * @param syncLogicEntity
	 * @param rowParam 行参数
	 */
	private void pageRowLogicDealWith(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		SingleStepListener singleStepListener = syncLogicEntity.getSingleStepSyncConfig().getSingleStepListener();
		
		//行回调
		if(singleStepListener != null){
			pageDealWithRowCall(syncLogicEntity, rowParam);
		}
		
		//是否同步空值
		Boolean isSyncNullValue = syncLogicEntity.getSingleStepSyncConfig().getIsSyncNullValue();
		if(!isSyncNullValue){
			filterNullValue(syncLogicEntity, rowParam);
		}
		
		//刷新  构建inserSql和updateSql和checkSql和DeleteSql语句(即dll)
		refreshBuildDllSql(syncLogicEntity, rowParam);
	}
	
	
	
	private void filterNullValue(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		String databaseType = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getToTemplate().getConnectConfig().getDatabaseType();
		
		Map<String,Object> notNullRowParams = new LinkedHashMap<String,Object>();
		Set<Entry<String, Object>> entrySet = rowParam.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			//sqlServer判断为空的方式
			if(entry.getValue() != null){
				if("sqlServer".equalsIgnoreCase(databaseType)){
					if(entry.getValue() instanceof String){
						String strValue = (String) entry.getValue();
						if(StringUtils.isNotEmpty(strValue)){
							notNullRowParams.put(entry.getKey(), entry.getValue());
						}
					}else{
						notNullRowParams.put(entry.getKey(), entry.getValue());
					}
				}else{
					notNullRowParams.put(entry.getKey(), entry.getValue());
				}
			}
		}
		
		rowParam.clear();
		rowParam.putAll(notNullRowParams);
	}
	
	/**
	 * 刷新  构建inserSql和updateSql和checkSql和DeleteSql语句(即dll)
	 * @param syncLogicEntity
	 * @param rowParam
	 */
	private void refreshBuildDllSql(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		List<String> rowColumns = getRowColumns(syncLogicEntity, rowParam);
		BaseCore baseCore = new BaseCore();
		baseCore.refreshSyncColumns(syncLogicEntity, rowColumns,rowParam);
		baseCore.addStaticColumns(syncLogicEntity,rowParam);
		baseCore.removeFromColumns(syncLogicEntity);
		baseCore.buildDllSql(syncLogicEntity);
	}
	
	/**
	 * 分页处理行回调
	 * @param syncLogicEntity
	 * @param params
	 */
	private void pageDealWithRowCall(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		SingleStepListener singleStepListener = syncLogicEntity.getSingleStepSyncConfig().getSingleStepListener();
		Map<String, Object> rowCallParams = singleStepListener.rowCall(syncLogicEntity,rowParam);
		rowCallParams = rowParamToUpperCase(syncLogicEntity, rowCallParams);
		rowParam.putAll(rowCallParams);
	}
	
	/**
	 * 将行参数转大写
	 * @param syncLogicEntity
	 * @param rowParam
	 * @return
	 */
	private Map<String,Object> rowParamToUpperCase(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam){
		Map<String,Object> newRowParams = new LinkedHashMap<String,Object>();
		Set<Entry<String, Object>> entrySet = rowParam.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			newRowParams.put(entry.getKey().toUpperCase(), entry.getValue());
		}
		return newRowParams;
	}
	
	/**
	 * 检查数据是否存在,存在就返回true,否则返回false
	 * @param syncLogicEntity
	 * @param checkSql
	 * @param params
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private Boolean isExis(SyncLogicEntity syncLogicEntity,String checkSql,Map<String,Object> params){
		NamedParameterJdbcTemplate namedToTemplate = syncLogicEntity.getNamedToTemplate();
		int queryForInt = namedToTemplate.queryForInt(checkSql, params);
		if(queryForInt>0){
			return true;
		}
		return false;
	}
	
	/**
	 * 通过行参数得到行列
	 * @param syncLogicEntity
	 * @param rowParam
	 * @return
	 */
	private List<String> getRowColumns(SyncLogicEntity syncLogicEntity,Map<String,Object> rowParam){
		List<String> rowColumns = new ArrayList<String>();
		Set<Entry<String, Object>> entrySet = rowParam.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			rowColumns.add(entry.getKey());
		}
		return rowColumns;
	}

}
