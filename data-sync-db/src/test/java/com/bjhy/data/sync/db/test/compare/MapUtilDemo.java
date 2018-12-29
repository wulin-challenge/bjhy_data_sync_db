package com.bjhy.data.sync.db.test.compare;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bjhy.data.sync.db.domain.ConnectConfig;
import com.bjhy.data.sync.db.domain.RowCompareParamSet;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.loader.DataSourceLoader;
import com.bjhy.data.sync.db.util.MapUtil;

public class MapUtilDemo {
	
	public static void main(String[] args) {
		List<Map<String, Object>> fromData = fromData();
		List<Map<String, Object>> todata = toData();
		
		MapUtil mapUtil = new MapUtil();
		
		long start = System.nanoTime();
		long start2 = System.currentTimeMillis();
		
		RowCompareParamSet rowCompareParamSet = new RowCompareParamSet();
		rowCompareParamSet.getLessRowSet().addAll(fromData);
		rowCompareParamSet.getMoreRowSet().addAll(todata);
		rowCompareParamSet.setUniqueValueKey("id");
		
		String[] exclude = new String[]{"DANGKAID","DKJYBH","DKJYMC","DKURL","YWGKPASSWORD","TGBZ",
				"TGLBIDS","TGLBNAMES","JLCYSCORE","GLJD","KSDCDW","SFBN","GYDWMODIFYDATE"};
		
		rowCompareParamSet.getExcludeColumn().addAll(Arrays.asList(exclude));
		Set<String> compareList = mapUtil.compareSet(rowCompareParamSet, String.class);
//		Set<String> compareList = compareList(fromData, todata, "id");
		
		long end = System.nanoTime();
		long end2 = System.currentTimeMillis();
		
		System.out.println(compareList+"->毫秒:"+(end2-start2)+"->纳秒:"+(end-start));
	}
	
	public void firstCompare(){
		long start = System.nanoTime();
		long start2 = System.currentTimeMillis();
		boolean compare = compare(getMap1(), getMap2());
		long end = System.nanoTime();
		long end2 = System.currentTimeMillis();
		
		System.out.println(compare+"->毫秒:"+(end2-start2)+"->纳秒:"+(end-start));
	}
	
	public static List<Map<String,Object>> toData(){
		return toTemplate().queryForList("select * from criminal_base_info where id in('4bb9086f0cbf45d4b17a29efc14d343b','1c26107fb7b74b58ace453f618b22f02','4ba883426dd74c1ea4d40973ef1e20a7')");
	}
	
	public static List<Map<String,Object>> fromData(){
		 List<Map<String, Object>> queryForList = fromTemplate().queryForList("select * from criminal_base_info where id in('4bb9086f0cbf45d4b17a29efc14d343b','1c26107fb7b74b58ace453f618b22f02','4ba883426dd74c1ea4d40973ef1e20a7')");
//		 for (Map<String, Object> map : queryForList) {
//			 map.remove("DANGKAID");
//			 map.remove("DKJYBH");
//			 map.remove("DKJYMC");
//			 map.remove("DKURL");
//			 map.remove("YWGKPASSWORD");
//			 map.remove("TGBZ");
//			 map.remove("TGLBIDS");
//			 map.remove("TGLBNAMES");
//			 map.remove("JLCYSCORE");
//			 map.remove("GLJD");
//			 map.remove("KSDCDW");
//			 map.remove("SFBN");
//			 map.remove("GYDWMODIFYDATE");
//		 }
		 return queryForList;
	}
	
	public static SyncTemplate fromTemplate(){
		DataSourceLoader ds = new DataSourceLoader();
		ConnectConfig fromCc = new ConnectConfig();
		fromCc.setConnectUrl("jdbc:oracle:thin:@192.168.0.49:1521:orcl");
		fromCc.setConnectUsername("dyyz");
		fromCc.setConnectPassword("123456789");
		fromCc.setConnectDriver("oracle.jdbc.driver.OracleDriver");
		
		
		
		return ds.getSyncTemplate(fromCc);
	}
	
	public static SyncTemplate toTemplate(){
		DataSourceLoader ds = new DataSourceLoader();
		ConnectConfig fromCc = new ConnectConfig();
		fromCc.setConnectUrl("jdbc:oracle:thin:@192.168.0.82:1521:orcl");
		fromCc.setConnectUsername("sjhj");
		fromCc.setConnectPassword("123456789");
		fromCc.setConnectDriver("oracle.jdbc.driver.OracleDriver");
		
		return ds.getSyncTemplate(fromCc);
	}
	
	/**
	 * map2的必须包含map1的key
	 * @param list1->map1
	 * @param list2->map2
	 * @return
	 */
	public static Set<String> compareList(List<Map<String,Object>> list1,List<Map<String,Object>> list2,String uqKey){
		Set<String> set = new HashSet<String>();
		Map<String,Map<String,Object>> hashList2 = new HashMap<String,Map<String,Object>>();
		for (Map<String, Object> map : list2) {
			hashList2.put((String)map.get(uqKey), map);
		}
		for (Map<String, Object> map1 : list1) {
			String uqKeyValue = (String)map1.get(uqKey);
			Map<String, Object> map2 = (Map<String, Object>)hashList2.get(uqKeyValue);
			boolean compare = compare(map1, map2);
			if(!compare){
				set.add(uqKeyValue);
			}
		}
		return set;
	}
	
	/**
	 * map2的必须包含map1的key
	 * @param map1
	 * @param map2
	 * @return
	 */
	public static boolean compare( Map<String,Object> map1, Map<String,Object> map2){
		boolean result = true;
		Set<Entry<String, Object>> entrySet = map1.entrySet();
		int i = 0;
		for (Entry<String, Object> entry : entrySet) {
			i++;
			String key1 = entry.getKey();
			if(map2.containsKey(key1)){
				Object value1 = entry.getValue();
				Object value2 = map2.get(key1);
				
				if(value1 == null && value2 == null){
					continue;
				}

				if(value1 == null && value2 != null ){
					return false;
				}
				if(value2 == null && value1 != null ){
					return false;
				}
				
				Class<? extends Object> class1 = value1.getClass();
				Class<? extends Object> class2 = value2.getClass();
				
				if(class1 == class2){
					if(class1 == String.class){
						if(!value1.equals(value2)){
							return false;
						}
					}else if(class1 == Date.class){
						if(((Date)value1).getTime() != ((Date)value2).getTime()){
							return false;
						}
					}else if(class1 == int.class || class1 == Integer.class){
						if(value1 != value2){
							return false;
						}
						
					}else if(class1 == long.class || class1 == Long.class){
						if(value1 != value2){
							return false;
						}
					}else if(class1 == double.class || class1 == Double.class){
						BigDecimal data1 = new BigDecimal((double)value1);
						BigDecimal data2 = new BigDecimal((double)value2);
						if(data1.compareTo(data2) != 0){
							return false;
						}
					}else if(class1 == float.class || class1 == Float.class){
						BigDecimal data1 = new BigDecimal((double)value1);
						BigDecimal data2 = new BigDecimal((double)value2);
						if(data1.compareTo(data2) != 0){
							return false;
						}
					}
				}else{
					return false;
				}
			}else{
//				System.out.println(key1);
//				continue;
				return false;
			}
		}
		System.out.println(i);
		return result;
	}
	
	public static Map<String,Object> getMap1(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse("2018-12-28 14:17:15");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Map<String,Object> row = new HashMap<String,Object>();
		row.put("name",  getStr());
		row.put("age", 15);
		row.put("money", 15.5);
		row.put("long", 12l);
		row.put("date", date);
		return row;
	}
	
	public static Map<String,Object> getMap2(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse("2018-12-28 14:17:15");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Map<String,Object> row = new HashMap<String,Object>();
		row.put("name",  getStr());
		row.put("age", 15);
		row.put("money", 15.5);
		row.put("long", 12l);
		row.put("date", date);
		return row;
	}
	
	
	public static String getStr(){
		return "开发角色放假时间发生纠纷的设计费凉快圣诞节福开发角纠纷的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发"
				+ "生纠纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉纠纷的设计费凉"
				+ "快圣诞节福开发角色放假时间发生纠纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发生"
				+ "纠纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉纠纷的设计费凉快"
				+ "圣诞节福开发角色放假时间发生纠纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发生纠"
				+ "纷的设计费凉纠纷的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东的设计费凉快圣诞节福开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境开发角色放假时间发生纠纷的设计费凉快圣诞节福利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境利看电视剧福克斯京东方科技独守空房京东数科发大水了房间里的设计费看电视剧发了跨境电商";
	}

}
