package com.bjhy.data.sync.db.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bjhy.data.sync.db.inter.face.OwnInterface.ValueCompare;
import com.bjhy.data.sync.db.value.convert.ValueTypeConvert;

/**
 * 行比较参数,内部使用
 * @author wubo
 *
 */
public class RowCompareParam{
	
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
	 * 较少的行数据
	 */
	private Map<String,Object> lessRow = new HashMap<String,Object>(32);
	
	/**
	 * 较多的行数据
	 */
	private Map<String,Object> moreRow = new HashMap<String,Object>(32);
	
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
	
	public RowCompareParam() {}
	
	/**
	 * @param excludeColumn 排除的列
	 * @param specifyCompareColumn 指定比较的列
	 * @param lessRow 较少的行数据
	 * @param moreRow 较多的行数据
	 */
	public RowCompareParam(Set<String> excludeColumn, Set<String> specifyCompareColumn, Map<String, Object> lessRow,Map<String, Object> moreRow,ValueCompare valueCompare) {
		this.excludeColumn.addAll(excludeColumn);
		this.specifyCompareColumn.addAll(specifyCompareColumn);
		this.lessRow.putAll(lessRow);
		this.moreRow.putAll(moreRow);
		this.valueCompare = valueCompare;
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

	public void setExcludeColumn(Set<String> excludeColumn) {
		this.excludeColumn = excludeColumn;
	}
	
	public Set<String> getSpecifyCompareColumn() {
		return specifyCompareColumn;
	}

	public Map<String, Object> getLessRow() {
		return lessRow;
	}

	public Map<String, Object> getMoreRow() {
		return moreRow;
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