package com.bjhy.data.sync.db.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql工具类
 * @author wubo
 *
 */
public class SqlUtil {
	
	 private static final Pattern FROM_PATTERN_1 = Pattern.compile("from\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
	 private static final Pattern FROM_PATTERN_2 = Pattern.compile("from\\s+\\((.*)\\)\\s*");
	
	 /**
	  * 是否是简单sql,是返回true,否返回false
	  * 注意:通过正则表达式得到sql的主表名(在没有只查询的情况下),且fromsql只能是 from 及后面的部分,不能有select部分
	  * @param fromSql
	  * @return
	  */
	 public static Boolean isSimpleSql(String fromSql){
		 String tempSql = fromSql;
		 tempSql = tempSql.toUpperCase();
		 if(tempSql.contains("WHERE")){
			 return false;
		 }
		 if(tempSql.contains("ORDER")){
			 return false;
		 }
		 if(tempSql.contains("GROUP")){
			 return false;
		 }
		//如果匹配成功表示当前sql有子查询,则返回false
		Matcher matcher = FROM_PATTERN_2.matcher(fromSql);
		if (matcher.find()) {
			return false;
		}
		
		Boolean isSimpleSql;
		//表示当前sql没有只查询,则返回表名
		matcher = FROM_PATTERN_1.matcher(fromSql);
		if (matcher.find()) {
			isSimpleSql = true;
		}else{
			isSimpleSql = false;
		}
		return isSimpleSql;
	 }
	 
//	public static void main(String[] args) {
//		String sql = "select * from ded";
////		String sql = "select (select * from xx) s from ded";
////		String sql = "select * from (select * from dddd0_) ";
////		String sql = "select (select * from xx) s from (select * from dddd0_) ";
//		String from = getTableName(sql);
//        System.out.println(from);
//	}

}
