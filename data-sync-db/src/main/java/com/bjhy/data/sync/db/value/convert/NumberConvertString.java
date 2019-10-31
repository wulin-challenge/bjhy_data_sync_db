package com.bjhy.data.sync.db.value.convert;
import java.math.BigDecimal;

import com.bjhy.data.sync.db.value.convert.AbstractValueTypeConvert;

/**
 * 将number类型的值转换为字符串类型
 * @author wubo
 */
public class NumberConvertString extends AbstractValueTypeConvert{
	
	public NumberConvertString(Class<?> originalClass) {
		super(originalClass, String.class);
	}

	@Override
	protected Object doConvertValue(Object originalValue) {
		if(int.class == originalClass || Integer.class == originalClass){
			return convertInteger((Integer)originalValue);
		}else if(long.class == originalClass || Long.class == originalClass){
			return convertLong((Long)originalValue);
		}else if(BigDecimal.class == originalClass){
			return convertBigDecimal((BigDecimal)originalValue);
		}
		return originalValue;
	}
	
	private String convertLong(Long originalValue){
		if(originalValue == null) {
			return "";
		}
		return Long.toString(originalValue);
	}
	
	private String convertInteger(Integer originalValue){
		if(originalValue == null) {
			return "";
		}
		return Integer.toString(originalValue);
	}
	
	private String convertBigDecimal(BigDecimal originalValue){
		if(originalValue == null){
			return "";
		}
		
		return originalValue.toString();
	}

}