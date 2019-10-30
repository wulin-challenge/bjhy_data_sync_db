package com.bjhy.data.sync.db.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bjhy.data.sync.db.inter.face.OwnInterface.SingleStepListener;
import com.bjhy.data.sync.db.version.check.VersionCheckCore;

/**
 * 单个步骤同步配置参数
 * @author wubo
 */
public class SingleStepSyncConfig {
	
	/**
	 * 系统检测同步
	 */
	public static final String START_STEP_SYSTEM_CHECK_SYNC = "systemCheckSync";
	
	/**
	 * 用户同步
	 */
	public static final String START_STEP_USER_SYNC = "userSync";
	
	/**
	 * 当个数据源运行的Entity
	 */
	private SingleRunEntity singleRunEntity;
	
	/**
	 * 启动同步步骤方式
	 */
	private String startStepSyncType = START_STEP_USER_SYNC;
	
	/**
	 * 步骤唯一标示
	 */
	private String stepUniquelyIdentifies;

	/**
	 * 来源sql的from到最后
	 */
	private String fromFromPart;
	
	/**
	 * 来源Sql的select到from之间的部分
	 */
	private String fromSelectPart;
	
	/**
	 * 目标表表名
	 */
	private String toTableName;
	
	/**
	 * 添加(来源)静态列-值,意思不是要在来源(fromSql)的添加,而是把这种配置的静态列也当成来源数据
	 */
	private Map<String,Object> addStaticFromColumns = new LinkedHashMap<String,Object>();
	
	/**
	 * 是否强制添加静态 (列-值)
	 * true:表示当来源(fromSql)中包含与静态列相同的列名称时,则进行强制覆盖
	 * false:表示当来源(fromSql)中包含与静态列相同的列名称时,不进行覆盖
	 */
	private Boolean isForceAddStaticFromColumns = true;
	
	
	/**
	 * 剔除(来源)不需要的字段
	 */
	private List<String> removeFromColumns = new ArrayList<String>();
	
	/**
	 * 添加目标表的列
	 */
	private List<AddColumnAttribute> addToTableColumns = new ArrayList<AddColumnAttribute>();
	
	/**
	 * 是否自动添加同步列或者自定义列
	 */
	private Boolean isAutoAddToTableColumns = true;
	
	/**
	 * 是否多线程分页
	 */
	private Boolean isMultiThreadPage = true;
	
	/**
	 * 高性能的分页列
	 */
	private String highPerformancePageColumn;
	
	/**
	 * 是否只同步一次
	 * 该参数为false则表示无限次同步,true表示仅同步一次,该参数的使用必须配合配置文件中的 sync.is.this.only.one的属性
	 */
	private Boolean isThisOnlyOneSync = false;
	
	/**
	 * 是否同步null值,true表示同步空值,否则反之
	 */
	@Deprecated
	private Boolean isSyncNullValue = true;
	
	/**
	 * 是否添加版本检测过滤数据
	 */
	private Boolean isAddVersionCheckFilter = true;
	
	/**
	 * 通过{@link #isAddVersionCheckFilter}进行数据过滤时,是否对 ${@link #VersionCheckCore.SYNC_VERSION_CHECK}
	 * 字段值为空的数据执行删除操作
	 */
	private Boolean isDeleteVersionValueEmpty = true;
	
	/**
	 * 更新字段,通过该字段去更新已经存在的数据
	 */
	private String updateColumn;
	
	/**
	 * 该字段配置的update的Where后面的语句,表示要更新的条件
	 */
	private String updateWhere;
	
	/**
	 * 校验目标表是用到的where语句 : 该语句主要是用于系统校验是用的
	 */
	private String toValidationWhere;
	
	/**
	 * 同步分行实体: 将同步字段量非常多的实体进行拆分,先插入主字段后采用多线程的方式进行进行更新后面的字段值
	 * 例如:user(column1,column2,...,column300) 假设 column1,column2是主字段,
	 * 而数据库中又不存在改行的值,就先进行查询,然后采用多线程的方式更新其他值
	 */
	private SyncPageRowEntity syncPageRowEntity;
	
	/**
	 * 单个步骤的同步监听器
	 */
	private SingleStepListener singleStepListener;
	
	/**
	 * 单个步骤执行执行前监听(抽象类SingleStepBeforeListener)
	 */
	private String singleStepBeforeListener;
	
	/**
	 * 采用反射生成监听器的 监听器名称(抽象类SingleStepListener)
	 */
	private String SingleStepListenerName;
	
	/**
	 * 单个步骤执行后监听(抽象类SingleStepAfterListener)
	 */
	private String singleStepAfterListener;
	
	/**
	 * 简单(字段名称)的映射(复杂字段的映衬可以使用监听实现) 该映射会在行回调监听之前执行
	 * 例如: select o.column1,o.column2 from tablename o;
	 * 将 newColumn1 替换 column1
	 */
	private Map<String,String> simpleColumnNameMapping = new HashMap<String,String>();
	
	/**
	 * 增量同步数据
	 */
	private IncrementalSync incrementalSync;
	
	/**
	 * 是否为顺序同步步骤
	 */
	private Boolean isOrderSyncStep = false;
	
	/**
	 * 得到fromSql语句
	 * @return
	 */
	public String getFromSql(){
		StringBuffer fromSql = new StringBuffer();
		if(StringUtils.isEmpty(fromSelectPart)){
			fromSql.append("SELECT *");
		}else{
			fromSql.append(fromSelectPart);
		}
		fromSql.append(" "+fromFromPart);
		return fromSql.toString();
	}
	
	/**
	 * 得到fromCount语句
	 * @return
	 */
	public String getFromCountSql(){
		return "SELECT COUNT(1) NUM_ "+fromFromPart;
	}
	
	/**
	 * 设置单个步骤的监听
	 * @param singleStepListener
	 */
	public void setSingleStepListener(SingleStepListener singleStepListener) {
		this.singleStepListener = singleStepListener;
	}
	
	public SingleStepListener getSingleStepListener() {
		return singleStepListener;
	}

	public SingleRunEntity getSingleRunEntity() {
		return singleRunEntity;
	}

	public void setSingleRunEntity(SingleRunEntity singleRunEntity) {
		this.singleRunEntity = singleRunEntity;
	}
	
	public String getFromFromPart() {
		return fromFromPart;
	}

	public void setFromFromPart(String fromFromPart) {
		this.fromFromPart = fromFromPart;
	}

	public String getFromSelectPart() {
		return fromSelectPart;
	}

	public void setFromSelectPart(String fromSelectPart) {
		this.fromSelectPart = fromSelectPart;
	}

	public String getToTableName() {
		return toTableName;
	}

	public void setToTableName(String toTableName) {
		this.toTableName = toTableName;
	}

	public Map<String, Object> getAddStaticFromColumns() {
		return addStaticFromColumns;
	}

	public void setAddStaticFromColumns(Map<String, Object> addStaticFromColumns) {
		this.addStaticFromColumns = addStaticFromColumns;
	}

	public Boolean getIsForceAddStaticFromColumns() {
		return isForceAddStaticFromColumns;
	}

	public void setIsForceAddStaticFromColumns(Boolean isForceAddStaticFromColumns) {
		this.isForceAddStaticFromColumns = isForceAddStaticFromColumns;
	}

	public Boolean getIsMultiThreadPage() {
		return isMultiThreadPage;
	}

	public void setIsMultiThreadPage(Boolean isMultiThreadPage) {
		this.isMultiThreadPage = isMultiThreadPage;
	}

	public Boolean getIsThisOnlyOneSync() {
		return isThisOnlyOneSync;
	}

	public void setIsThisOnlyOneSync(Boolean isThisOnlyOneSync) {
		this.isThisOnlyOneSync = isThisOnlyOneSync;
	}

	public String getUpdateColumn() {
		return updateColumn;
	}

	public void setUpdateColumn(String updateColumn) {
		this.updateColumn = updateColumn;
	}

	public String getUpdateWhere() {
		return updateWhere;
	}

	public void setUpdateWhere(String updateWhere) {
		this.updateWhere = updateWhere;
	}

	/**
	 * 不在对null值这个情况进行处理
	 * @return
	 */
	@Deprecated
	public Boolean getIsSyncNullValue() {
//		return isSyncNullValue;
		return true;
	}

	@Deprecated
	public void setIsSyncNullValue(Boolean isSyncNullValue) {
//		this.isSyncNullValue = isSyncNullValue;
	}

	public List<String> getRemoveFromColumns() {
		return removeFromColumns;
	}

	public void setRemoveFromColumns(List<String> removeFromColumns) {
		this.removeFromColumns = removeFromColumns;
	}

	public String getHighPerformancePageColumn() {
		return highPerformancePageColumn;
	}

	public void setHighPerformancePageColumn(String highPerformancePageColumn) {
		this.highPerformancePageColumn = highPerformancePageColumn;
	}

	public Boolean getIsAddVersionCheckFilter() {
		return isAddVersionCheckFilter;
	}

	public void setIsAddVersionCheckFilter(Boolean isAddVersionCheckFilter) {
		this.isAddVersionCheckFilter = isAddVersionCheckFilter;
	}
	
	public Boolean getIsDeleteVersionValueEmpty() {
		return isDeleteVersionValueEmpty;
	}

	public void setIsDeleteVersionValueEmpty(Boolean isDeleteVersionValueEmpty) {
		this.isDeleteVersionValueEmpty = isDeleteVersionValueEmpty;
	}

	public String getStepUniquelyIdentifies() {
		return stepUniquelyIdentifies;
	}

	public void setStepUniquelyIdentifies(String stepUniquelyIdentifies) {
		this.stepUniquelyIdentifies = stepUniquelyIdentifies;
	}

	public String getStartStepSyncType() {
		return startStepSyncType;
	}

	public void setStartStepSyncType(String startStepSyncType) {
		this.startStepSyncType = startStepSyncType;
	}

	public String getSingleStepListenerName() {
		return SingleStepListenerName;
	}

	public void setSingleStepListenerName(String singleStepListenerName) {
		SingleStepListenerName = singleStepListenerName;
	}

	public String getToValidationWhere() {
		return toValidationWhere;
	}

	public void setToValidationWhere(String toValidationWhere) {
		this.toValidationWhere = toValidationWhere;
	}

	public SyncPageRowEntity getSyncPageRowEntity() {
		return syncPageRowEntity;
	}

	public void setSyncPageRowEntity(SyncPageRowEntity syncPageRowEntity) {
		this.syncPageRowEntity = syncPageRowEntity;
	}

	public String getSingleStepBeforeListener() {
		return singleStepBeforeListener;
	}

	public void setSingleStepBeforeListener(String singleStepBeforeListener) {
		this.singleStepBeforeListener = singleStepBeforeListener;
	}

	public String getSingleStepAfterListener() {
		return singleStepAfterListener;
	}

	public void setSingleStepAfterListener(String singleStepAfterListener) {
		this.singleStepAfterListener = singleStepAfterListener;
	}

	public Map<String, String> getSimpleColumnNameMapping() {
		return simpleColumnNameMapping;
	}

	public List<AddColumnAttribute> getAddToTableColumns() {
		return addToTableColumns;
	}
	
	public Boolean getIsAutoAddToTableColumns() {
		return isAutoAddToTableColumns;
	}

	public void setIsAutoAddToTableColumns(Boolean isAutoAddToTableColumns) {
		this.isAutoAddToTableColumns = isAutoAddToTableColumns;
	}

	public IncrementalSync getIncrementalSync() {
		return incrementalSync;
	}

	public void setIncrementalSync(IncrementalSync incrementalSync) {
		this.incrementalSync = incrementalSync;
	}

	public Boolean getIsOrderSyncStep() {
		return isOrderSyncStep;
	}

	public void setIsOrderSyncStep(Boolean isOrderSyncStep) {
		this.isOrderSyncStep = isOrderSyncStep;
	}

	@Override
	public int hashCode() {
		String fromTask = singleRunEntity.getFromTemplate().getConnectConfig().getTask();
		String toTask = singleRunEntity.getToTemplate().getConnectConfig().getTask();
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((startStepSyncType == null)?0:startStepSyncType.hashCode());
		result = prime * result + ((stepUniquelyIdentifies == null)?0:stepUniquelyIdentifies.hashCode());
		result = prime * result + ((fromFromPart == null)?0:fromFromPart.hashCode());
		result = prime * result + ((fromSelectPart == null)?0:fromSelectPart.hashCode());
		result = prime * result + ((toTableName == null)?0:toTableName.hashCode());
		result = prime * result + ((highPerformancePageColumn == null)?0:highPerformancePageColumn.hashCode());
		result = prime * result + ((updateColumn == null)?0:updateColumn.hashCode());
		result = prime * result + ((updateWhere == null)?0:updateWhere.hashCode());
		result = prime * result + ((toValidationWhere == null)?0:toValidationWhere.hashCode());
		result = prime * result + ((SingleStepListenerName == null)?0:SingleStepListenerName.hashCode());
		result = prime * result + ((fromTask == null)?0:fromTask.hashCode());
		result = prime * result + ((toTask == null)?0:toTask.hashCode());
		result = prime * result + ((isOrderSyncStep == null)?0:isOrderSyncStep.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		
		SingleStepSyncConfig other = (SingleStepSyncConfig)obj;
		
		String fromTask = singleRunEntity.getFromTemplate().getConnectConfig().getTask();
		String toTask = singleRunEntity.getToTemplate().getConnectConfig().getTask();
		
		String otherFromTask = other.singleRunEntity.getFromTemplate().getConnectConfig().getTask();
		String otherToTask = other.singleRunEntity.getToTemplate().getConnectConfig().getTask();
		
		if (startStepSyncType == null) {
			if (other.startStepSyncType != null)
				return false;
		} else if (!startStepSyncType.equals(other.startStepSyncType))
			return false;
		
		if (stepUniquelyIdentifies == null) {
            if (other.stepUniquelyIdentifies != null)
                return false;
        } else if (!stepUniquelyIdentifies.equals(other.stepUniquelyIdentifies))
            return false;

		if (fromFromPart == null) {
            if (other.fromFromPart != null)
                return false;
        } else if (!fromFromPart.equals(other.fromFromPart))
            return false;
		
		if (fromSelectPart == null) {
            if (other.fromSelectPart != null)
                return false;
        } else if (!fromSelectPart.equals(other.fromSelectPart))
            return false;
		
		if (toTableName == null) {
            if (other.toTableName != null)
                return false;
        } else if (!toTableName.equals(other.toTableName))
            return false;
		
		if (highPerformancePageColumn == null) {
            if (other.highPerformancePageColumn != null)
                return false;
        } else if (!highPerformancePageColumn.equals(other.highPerformancePageColumn))
            return false;
		
		if (updateColumn == null) {
            if (other.updateColumn != null)
                return false;
        } else if (!updateColumn.equals(other.updateColumn))
            return false;
		
		if (updateWhere == null) {
            if (other.updateWhere != null)
                return false;
        } else if (!updateWhere.equals(other.updateWhere))
            return false;
		
		if (toValidationWhere == null) {
            if (other.toValidationWhere != null)
                return false;
        } else if (!toValidationWhere.equals(other.toValidationWhere))
            return false;
		
		if (SingleStepListenerName == null) {
			if (other.SingleStepListenerName != null)
				return false;
		} else if (!SingleStepListenerName.equals(other.SingleStepListenerName))
			return false;
		
		if (isOrderSyncStep == null) {
			if (other.isOrderSyncStep != null)
				return false;
		} else if (!isOrderSyncStep.equals(other.isOrderSyncStep))
			return false;
		
		if (fromTask == null) {
			if (otherFromTask != null)
				return false;
		} else if (!fromTask.equals(otherFromTask))
			return false;
		
		if (toTask == null) {
			if (otherToTask != null)
				return false;
		} else if (!toTask.equals(otherToTask))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		String fromTask = singleRunEntity.getFromTemplate().getConnectConfig().getTask();
		String toTask = singleRunEntity.getToTemplate().getConnectConfig().getTask();
		String fromConnectUrl = singleRunEntity.getFromTemplate().getConnectConfig().getConnectUrl();
		String toConnectUrl = singleRunEntity.getToTemplate().getConnectConfig().getConnectUrl();
		
		StringBuilder toString = new StringBuilder("{fromTask:"+fromTask);
		toString.append(",toTask:"+toTask);
		toString.append(",startStepSyncType:"+startStepSyncType);
		toString.append(",stepUniquelyIdentifies:"+stepUniquelyIdentifies);
		toString.append(",fromFromPart:"+fromFromPart);
		toString.append(",fromSelectPart:"+fromSelectPart);
		toString.append(",toTableName:"+toTableName);
		toString.append(",highPerformancePageColumn:"+highPerformancePageColumn);
		toString.append(",updateColumn:"+updateColumn);
		toString.append(",updateWhere:"+updateWhere);
		toString.append(",toValidationWhere:"+toValidationWhere);
		toString.append(",fromConnectUrl:"+fromConnectUrl);
		toString.append(",toConnectUrl:"+toConnectUrl);
		toString.append(",isOrderSyncStep:"+isOrderSyncStep);
		toString.append(",SingleStepListenerName:"+SingleStepListenerName+"}");
		return toString.toString();
	}
}
