package com.bjhy.data.sync.db.value.convert;

/**
 * 值类型转换的抽象实现
 * @author wubo
 *
 * @param <O> original 要转换值得原始类型
 * @param <R> return 返回值得类型
 */
public abstract class AbstractValueTypeConvert implements ValueTypeConvert{
	
	/**
	 * 原始值的类型class
	 */
	protected Class<?> originalClass;
	
	/**
	 * 返回值的类型class
	 */
	protected Class<?> returnClass;
	
	public AbstractValueTypeConvert(Class<?> originalClass, Class<?> returnClass) {
		super();
		this.originalClass = originalClass;
		this.returnClass = returnClass;
	}

	@Override
	public Object convertValue(Object originalValue) {
		return doConvertValue(originalValue);
	}

	/**
	 * 将 O 类型的值转换为 R类型的值
	 * @param originalValue 原始类型的值 
	 * @return 返回R类型的值
	 */
	protected abstract Object doConvertValue(Object originalValue);
	
}
