package com.bjhy.data.sync.db.value.convert;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;


/**
 * 将字符串类型的值转换为number类型
 * @author wubo
 */
public class StringConvertNumber extends AbstractValueTypeConvert{
	
	public StringConvertNumber(Class<?> returnClass) {
		super(String.class, returnClass);
	}

	@Override
	protected Object doConvertValue(Object originalValue) {
		if(int.class == returnClass || Integer.class == returnClass){
			return convertInteger((String)originalValue);
		}else if(long.class == returnClass || Long.class == returnClass){
			return convertLong((String)originalValue);
		}else if(BigDecimal.class == returnClass){
			return convertBigDecimal((String)originalValue);
		}
		return originalValue;
	}
	
	private Long convertLong(String originalValue){
		if(StringUtils.isBlank(originalValue)){
			return 0l;
		}
		return Long.parseLong(originalValue);
	}
	
	private Integer convertInteger(String originalValue){
		if(StringUtils.isBlank(originalValue)){
			return 0;
		}
		return Integer.parseInt(originalValue);
	}
	
	private BigDecimal convertBigDecimal(String originalValue){
		if(StringUtils.isBlank(originalValue)){
			return null;
		}
		return new BigDecimal(originalValue);
	}

}
