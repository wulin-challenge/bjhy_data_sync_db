package com.bjhy.data.sync.db.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.collection.ConcurrentSet;
import com.bjhy.data.sync.db.domain.BatchTaskNodeKey;
import com.bjhy.data.sync.db.domain.BatchTaskNodeValue;
import com.bjhy.data.sync.db.domain.OrderTaskNodeValue;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.domain.SyncPageRowEntity;
import com.bjhy.data.sync.db.thread.ConsumerTask;
import com.bjhy.data.sync.db.thread.MultiThreadConsumerTask;
import com.bjhy.data.sync.db.thread.ThreadFactoryImpl;
import com.bjhy.data.sync.db.util.BaseCoreUtil;
import com.bjhy.data.sync.db.util.LoggerUtils;
import com.bjhy.data.sync.db.validation.SyncStepValidationRepair;

/**
 * 异步批量提交code
 * @author wulin
 *
 */
public class BaseAsynchronousBatchCommitCode {
	private static BaseAsynchronousBatchCommitCode instance;
	/**
	 * 批量提交数量
	 */
	private static final int BATCH_COMMIT_NUMBER = 500;

	/**
	 * 默认5万条任务节点,TODO 后期可修改为可配置
	 */
	private BlockingQueue<BatchTaskNodeValue> asyncTask = new LinkedBlockingQueue<BatchTaskNodeValue>(50);
	
	/**
	 * 未完成的任务
	 */
	private ConcurrentHashMap<BatchTaskNodeKey,BatchTaskNodeValue> unfinishedTask = new ConcurrentHashMap<BatchTaskNodeKey,BatchTaskNodeValue>();
	
	/**
	 * syncStepId 和 BatchTaskNodeKey 的映射
	 */
	private ConcurrentHashMap<Long/* syncStepId*/,BatchTaskNodeKey> idAndKeyMapping = new ConcurrentHashMap<Long,BatchTaskNodeKey>();
	
	private MultiThreadConsumerTask<BatchTaskNodeValue> consumer = new MultiThreadConsumerTask<BatchTaskNodeValue>(5, 1, TimeUnit.SECONDS, new ThreadFactoryImpl("BaseAsynchronousBatchCommitCode"));
	/**
	 * 正在消费的任务
	 */
	private ConcurrentSet<Long> bingConsumerTask= new ConcurrentSet<Long>();
	
	/**
	 * 消费者线程的ThreadLocal
	 */
	private ThreadLocal<Long> consumerThreadLocal = new ThreadLocal<Long>();
	
	/**
	 * 上次失败时间
	 */
	private AtomicLong preFailTime = new AtomicLong(0l);
	
	/**
	 * 失败时间间隔(默认5分钟)
	 */
	private long failPrintTimeInterval = 1000 * 60 * 5l;
	
	private ReentrantLock lock = new ReentrantLock();
	
	/**
	 * 批量执行insert或者update
	 * @param syncLogicEntity
	 * @param rowParam
	 */
	public void batchInsertOrUpdate(SyncLogicEntity syncLogicEntity,Map<String, Object> rowParam) {
		
		SyncPageRowEntity syncPageRowEntity = syncLogicEntity.getSingleStepSyncConfig().getSyncPageRowEntity();
		if(syncPageRowEntity != null){
			addPageRowInsertOrUpdateTask(syncLogicEntity, syncPageRowEntity, syncPageRowEntity.getPageRowInsertColumnSql(), rowParam);
		}else {
			addInsertOrUpdateTask(syncLogicEntity, syncLogicEntity.getInsertSql(), syncLogicEntity.getUpdateSql(), rowParam);
		}
	}
	
	/**
	 * 添加行拆分后数据的保存或者更新任务
	 * @param syncLogicEntity
	 * @param syncPageRowEntity
	 * @param insertSql
	 * @param rowParam
	 */
	private void addPageRowInsertOrUpdateTask(SyncLogicEntity syncLogicEntity,SyncPageRowEntity syncPageRowEntity,String insertSql,final Map<String, Object> rowParam){
		final List<String> pageRowUpdateColumnSqlList = syncPageRowEntity.getPageRowUpdateColumnSqlList();
		
		//数据检查操作
		String checkSql = syncLogicEntity.getCheckSql();
		Boolean exis = BaseCoreUtil.isExis(syncLogicEntity,checkSql, rowParam);
		
		//数据保存或者更新
		if(!exis){
			addTask(syncLogicEntity,insertSql, null, rowParam);
			
		}else{
			addTask(syncLogicEntity,null, pageRowUpdateColumnSqlList, rowParam);
		}
	}
	
	/**
	 * 添加数据的保存或者更新任务
	 * @param syncLogicEntity
	 * @param insertSql
	 * @param updateSql
	 * @param rowParam
	 */
	private void addInsertOrUpdateTask(SyncLogicEntity syncLogicEntity,String insertSql, String updateSql, Map<String, Object> rowParam) {
		
		//数据检查操作
		String checkSql = syncLogicEntity.getCheckSql();
		Boolean exis = BaseCoreUtil.isExis(syncLogicEntity,checkSql, rowParam);
		
		//数据保存或者更新
		if(exis){
			addTask(syncLogicEntity,null, Arrays.asList(updateSql), rowParam);
		}else{
			addTask(syncLogicEntity,insertSql, null, rowParam);
		}
	}
	
	/**
	 * 添加结束任务
	 * @param syncLogicEntity
	 */
	public void addEndTask(SyncLogicEntity syncLogicEntity) {
		BatchTaskNodeValue value = null;
		BatchTaskNodeKey key = idAndKeyMapping.get(syncLogicEntity.getSyncStepId());
		if(key == null) {
			synchronized(unfinishedTask) {
				//添加一个空任务来执行结束节点任务
				BatchTaskNodeKey emptyKey = new BatchTaskNodeKey(null, null);
				value = buildTaskValue(syncLogicEntity, null, null);
				value.setIsStepEndTaskNode(true);
				setOrderTaskCondition(value);
				
				moveEndTask(emptyKey, value);
				return;
			}
		}
		
		value = unfinishedTask.get(key);
		if(value != null) {
			synchronized(unfinishedTask) {
				value.setIsStepEndTaskNode(true);
				setOrderTaskCondition(value);
				moveEndTask(key, value);
				idAndKeyMapping.remove(syncLogicEntity.getSyncStepId());
			}
		}
		
	}

	private void setOrderTaskCondition(BatchTaskNodeValue value) {
		if(value instanceof OrderTaskNodeValue) {
			OrderTaskNodeValue orderValue = (OrderTaskNodeValue) value;
			orderValue.setCondition(lock.newCondition());
		}
	}
	
	/**
	 * 移动结束任务
	 * @param key
	 * @param value
	 */
	private void moveEndTask(BatchTaskNodeKey key, BatchTaskNodeValue value) {
		try {
			lock.lock();
			asyncTask.put(value);
			
			//若当前移动的任务为顺序任务,则在该任务之后添加一空顺序任务以保证其顺序执行
			if(value instanceof OrderTaskNodeValue) {
				asyncTask.put(createEmptyOrderTaskValue(value.getSyncLogicEntity()));
				
				OrderTaskNodeValue orderValue = (OrderTaskNodeValue) value;
				Condition condition = orderValue.getCondition();
				condition.await();
			}
		} catch (InterruptedException e) {
			LoggerUtils.error("添加任务失败!"+e.getMessage());
		} finally {
			if(lock.isLocked()) {
				lock.unlock();
			}
			unfinishedTask.remove(key);
		}
	}
		

	/**
	 * 移动任务
	 * @param key
	 * @param value
	 */
	private void moveTask(BatchTaskNodeKey key, BatchTaskNodeValue value) {
		try {
			asyncTask.put(value);
			//若当前移动的任务为顺序任务,则在该任务之后添加一空顺序任务以保证其顺序执行
			if(value instanceof OrderTaskNodeValue) {
				asyncTask.put(createEmptyOrderTaskValue(value.getSyncLogicEntity()));
			}
		} catch (InterruptedException e) {
			LoggerUtils.error("添加任务失败!"+e.getMessage());
		}
		unfinishedTask.remove(key);
	}
	
	/**
	 * 添加空顺序任务
	 */
	public void addEmptyOrderTask() {
		SyncLogicEntity logic = new SyncLogicEntity();
		SingleStepSyncConfig step = new SingleStepSyncConfig();
		step.setIsOrderSyncStep(true);
		
		logic.setSingleStepSyncConfig(step);
		logic.setSyncStepId(-1l);
		
		BatchTaskNodeKey emptyKey = new BatchTaskNodeKey(null, null);
		BatchTaskNodeValue emptyValue = buildTaskValue(logic, null, null);
		moveTask(emptyKey, emptyValue);
	}
	
	private void addTask(SyncLogicEntity syncLogicEntity,String insertSql,List<String> updateSqlList,final Map<String, Object> rowParam) {
		BatchTaskNodeKey key = new BatchTaskNodeKey(insertSql, updateSqlList);
		
		synchronized(unfinishedTask) {
			BatchTaskNodeValue value = unfinishedTask.get(key);
			
			//TODO 目前不确定 一个 Id 是否会对应多个key
			idAndKeyMapping.putIfAbsent(syncLogicEntity.getSyncStepId(), key);
			if(value == null) {
				value = buildTaskValue(syncLogicEntity, insertSql, updateSqlList);
				unfinishedTask.put(key, value);
			}
			
			if(value.getData().size()<BATCH_COMMIT_NUMBER) {
				value.getData().add(rowParam);
			}else {
				moveTask(key, value);
				addTask(syncLogicEntity,insertSql, updateSqlList, rowParam);
			}
	   }
	}
	
	
	/**
	 * 构建任务value值
	 * @param syncLogicEntity
	 * @param insertSql
	 * @param updateSqlList
	 * @return
	 */
	private BatchTaskNodeValue buildTaskValue(SyncLogicEntity syncLogicEntity,String insertSql,List<String> updateSqlList) {
		Boolean isOrderSyncStep = syncLogicEntity.getSingleStepSyncConfig().getIsOrderSyncStep();
		if(isOrderSyncStep) {
			return new OrderTaskNodeValue(syncLogicEntity,insertSql, updateSqlList);
		}else {
			return new BatchTaskNodeValue(syncLogicEntity,insertSql, updateSqlList);
		}
	}
	
	/**
	 * 创建空顺序任务value
	 * @param syncLogicEntity
	 * @return
	 */
	private BatchTaskNodeValue createEmptyOrderTaskValue(SyncLogicEntity syncLogicEntity) {
		return new OrderTaskNodeValue(syncLogicEntity, null, null);
	}
	
	public static BaseAsynchronousBatchCommitCode getInstance() {
		if(instance == null) {
			synchronized(BaseAsynchronousBatchCommitCode.class) {
				if(instance == null) {
					instance = new BaseAsynchronousBatchCommitCode();
					instance.startConsumer();
				}
			}
		}
		return instance;
	}
	
	private void startConsumer() {
		consumer.run(new ConsumerTask<BatchTaskNodeValue>() {
			@Override
			public BatchTaskNodeValue getTask() {
				return BaseAsynchronousBatchCommitCode.this.getTask();
			}

			@Override
			public void execute(BatchTaskNodeValue task) {
				BaseAsynchronousBatchCommitCode.this.executeSync(task);
			}
		});
	}
	
	/**
	 * 得到任务
	 */
	private BatchTaskNodeValue getTask() {
		synchronized(asyncTask) {
			//清除当前线程的同步任务标识
			Long currentSyncStepId = consumerThreadLocal.get();
			if(currentSyncStepId != null) {
				bingConsumerTask.remove(currentSyncStepId);
			}
			
			Iterator<BatchTaskNodeValue> iterator = asyncTask.iterator();
			while(iterator.hasNext()) {
				BatchTaskNodeValue value = iterator.next();
				Long syncStepId = value.getSyncLogicEntity().getSyncStepId();
				
				//处理顺序任务节点情况
				if(value instanceof OrderTaskNodeValue) {
					return getOrderTask(value, syncStepId);
				}
				
				//处理普通情况
				if(isMatch(syncStepId)) {
					setTaskStatus(value, syncStepId);
					return value;
				}
			}
		}
		return null;
	}
	
	/**
	 * 得到顺序任务
	 * @param value 批量任务节点值
	 * @param syncStepId 同步步骤Id
	 * @return
	 */
	private BatchTaskNodeValue getOrderTask(BatchTaskNodeValue value,Long syncStepId) {
		if(bingConsumerTask.isEmpty()) {
			setTaskStatus(value, syncStepId);
			return value;
		}
		return null;
	}
	
	/**
	 * 设置任务状态
	 * @param value 批量任务节点值
	 * @param syncStepId 同步步骤Id
	 */
	private void setTaskStatus(BatchTaskNodeValue value,Long syncStepId) {
		bingConsumerTask.add(syncStepId);
		consumerThreadLocal.set(syncStepId);
		asyncTask.remove(value);
	}
	
	/**
	 * 是否匹配,true:表示匹配,false:表示不匹配
	 * @param syncStepId
	 * @return
	 */
	private boolean isMatch(Long syncStepId) {
		
		return (!bingConsumerTask.contains(syncStepId) 
				|| syncStepId == consumerThreadLocal.get());
	}
	
	private void executeSync(BatchTaskNodeValue value) {
		//操作标记,insert/update
		String operationMark = null;
		SyncLogicEntity syncLogicEntity = value.getSyncLogicEntity();
		
		//批量提交的数据量
		int len = value.getData().size();
		try {
			String insertSql = value.getInsertSql();
			if(StringUtils.isNotBlank(insertSql)) {
				operationMark = "insert";
				
				NamedParameterJdbcTemplate namedToTemplate = value.getSyncLogicEntity().getNamedToTemplate();
				namedToTemplate.batchUpdate(insertSql, value.getArrayData());
				syncLogicEntity.getSyncStepLogInfoEntity().getInsertCount().addAndGet(len);
			}
			
			List<String> updateSqlList = value.getUpdateSqlList();
			for (String updateSql : updateSqlList) {
				operationMark = "update";
				
				NamedParameterJdbcTemplate namedToTemplate = value.getSyncLogicEntity().getNamedToTemplate();
				namedToTemplate.batchUpdate(updateSql, value.getArrayData());
				syncLogicEntity.getSyncStepLogInfoEntity().getUpdateCount().addAndGet(len);
			}
		} catch (Throwable e) {
			SyncStepValidationRepair.getInstance().getNeedRepairSteps().add(syncLogicEntity.getSingleStepSyncConfig());
			Boolean isThisOnlyOneSync = syncLogicEntity.getSingleStepSyncConfig().getIsThisOnlyOneSync();
			//只同步一次的步骤,重复就不打印出来
			if(!isThisOnlyOneSync){
				////得到insert或者update的错误信息
				getInsertOrUpdateFailInfo(syncLogicEntity, operationMark, e,len);
			}
		}finally {
			if(value.getIsStepEndTaskNode()) {
				//生产者步骤解锁
				producerStepUnlock(value);
				
				try {
					BaseCoreUtil.endStepSync(syncLogicEntity);
				} catch (Exception e) {
					LoggerUtils.error("BaseCoreUtil.endStepSync : "+e.getMessage());
				}
			}
		}
		
	}

	/**
	 * 生产者步骤解锁
	 * @param value
	 */
	private void producerStepUnlock(BatchTaskNodeValue value) {
		if(value instanceof OrderTaskNodeValue) {
			OrderTaskNodeValue orderValue = (OrderTaskNodeValue) value;
			Condition condition = orderValue.getCondition();
			if(condition != null) {
				try {
					lock.lock();
					condition.signal();
				} finally {
					if(lock.isLocked()) {
						lock.unlock();
					}
				}
			}
		}
	}
	
	/**
	 * 得到insert或者update的错误信息
	 * @param syncLogicEntity 同步逻辑实例
	 * @param operationMark insert/update标记
	 * @param e 具体异常
	 */
	private void getInsertOrUpdateFailInfo(SyncLogicEntity syncLogicEntity, String operationMark,Throwable e,int len) {
		//得到必要的数据源等信息
		String dataSourceName = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceName();
		String dataSourceNumber = syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getFromTemplate().getConnectConfig().getDataSourceNumber();
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		
		//拼装错误信息
		StringBuilder failInfo = new StringBuilder("表名:"+toTableName+",数据源名称:"+dataSourceName+",数据源编号:"+dataSourceNumber);
		failInfo.append(",当前这条数据已经存在或sql有错误,具体的错误信息:"+e.getMessage());
		
		syncLogicEntity.getSyncStepLogInfoEntity().getFailCount().addAndGet(len);
		syncLogicEntity.getSyncStepLogInfoEntity().getFailInfo().add(failInfo.toString());
		
		printErronInfo(failInfo);
	}
	
	/**
	 * 打印错误信息
	 * @param failInfo
	 */
	private void printErronInfo(StringBuilder failInfo) {
		long currentTime = System.currentTimeMillis();
		if((currentTime-preFailTime.get())>failPrintTimeInterval) {
			synchronized (preFailTime) {
				if((currentTime-preFailTime.get())>failPrintTimeInterval) {
					LoggerUtils.error("严重错误 : "+failInfo.toString());
					preFailTime.set(currentTime);
				}
			}
		}
	}
}
