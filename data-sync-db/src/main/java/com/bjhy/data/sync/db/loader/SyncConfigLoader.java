package com.bjhy.data.sync.db.loader;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.data.sync.db.domain.SyncConfig;
import com.bjhy.data.sync.db.log.LogCache;
import com.bjhy.data.sync.db.util.LoggerUtils;
import com.bjhy.data.sync.db.util.SyncPropertiesUtil;

/**
 * 同步配置加载类
 * @author wubo
 *
 */
public class SyncConfigLoader {
	
	/**
	 * 配置加载
	 * @return 
	 */
	public SyncConfig configLoader(){
		SyncConfig syncConfig = new SyncConfig();
		
		//加载模式
		String model = SyncPropertiesUtil.getProperty("sync.data.source.loader.model");
		if(StringUtils.isNotEmpty(model)){
			String[] modelArray = model.split(",");
			for (String singleModel : modelArray) {
				syncConfig.getSyncDataSourceLoaderModel().add(singleModel);
			}
			LogCache.addStartLog("sync.data.source.loader.model 加载成功");
			LoggerUtils.info("sync.data.source.loader.model 加载成功");
		}else{
			model="prop";
			syncConfig.getSyncDataSourceLoaderModel().add(model);
			
			LogCache.addStartLog("sync.data.source.loader.model 属性为空,使用默认值  prop");
			LoggerUtils.error("sync.data.source.loader.model 属性为空,使用默认值  prop");
		}
//		#自动校验数据,自动修复
//		sync.is.auto.repair=true
		syncConfig.setSyncIsThisOnlyOne(loadSyncIsThisOnlyOne());
		syncConfig.setSyncIsAutoRepair(loadSyncIsAutoRepair());
		syncConfig.setSyncSyncNullValue(loadSyncNullValue());
		syncConfig.setSyncToMaxThreadNum(loadSyncToMaxThreadNum());
		syncConfig.setSyncFromMaxThreadNum(loadSyncFromMaxThreadNum());
		syncConfig.setSyncTablePageMaxThreadNum(loadSyncTablePageMaxThreadNum());
		syncConfig.setSyncInsertOrUpdateMaxThreadNum(loadSyncInsertOrUpdateMaxThreadNum());
		syncConfig.setSyncPageRowThreadMaxThreadNum(loadSyncPageRowThreadMaxThreadNum());
		syncConfig.setSyncPageRowMaxColumnNum(loadSyncPageRowMaxColumnNum());
		return syncConfig;
	}
	
	/**
	 * 加载  sync.is.this.only.one
	 * 若SingleStepSyncConfig中isThisOnlyOne设置为true的步骤,sync.is.this.only.one=true才生效同步
	 * @return
	 */
	private Boolean loadSyncIsThisOnlyOne(){
		String one = SyncPropertiesUtil.getProperty("sync.is.this.only.one");
		Boolean onlyOne = null;
		if(StringUtils.isNotEmpty(one)){
			if("true".equals(one)){
				onlyOne = true;
			}
		}
		return onlyOne;
	}
	
	/**
	 * 加载  sync.is.auto.repair
	 * @return
	 */
	private Boolean loadSyncIsAutoRepair(){
		String repair = SyncPropertiesUtil.getProperty("sync.is.auto.repair");
		Boolean autoRepair = null;
		if(StringUtils.isNotEmpty(repair)){
			if("true".equals(repair)){
				autoRepair = true;
			}
		}
		return autoRepair;
	}
	
	
	
	/**
	 * 是否同步null值,true表示同步空值,否则反之
	 * 若配置文件中 sync.is.sync.null.value=true 那表示所有步骤都要同步空值
	 * 若配置文件中 sync.is.sync.null.value=false 而步骤中 isSyncNullValue = true 那表示该步骤要同步空值
	 */
	private Boolean loadSyncNullValue(){
		String value = SyncPropertiesUtil.getProperty("sync.is.sync.null.value");
		Boolean nullValue = null;
		if(StringUtils.isNotEmpty(value)){
			if("true".equals(value)){
				nullValue = true;
			}
		}
		return nullValue;
	}
	
	
	
	/**
	 * 加载  sync.to.max.thread.num
	 * @return
	 */
	private Integer loadSyncToMaxThreadNum(){
		String num = SyncPropertiesUtil.getProperty("sync.to.max.thread.num");
		Integer threadNum = null;
		if(StringUtils.isNotEmpty(num)){
			threadNum = Integer.parseInt(num);
		}
		
		return threadNum;
	}
	
	/**
	 * 加载  sync.from.max.thread.num
	 * @return
	 */
	private Integer loadSyncFromMaxThreadNum(){
		String num = SyncPropertiesUtil.getProperty("sync.from.max.thread.num");
		Integer threadNum = null;
		if(StringUtils.isNotEmpty(num)){
			threadNum = Integer.parseInt(num);
		}
		
		return threadNum;
	}
	
	/**
	 * 加载  sync.table.page.max.thread.num
	 * @return
	 */
	private Integer loadSyncTablePageMaxThreadNum(){
		String num = SyncPropertiesUtil.getProperty("sync.table.page.max.thread.num");
		Integer threadNum = null;
		if(StringUtils.isNotEmpty(num)){
			threadNum = Integer.parseInt(num);
		}
		
		return threadNum;
	}
	
	/**
	 * 加载  sync.intert.or.update.max.thread.num
	 * @return
	 */
	private Integer loadSyncInsertOrUpdateMaxThreadNum(){
		String num = SyncPropertiesUtil.getProperty("sync.intert.or.update.max.thread.num");
		Integer threadNum = null;
		if(StringUtils.isNotEmpty(num)){
			threadNum = Integer.parseInt(num);
		}
		
		return threadNum;
	}
	
	/**
	 * 加载  sync.page.row.thread.max.thread.num
	 * @return
	 */
	private Integer loadSyncPageRowThreadMaxThreadNum(){
		String num = SyncPropertiesUtil.getProperty("sync.page.row.thread.max.thread.num");
		Integer threadNum = null;
		if(StringUtils.isNotEmpty(num)){
			threadNum = Integer.parseInt(num);
		}
		
		return threadNum;
	}
	
	/**
	 * 加载 sync.page.row.max.column.num
	 * @return
	 */
	private Integer loadSyncPageRowMaxColumnNum(){
		String num = SyncPropertiesUtil.getProperty("sync.page.row.max.column.num");
		Integer columnNum = null;
		if(StringUtils.isNotEmpty(num)){
			columnNum = Integer.parseInt(num);
		}
		
		return columnNum;
	}
	
}
