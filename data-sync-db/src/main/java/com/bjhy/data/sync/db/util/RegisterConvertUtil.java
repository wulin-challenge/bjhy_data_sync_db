package com.bjhy.data.sync.db.util;

import com.bjhy.data.sync.db.value.convert.ValueTypeConvertRegistry;

/**
 * 注册转换器工具类,单例
 * @author wubo
 *
 */
public class RegisterConvertUtil {
	private static RegisterConvertUtil registerConvertUtil;
	
	private ValueTypeConvertRegistry registry = new ValueTypeConvertRegistry();
	
	private RegisterConvertUtil() {
		super();
	}

	public static RegisterConvertUtil getInstance(){
		if(registerConvertUtil == null){
			registerConvertUtil = new RegisterConvertUtil();
		}
		return registerConvertUtil;
	}
	
	public ValueTypeConvertRegistry getValueTypeConvertRegistry(){
		return registry;
	}

}
