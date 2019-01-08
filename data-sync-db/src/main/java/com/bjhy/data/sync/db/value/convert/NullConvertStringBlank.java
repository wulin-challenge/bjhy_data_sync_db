package com.bjhy.data.sync.db.value.convert;

/**
 * 将 Null类型的值转换为 StringBlank类型的值
 * @author wubo
 *
 */
public class NullConvertStringBlank extends AbstractValueTypeConvert{

	public NullConvertStringBlank() {
		super(Null.class, StringBlank.class);
	}

	@Override
	protected Object doConvertValue(Object originalValue) {
		return "";
	}

}
