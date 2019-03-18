package com.bjhy.db.converter.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 考核分简单字段映射
 * @author wubo
 */
public class YzColumnMappingUtil {
	
	/**
	 * baseInfo的映射
	 * @return
	 */
	public static Map<String,String> baseInfoMapping(){
		Map<String,String> baseInfoMapping = new HashMap<String,String>();
		baseInfoMapping.put("ISDELETE", "IS_DELETED");
		baseInfoMapping.put("CREATEDATE", "GMT_CREATE");
		baseInfoMapping.put("SEXENUM", "SEX_CODE");
		baseInfoMapping.put("JYBH", "SYS_ORGCODE");
		baseInfoMapping.put("JYMC", "SYS_ORGNAME");
		baseInfoMapping.put("GYDWID", "SYS_PERMCODE");
		baseInfoMapping.put("GYDWNAME", "SYS_PERMNAME");
		
		return baseInfoMapping;
	}

}
