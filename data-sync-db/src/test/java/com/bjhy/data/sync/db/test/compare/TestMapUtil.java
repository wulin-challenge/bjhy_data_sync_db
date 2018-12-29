package com.bjhy.data.sync.db.test.compare;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bjhy.data.sync.db.domain.RowCompareParam;
import com.bjhy.data.sync.db.domain.RowCompareParamSet;
import com.bjhy.data.sync.db.util.MapUtil;

public class TestMapUtil {

	@Test
	public void testRowCompareParam(){
		Map<String, Object> map1 = MapUtilDemo.getMap1();
		Map<String, Object> map2 = MapUtilDemo.getMap2();
		
		RowCompareParam rowCompareParam = new RowCompareParam("name",map1,map2);
		
		MapUtil mapUtil = new MapUtil();
		
		mapUtil.compare(rowCompareParam);
	}
	
	@Test
	public void testSpecifyCompareColumn(){
		MapUtil mapUtil = new MapUtil();
		String[] spesifyCompareColumm = new String[]{
				"ACCUSATIONID","ACCUSATIONIDS","ACCUSATIONNAME","ACCUSATIONNAMES","BH","BIRTHADDRESSID",
				"BIRTHADDRESSNAME","BIRTHPLACEAREAID","BIRTHPLACEAREANAME","BIRTHPLACECITYID","BIRTHPLACECITYNAME","BIRTHPLACECONTENT",
				"BIRTHPLACEPROVINCEID","BIRTHPLACEPROVINCENAME","BQMMID","BQMMNAME","BQWHCDID","BQWHCDNAME","BQZCID"};
		
		List<Map<String, Object>> fromData = MapUtilDemo.fromData();
		List<Map<String, Object>> todata = MapUtilDemo.toData();
		
		RowCompareParamSet rowCompareParamSet = new RowCompareParamSet();
		rowCompareParamSet.getLessRowSet().addAll(fromData);
		rowCompareParamSet.getMoreRowSet().addAll(todata);
		rowCompareParamSet.setUniqueValueKey("id");
		rowCompareParamSet.getSpecifyCompareColumn().addAll(Arrays.asList(spesifyCompareColumm));
		
		Set<String> compareSet = mapUtil.compareSet(rowCompareParamSet, String.class);
		
		System.out.println(compareSet);
		
	}
}
