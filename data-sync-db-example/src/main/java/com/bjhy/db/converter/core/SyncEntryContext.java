package com.bjhy.db.converter.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.bjhy.data.sync.db.core.BaseRun;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;
import com.bjhy.db.converter.service.SyncService;

/**
 * 同步入口上下文
 * @author wubo
 *
 */
public class SyncEntryContext implements SyncEntry,BeanFactoryAware{
	
	private Logger logger = Logger.getLogger(SyncEntryContext.class);
	/**
	 * 任务链接符号
	 */
	private static final String taskLink = ":";
	
	/**
	 * 任务列表
	 */
	private Map<String,List<SyncService>> tasks = new LinkedHashMap<String,List<SyncService>>();
	
	private DefaultListableBeanFactory beanFactory;
	
	/**
	 * 是否只执行第一个子任务before和最后一个子任务after回调钩子
	 * <p> true:只执行第一个子任务before和最后一个子任务after回调钩子
	 * <p> false:执行所有子任务的before和after回调钩子
	 */
	private Boolean onlyBeforeAndAfter;
	
	@Override
	public void run() {
		synchronized (tasks) {
			// 构建任务
			buildTask();
		}
		
		// 运行所有任务
		runAllTask();
	}
	
	/**
	 * 运行所有任务
	 */
	private void runAllTask(){
		Set<Entry<String, List<SyncService>>> entrySet = tasks.entrySet();
		
		for (Entry<String, List<SyncService>> entry : entrySet) {
			List<SyncService> subTaskList = entry.getValue();
			//升序排序
			subTaskList.sort(new Comparator<SyncService>(){
				@Override
				public int compare(SyncService x, SyncService y) {
					return x.order()-y.order();
				}
			});
			runSubTask(subTaskList);
		}
	}
	
	/**
	 * 运行子任务
	 * @param subTaskList 子任务列表
	 */
	private void runSubTask(List<SyncService> subTaskList){
		//运行子任务
		runSubTask(0, subTaskList);
	}
	
	/**
	 * 运行子任务
	 * @param syncService
	 */
	private void runSubTask(final int currentTask,final List<SyncService> subTaskList){
		
		BaseRun baseRun = BaseRun.getInstance();
		final SyncService syncService = subTaskList.get(currentTask);
		baseRun.baseForRun(new BaseRunEntity(syncService.fromTask(),syncService.toTask()), new ForRunSync(){
			
			@Override
			public void allRunBefore() {
				//若是第一个子任务,则执行任务前置函数
				if(currentTask == 0 && onlyBeforeAndAfter){
					syncService.runTaskBefore();
				}
				//执行所有子任务的before和after回调钩子
				if(!onlyBeforeAndAfter){
					syncService.runTaskBefore();
				}
			}

			@Override
			public void singleRun(SingleRunEntity singleRunEntity) {
				try {
					syncService.runSync(singleRunEntity);
				} catch (Exception e) {
					logger.error("子任务出错!",e);
				}
			}
			
			@Override
			public void allRunAfter() {
				int index = currentTask+1;
				//判断是否是最后一个字任务
				if(index<subTaskList.size()){
					runSubTask(index,subTaskList);
				}
				
				//若是最后一个子任务,则执行任务后置函数
				if(index == subTaskList.size() && onlyBeforeAndAfter){
					syncService.runTaskAfter();
				}
				
				//执行所有子任务的before和after回调钩子
				if(!onlyBeforeAndAfter){
					syncService.runTaskAfter();
				}
			}
		});
	}
	
	/**
	 * 构建任务
	 */
	private void buildTask(){
		if(tasks.size()!=0){
			return;
		}
		Map<String, SyncService> taskServices = beanFactory.getBeansOfType(SyncService.class);
		if(taskServices != null && taskServices.size()>0){
			List<SyncService> syncServiceTasks = new ArrayList<SyncService>(taskServices.values());
			//升序排序
			syncServiceTasks.sort(new Comparator<SyncService>(){
				@Override
				public int compare(SyncService x, SyncService y) {
					return x.taskOrder()-y.taskOrder();
				}
			});
			
			for (SyncService syncService : syncServiceTasks) {
				String taskKey = getTaskKey(syncService);
				List<SyncService> taskGroup = tasks.get(taskKey);
				
				if(taskGroup == null){
					taskGroup = new ArrayList<SyncService>();
				}
				taskGroup.add(syncService);
				tasks.put(taskKey, taskGroup);
			}
		}
	}
	
	/**
	 * 得到任务key
	 * @param syncService 同步业务数据的Service
	 * @return 任务key
	 */
	private String getTaskKey(SyncService syncService){
		return syncService.fromTask()+taskLink+syncService.toTask();
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if(beanFactory instanceof DefaultListableBeanFactory){
			this.beanFactory = (DefaultListableBeanFactory)beanFactory;
		}
	}

	public void setOnlyBeforeAndAfter(Boolean onlyBeforeAndAfter) {
		this.onlyBeforeAndAfter = onlyBeforeAndAfter;
	}
}
