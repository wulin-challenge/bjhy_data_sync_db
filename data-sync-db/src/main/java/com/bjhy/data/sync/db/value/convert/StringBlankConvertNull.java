package com.bjhy.data.sync.db.value.convert;

/**
 * 将 StringBlank 类型的值转换为 Null的值
 * @author wubo
 */
public class StringBlankConvertNull extends AbstractValueTypeConvert {

	public StringBlankConvertNull() {
		super(StringBlank.class, Null.class);
	}

	@Override
	protected Object doConvertValue(Object originalValue) {
		return null;
	}

}
