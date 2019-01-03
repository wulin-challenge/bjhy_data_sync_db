package com.bjhy.data.sync.db.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bjhy.data.sync.db.inter.face.OwnInterface.ValueCompare;

/**
 * 增量同步数据
 * @author wubo
 *
 */
public class IncrementalSync {
	
	/**
	 * 行唯一值key
	 */
	private String uniqueValueKey;
	
	/**
	 * 判断两个值是否相等的回调接口
	 */
	private ValueCompare valueCompare;
	
	/**
	 * 增量同步方式
	 */
	private IncrementalSyncMode incrementalSyncMode = IncrementalSyncMode.INTERSECTION_COLUMN;
	
	/**
	 * 排除的列,这里的列都是 lessRow 中的列
	 */
	private Set<String> excludeColumn = new HashSet<String>(16);
	
	/**
	 * 指定比较的列,这里的列都是 lessRow 中的列,如果指定了比较列,那么将只对比较例进行处理
	 */
	private Set<String> specifyCompareColumn = new HashSet<String>(16);
	
	/**
	 * 强制更新字段
	 * <p> 强制更新字段的值在每一次同步过程中都会强制更新到数据库中
	 */
	private List<String> forceUpdateColumn = new ArrayList<String>(5);
	
	/**
	 * 经过字段打印方式
	 * <p> 当报警字段报警时的打印方式
	 */
	private AlarmColumnPrintLevel alarmColumnPrintLevel = AlarmColumnPrintLevel.LOGGING;
	
	/**
	 * 警告字段
	 * <p>警告字段用于当前同步的数据与上一次同步的数据进行对比,若果两次同步数据不同就会报警,报警的方式以输出日志打印异常两种
	 */
	private List<String> alarmColumn = new ArrayList<String>(5);

	public String getUniqueValueKey() {
		return uniqueValueKey != null?uniqueValueKey.toUpperCase():null;
	}

	public void setUniqueValueKey(String uniqueValueKey) {
		this.uniqueValueKey = uniqueValueKey;
	}

	public ValueCompare getValueCompare() {
		return valueCompare;
	}

	public void setValueCompare(ValueCompare valueCompare) {
		this.valueCompare = valueCompare;
	}

	public Set<String> getExcludeColumn() {
		return excludeColumn;
	}

	public Set<String> getSpecifyCompareColumn() {
		return specifyCompareColumn;
	}
	
	public AlarmColumnPrintLevel getAlarmColumnPrintLevel() {
		return alarmColumnPrintLevel;
	}

	public void setAlarmColumnPrintLevel(AlarmColumnPrintLevel alarmColumnPrintLevel) {
		this.alarmColumnPrintLevel = alarmColumnPrintLevel;
	}

	public List<String> getForceUpdateColumn() {
		return forceUpdateColumn;
	}

	public List<String> getAlarmColumn() {
		return alarmColumn;
	}
	
	public IncrementalSyncMode getIncrementalSyncMode() {
		return incrementalSyncMode;
	}

	public void setIncrementalSyncMode(IncrementalSyncMode incrementalSyncMode) {
		this.incrementalSyncMode = incrementalSyncMode;
	}

	/**
	 * 增量同步方式
	 * @author wubo
	 */
	public static enum IncrementalSyncMode{
		INTERSECTION_COLUMN("intersectionColumn","来源表与目标表字段的交集"),TO_CONTAIN_FROM("toContainFrom","要同步的来源表字段必须被目标表的字段包含");
		
		private String code;
		private String name;
		private IncrementalSyncMode(String code, String name) {
			this.code = code;
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}

	/**
	 * 报警字段打印级别
	 * @author wubo
	 */
	public static enum AlarmColumnPrintLevel{
		
		LOGGING("logging","日志级别"),EXCEPTION("exception","异常级别");
		
		private String code;
		private String name;
		private AlarmColumnPrintLevel(String code, String name) {
			this.code = code;
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
