package com.bjhy.data.sync.db.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bjhy.data.sync.db.inter.face.OwnInterface.ValueCompare;
import com.bjhy.data.sync.db.value.convert.ValueTypeConvert;

/**
 * 多个行参数比较set
 * @author wubo
 *
 */
public class RowCompareParamSet {
	
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
	
	/**
	 * 较少的行数据 的集合
	 */
	private Collection<Map<String,Object>> lessRowSet = new HashSet<Map<String,Object>>(50);
	
	/**
	 * 较多行数据 的集合
	 */
	private Collection<Map<String,Object>> moreRowSet = new HashSet<Map<String,Object>>(50);
	
	/**
	 * 较少的行数据类型转换
	 */
	private Map<String,ValueTypeConvert> lessRowTypeConvert = new HashMap<String,ValueTypeConvert>(5);
	
	/**
	 * 较多的行数据类型转换
	 */
	private Map<String,ValueTypeConvert> moreRowTypeConvert = new HashMap<String,ValueTypeConvert>(5);
	
	/**
	 * 自动转换值类型
	 */
	private Boolean autoConvertValueType = true;
	
	public String getUniqueValueKey() {
		return uniqueValueKey;
	}

	public void setUniqueValueKey(String uniqueValueKey) {
		this.uniqueValueKey = uniqueValueKey;
	}

	public Collection<Map<String, Object>> getLessRowSet() {
		return lessRowSet;
	}

	public void setLessRowSet(Collection<Map<String, Object>> lessRowSet) {
		this.lessRowSet = lessRowSet;
	}

	public ValueCompare getValueCompare() {
		return valueCompare;
	}

	public Collection<Map<String, Object>> getMoreRowSet() {
		return moreRowSet;
	}

	public Set<String> getExcludeColumn() {
		return excludeColumn;
	}

	public Set<String> getSpecifyCompareColumn() {
		return specifyCompareColumn;
	}

	public Map<String, ValueTypeConvert> getLessRowTypeConvert() {
		return lessRowTypeConvert;
	}

	public Map<String, ValueTypeConvert> getMoreRowTypeConvert() {
		return moreRowTypeConvert;
	}

	public Boolean getAutoConvertValueType() {
		return autoConvertValueType;
	}

	public void setAutoConvertValueType(Boolean autoConvertValueType) {
		this.autoConvertValueType = autoConvertValueType;
	}
}
