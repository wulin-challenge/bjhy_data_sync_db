package com.bjhy.data.sync.db.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.util.Assert;

import com.bjhy.data.sync.db.domain.OneAndMultipleDataCompare;
import com.bjhy.data.sync.db.domain.RowCompareParam;
import com.bjhy.data.sync.db.domain.RowCompareParamSet;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ValueCompare;
import com.bjhy.data.sync.db.value.convert.Null;
import com.bjhy.data.sync.db.value.convert.StringBlank;
import com.bjhy.data.sync.db.value.convert.ValueTypeConvert;
import com.bjhy.data.sync.db.value.convert.ValueTypeConvertRegistry;

/**
 * map工具类
 * @author wubo
 */
public class MapUtil {
	
	/**
	 * 去重日志打印
	 */
	private Set<String> logCache = new HashSet<String>();
	
	/**
	 * 一行数据与多行hash数据中的一条比较
	 * @param oneAndMultipleDataCompare 一行数据与多行数据比较
	 * @return 返回的Map长度为0代表比较成功,若等于1代表比较失败
	 */
	public Map<String,Object> oneAndMultipleDataCompare(OneAndMultipleDataCompare oneAndMultipleDataCompare){
		Map<String,Object> row = new HashMap<String,Object>();
		
		Assert.notNull(oneAndMultipleDataCompare,"oneAndMultipleDataCompare 不能为空");
		Assert.hasLength(oneAndMultipleDataCompare.getUniqueValueKey(),"uniqueValueKey 不能为空");
		Assert.notNull(oneAndMultipleDataCompare.getLessRow(),"lessRow 行数据集合 不能为 空");
		
		String uniqueValueKey = oneAndMultipleDataCompare.getUniqueValueKey();
		Map<String, Object> lessRow = oneAndMultipleDataCompare.getLessRow();
		Map<Object, Map<String, Object>> moreRowHash = oneAndMultipleDataCompare.getMoreRowHash();
		
		if(lessRow.size()==0 || moreRowHash == null || moreRowHash.size()==0 || 
		   lessRow.get(uniqueValueKey) == null || moreRowHash.get(lessRow.get(uniqueValueKey)) == null){
			row.putAll(lessRow);
			return row;
		}
		
		RowCompareParam rowCompareParam = new RowCompareParam();
		rowCompareParam.getSpecifyCompareColumn().addAll(oneAndMultipleDataCompare.getSpecifyCompareColumn());
		rowCompareParam.getExcludeColumn().addAll(oneAndMultipleDataCompare.getExcludeColumn());
		rowCompareParam.setValueCompare(oneAndMultipleDataCompare.getValueCompare());
		rowCompareParam.getLessRow().putAll(lessRow);
		rowCompareParam.getMoreRow().putAll(moreRowHash.get(lessRow.get(uniqueValueKey)));
		rowCompareParam.getLessRowTypeConvert().putAll(oneAndMultipleDataCompare.getLessRowTypeConvert());
		rowCompareParam.getMoreRowTypeConvert().putAll(oneAndMultipleDataCompare.getMoreRowTypeConvert());
		
		//行数据比较
		boolean compare = compare(rowCompareParam);
		if(!compare){
			row.putAll(lessRow);
		}
		return row;
	}
	
	/**
	 * 两个集合数据进行对比,找出数据相同的 唯一key的值
	 * @param rowCompareParamSet 多个行参数比较set
	 * @param returnType 返回类型
	 * @return
	 */
	public <T> Set<T> compareSet(RowCompareParamSet rowCompareParamSet,Class<T> returnType){
		Set<T> result = new HashSet<T>();
		
		String uniqueValueKey = rowCompareParamSet.getUniqueValueKey();
		Collection<Map<String, Object>> lessRowSet = rowCompareParamSet.getLessRowSet();
		Collection<Map<String, Object>> moreRowSet = rowCompareParamSet.getMoreRowSet();
		
		Assert.hasLength(uniqueValueKey,"uniqueValueKey 不能为空");
		Assert.notNull(lessRowSet,"lessRowSet 较少行 数据集合 不能为 空");
		Assert.notNull(moreRowSet,"moreRowSet 较多行 数据集合 不能为 空");
		
		//将数据较多的一行进行hash
		Map<T,Map<String,Object>> hashMoreRowSet = hashMoreRowSet(uniqueValueKey, moreRowSet);
		
		for (Map<String, Object> lessRow : lessRowSet) {
			T uniqueValue = (T)lessRow.get(uniqueValueKey);
			Map<String, Object> moreRow = (Map<String, Object>)hashMoreRowSet.get(uniqueValue);
			
			if(moreRow == null || moreRow.size() ==0){
				result.add(uniqueValue);
				continue;
			}
			
			RowCompareParam rowCompareParam = new RowCompareParam();
			rowCompareParam.getSpecifyCompareColumn().addAll(rowCompareParamSet.getSpecifyCompareColumn());
			rowCompareParam.getExcludeColumn().addAll(rowCompareParamSet.getExcludeColumn());
			rowCompareParam.setValueCompare(rowCompareParamSet.getValueCompare());
			rowCompareParam.getLessRow().putAll(lessRow);
			rowCompareParam.getMoreRow().putAll(moreRow);
			rowCompareParam.getLessRowTypeConvert().putAll(rowCompareParamSet.getLessRowTypeConvert());
			rowCompareParam.getMoreRowTypeConvert().putAll(rowCompareParamSet.getMoreRowTypeConvert());
			
			//行数据比较
			boolean compare = compare(rowCompareParam);
			if(!compare){
				result.add(uniqueValue);
			}
		}
		return result;
	}
	
	/**
	 * 行数据比较
	 * @param rowCompareParam 行比较参数,内部使用
	 * @return
	 */
	public boolean compare(RowCompareParam rowCompareParam){
		// 验证参数
		if(!validationParams(rowCompareParam)){
			return false;
		}
		
		boolean result = true;
		Set<Entry<String, Object>> lessRowEntrySet = rowCompareParam.getLessRow().entrySet();
		Map<String, Object> moreRow = rowCompareParam.getMoreRow();
		
		//处理指定的比较列
		Set<String> specifyCompareColumn = rowCompareParam.getSpecifyCompareColumn();
		if(specifyCompareColumn.size()>0){
			//删除排除字段
			specifyCompareColumn.removeAll(rowCompareParam.getExcludeColumn());
			if(specifyCompareColumn.size() == 0){
				return false;
			}
			result = doCompareColumns(rowCompareParam, moreRow, specifyCompareColumn);
			return result;
		}
		
		//循环较少行,并进行比较
		result = forLessRowDoCompare(rowCompareParam, lessRowEntrySet, moreRow);
		return result;
	}

	/**
	 * 循环较少行,并进行比较
	 * @param rowCompareParam 行比较参数
	 * @param lessRowEntrySet 较少的行参数
	 * @param moreRow 较多参数的行
	 * @return 返回比较成功与否,成功返回true,否则返回false
	 */
	private boolean forLessRowDoCompare(RowCompareParam rowCompareParam,Set<Entry<String, Object>> lessRowEntrySet, Map<String, Object> moreRow) {
		boolean result = true;
		for (Entry<String, Object> lessRowEntry : lessRowEntrySet) {
			String compareColumn = lessRowEntry.getKey();
			
			//若排除列不为空且 包含 lessRowKey 列,则跳过比较
			if(rowCompareParam.getExcludeColumn().size()>0 && rowCompareParam.getExcludeColumn().contains(compareColumn)){
				continue;
			}
			
			//这代表验证失败
			if(!moreRow.containsKey(compareColumn)){
				result = false;
				break;
			}
			
			Object lessRowValue = lessRowEntry.getValue();
			Object moreRowValue = moreRow.get(compareColumn);
			if(!doCompare(compareColumn,rowCompareParam, lessRowValue, moreRowValue)){
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * 处理指定的比较列
	 * @param rowCompareParam 行比较参数
	 * @param moreRow 较多参数的行
	 * @param compareColumns 要比较的行参数列 ,这里的列都是 lessRow 中的列
	 * @return 返回比较成功与否,成功返回true,否则返回false
	 */
	private boolean doCompareColumns(RowCompareParam rowCompareParam, Map<String, Object> moreRow,Set<String> compareColumns) {
		for (String compareColumn : compareColumns) {
			Object lessRowValue = rowCompareParam.getLessRow().get(compareColumn);
			Object moreRowValue = moreRow.get(compareColumn);
			
			if(!doCompare(compareColumn,rowCompareParam, lessRowValue, moreRowValue)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param compareColumn 行比较参数
	 * @param rowCompareParam 行比较参数
	 * @param lessRowValue 较少行的值
	 * @param moreRowValue 较多行的值
	 * @return
	 */
	private boolean doCompare(String compareColumn,RowCompareParam rowCompareParam,Object lessRowValue,Object moreRowValue){
		//若比较的值都为空,则表示比较成功
		if(lessRowValue == null && moreRowValue == null){
			return true;
		}
		
		//指定列做值的类型转换
		lessRowValue = doValueTypeConvert(compareColumn, rowCompareParam.getLessRowTypeConvert(), lessRowValue);
		moreRowValue = doValueTypeConvert(compareColumn, rowCompareParam.getMoreRowTypeConvert(), moreRowValue);

		//在满足一下条件下进行值的自动类型转换,自动转换只将 lessRowValue 
		ValueTypeConvert lessRowTypeConvert = rowCompareParam.getLessRowTypeConvert().get(compareColumn);
		ValueTypeConvert moreRowTypeConvert = rowCompareParam.getMoreRowTypeConvert().get(compareColumn);
		if(lessRowTypeConvert == null && moreRowTypeConvert == null && rowCompareParam.getAutoConvertValueType()){
			lessRowValue = doLessRowValueAutoValueTypeConvert(lessRowValue, moreRowValue);
		}
		
		//值类型转换后,第二次判断,若比较的值都为空,则表示比较成功
		if(lessRowValue == null && moreRowValue == null){
			return true;
		}

		if(lessRowValue == null && moreRowValue != null ){
			return false;
		}
		if(moreRowValue == null && lessRowValue != null ){
			return false;
		}
		
		Class<? extends Object> lessRowClass = lessRowValue.getClass();
		Class<? extends Object> moreRowClass = moreRowValue.getClass();
		
		//若比较的值得类型不同这表示比较失败
		if(lessRowClass != moreRowClass){
			return false;
		}
		return doCompare(lessRowClass,lessRowValue, moreRowValue,rowCompareParam);
	}
	
	/**
	 * 将  lessRowValue 值的类型转换为 moreRowValue 值的类型
	 * @param lessRowValue 较少行的值
	 * @param moreRowValue 较多行的值
	 * @return 
	 */
	private Object doLessRowValueAutoValueTypeConvert(Object lessRowValue,Object moreRowValue){
		Class<? extends Object> lessRowClass = getAutoConvertValueClass(lessRowValue);
		Class<? extends Object> moreRowClass = getAutoConvertValueClass(moreRowValue);
		
		if(lessRowClass == moreRowClass){
			return lessRowValue;
		}
		
		ValueTypeConvertRegistry valueTypeConvertRegistry = RegisterConvertUtil.getInstance().getValueTypeConvertRegistry();
		ValueTypeConvert valueTypeConvert = valueTypeConvertRegistry.getValueTypeConvert(lessRowClass, moreRowClass);
		
		if(valueTypeConvert != null){
			return valueTypeConvert.convertValue(lessRowValue);
		}else{
			String logInfo = "没有找到 "+lessRowClass+"->"+moreRowClass+" 值类型转换器!";
			if(!logCache.contains(logInfo)){
				logCache.add(logInfo);
				LoggerUtils.warn(logInfo);
			}
		}
		return lessRowValue;
	}
	
	/**
	 * 自动转换值的class
	 * @param value
	 * @return
	 */
	private Class<?> getAutoConvertValueClass(Object value){
		if(value == null){
			return Null.class;
		}
		if(value.getClass() == String.class && "".equals(((String)value).trim())){
			return StringBlank.class;
		}
		return value.getClass();
	}
	
	/**
	 * 做值的类型转换
	 * @param compareColumn 比较列
	 * @param convertMap 转换集合
	 * @param value 要转换的值
	 * @return 转换后的值
	 */
	private Object doValueTypeConvert(String compareColumn,Map<String,ValueTypeConvert> convertMap,Object value){
		ValueTypeConvert valueTypeConvert = convertMap.get(compareColumn);
		if(valueTypeConvert != null){
			return valueTypeConvert.convertValue(value);
		}
		return value;
	}
	
	/**
	 * 比较相同数据库类型的两个值是否相等
	 * @param dataClass 数据类型
	 * @param lessRowValue 较少行比较列的值
	 * @param moreRowValue 较多行比较列的值
	 * @return 
	 */
	private boolean doCompare(Class<? extends Object> dataClass,Object lessRowValue,Object moreRowValue,RowCompareParam rowCompareParam){
		if(dataClass == String.class){
			if(!lessRowValue.equals(moreRowValue)){
				return false;
			}
		}else if(dataClass == Date.class){
			if(((Date)lessRowValue).getTime() != ((Date)moreRowValue).getTime()){
				return false;
			}
		}else if(dataClass == java.sql.Timestamp.class){
			if(((java.sql.Timestamp)lessRowValue).getTime() != ((java.sql.Timestamp)moreRowValue).getTime()){
				return false;
			}
		}else if(dataClass == int.class || dataClass == Integer.class){
			if((int)lessRowValue != (int)moreRowValue){
				return false;
			}
		}else if(dataClass == java.math.BigDecimal.class){
			BigDecimal data1 = (BigDecimal) lessRowValue;
			BigDecimal data2 = (BigDecimal) moreRowValue;
			if(data1.compareTo(data2) != 0){
				return false;
			}
		}else if(dataClass == long.class || dataClass == Long.class){
			BigDecimal data1 = new BigDecimal((long)lessRowValue);
			BigDecimal data2 = new BigDecimal((long)moreRowValue);
			if(data1.compareTo(data2) != 0){
				return false;
			}
		}else if(dataClass == double.class || dataClass == Double.class){
			BigDecimal data1 = new BigDecimal((double)lessRowValue);
			BigDecimal data2 = new BigDecimal((double)moreRowValue);
			if(data1.compareTo(data2) != 0){
				return false;
			}
		}else if(dataClass == float.class || dataClass == Float.class){
			BigDecimal data1 = new BigDecimal((float)lessRowValue);
			BigDecimal data2 = new BigDecimal((float)moreRowValue);
			if(data1.compareTo(data2) != 0){
				return false;
			}
		}else if(dataClass == boolean.class || dataClass == Boolean.class){
			if((boolean)lessRowValue != (boolean)moreRowValue){
				return false;
			}
		}else{
			// 此处增加回调接口,让用户自行实现其他类型值的判断...
			ValueCompare valueCompare = rowCompareParam.getValueCompare();
			if(valueCompare != null){
				return valueCompare.doEqualsCompare(dataClass, lessRowValue, moreRowValue);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 将数据较多的一行进行hash
	 * @param uniqueValueKey 唯一值key
	 * @param moreRowSet 较多行数据集合
	 * @return 返回hash后的较多行数据
	 */
	public <T> Map<T,Map<String,Object>> hashMoreRowSet(String uniqueValueKey,Collection<Map<String, Object>> moreRowSet){
		Map<T,Map<String,Object>> hashMoreRowSet = new HashMap<T,Map<String,Object>>(32);
		for (Map<String, Object> moreRow : moreRowSet) {
			hashMoreRowSet.put((T)moreRow.get(uniqueValueKey), moreRow);
		}
		return hashMoreRowSet;
	}
	
	/**
	 * 验证参数
	 * @param rowCompareParam 行比较参数
	 * @return
	 */
	private boolean validationParams(RowCompareParam rowCompareParam){
		if(rowCompareParam.getLessRow().size()==0 || rowCompareParam.getMoreRow().size()==0){
			return false;
		}
		return true;
	}
}
