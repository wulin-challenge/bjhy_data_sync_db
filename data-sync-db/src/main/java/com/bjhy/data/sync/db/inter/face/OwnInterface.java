package com.bjhy.data.sync.db.inter.face;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bjhy.data.sync.db.domain.AddColumnAttribute;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.domain.SyncTemplate;

/**
 * 所有的接口都在这个接口中
 * @author wubo
 *
 */
public interface OwnInterface {

	/**
	 * 运行线程接口
	 * @author wubo
	 *
	 */
	public abstract class ForRunThread{
		
		private Map<String,Object> shareParams = new LinkedHashMap<String,Object>();
		
		/**
		 * 所有线程运行前执行 
		 * @param iterations 所有要运行的线程总量
		 */
		public void allThreadBeforeRun(int iterations){}
		
		/**
		 * 当前线程运行前执行
		 * @param iterations 所有要运行的线程总量
		 * @param i 当前是第几个线程
		 */
		public void currentThreadBeforeRun(int iterations,int i){}
		
		/**
		 * 当前线程运行中执行
		 * @param iterations 所有要运行的线程总量
		 * @param i 当前是第几个线程
		 */
		public abstract void currentThreadRunning(int iterations,int i);
		
		/**
		 * 当前线程运行中执行
		 * @param iterations 所有要运行的线程总量
		 * @param i 当前是第几个线程
		 */
		public void currentThreadErrorRun(int iterations,int i,Exception e){};
		
		/**
		 * 当前线程运行后执行
		 * @param iterations 所有要运行的线程总量
		 * @param i 当前是第几个线程
		 */
		public void currentThreadAfterRun(int iterations,int i){}
		
		/**
		 * 所有线程运行完成后执行 
		 * @param iterations 所有要运行的线程总量
		 */
		public void allThreadAfterRun(int iterations){}

		/**
		 * 得到线程共享参数
		 * @return
		 */
		public Map<String, Object> getShareParams() {
			return shareParams;
		}
		
	}
	
	/**
	 * 循环运行同步
	 * @author wubo
	 */
	public abstract class ForRunSync{
		/**
		 * 共享参数
		 */
		private Map<String,Object> shareParams = new LinkedHashMap<String,Object>();
		
		/**
		 * 所有同步开始前运行
		 */
		public void allRunBefore(){};
		
		/**
		 * 目标源开运行前
		 * @param nativeTemplate 本地存储template
		 * @param toTemplate 目标template
		 * @param toIndex 当前目标Index
		 */
		public void toTempleateRunBefore(SyncTemplate nativeTemplate,SyncTemplate toTemplate,int toIndex){};
		
		/**
		 * 当前数据源运行前执行
		 * @param singleRunEntity 当个数据源运行的Entity
		 */
		public void currentRunBefore(SingleRunEntity singleRunEntity){};
		
		/**
		 * 正式运行中
		 * @param singleRunEntity 当个数据源运行的Entity
		 */
		public abstract void singleRun(SingleRunEntity singleRunEntity);
		
		/**
		 * 当前数据源运行后执行
		 * @param singleRunEntity 当个数据源运行的Entity
		 */
		public void currentRunAfter(SingleRunEntity singleRunEntity){}
		
		/**
		 * 目标源运行结束后
		 * @param nativeTemplate 本地存储template
		 * @param toTemplate 目标template
		 * @param toIndex 当前目标Index
		 */
		public void toTempleateRunAfter(SyncTemplate nativeTemplate,SyncTemplate toTemplate,int toIndex){};
		
		/**
		 * 所有同步运行完后执行
		 */
		public void allRunAfter(){}

		public Map<String, Object> getShareParams() {
			return shareParams;
		}
		
	}
	
	/**
	 * 多线程分页接口,利用该接口屏蔽了数据库分页语句之间的差异
	 * @author wubo 
	 *
	 */
	public interface MultiThreadPage{
		
		/**
		 * 分页的数据
		 * @param index
		 * @return
		 */
		public List<Map<String,Object>> pageData(int index);
		
		/**
		 * 分页迭代次数
		 */
		public Integer pageIterations();
		
		/**
		 * 步骤(表)的最大线程数据
		 * @return
		 */
		public Integer stepMaxThreadNumber();
		
		
	}
	
	/**
	 * 单个步骤的监听器
	 * @author wubo
	 *
	 */
	public abstract class SingleStepListener{
		public abstract Map<String,Object> rowCall(final SyncLogicEntity syncLogicEntity,final Map<String,Object> rowParam);
	}
	

	/**
	 * 单个步骤执行执行前监听
	 * @author wubo
	 *
	 */
	public abstract class SingleStepBeforeListener{
		public abstract void stepBeforeCall(final SyncLogicEntity syncLogicEntity);
	}
	
	/**
	 * 单个步骤执行后监听
	 * @author wubo
	 *
	 */
	public abstract class SingleStepAfterListener{
		public abstract void stepAfterCall(final SyncLogicEntity syncLogicEntity);
	}
	
	/**
	 * 版本检查接口
	 * @author wubo
	 *
	 */
	public interface VersionCheck{
		
		/**
		 * 得到添加VersionCheck列的sql语句
		 * @return
		 */
		public List<String> getAddVersionCheckColumnSql(List<AddColumnAttribute> addToTableColumns);
		
		/**
		 * 得到通过versionCheck清楚数据的deletesql
		 * @return
		 */
		public String getToDeleteByVersionCheck();
		
	}
	
}
