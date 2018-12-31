package com.bjhy.data.sync.db.domain;

import java.util.HashSet;
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
	 * 排除的列,这里的列都是 lessRow 中的列
	 */
	private Set<String> excludeColumn = new HashSet<String>(16);
	
	/**
	 * 指定比较的列,这里的列都是 lessRow 中的列,如果指定了比较列,那么将只对比较例进行处理
	 */
	private Set<String> specifyCompareColumn = new HashSet<String>(16);

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
}
