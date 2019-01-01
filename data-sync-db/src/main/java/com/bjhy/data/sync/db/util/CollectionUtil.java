package com.bjhy.data.sync.db.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 集合工具类
 * @author wubo
 *
 */
public class CollectionUtil {
	
	/**
	 * 得到两个结合的交集<br/>
	 * 说明:请将内部数据较少的集合传参是传给第一个,较多数据的集合传给第二个,这样内部在处理时效率会更高些
	 * 
	 * @param lessCollection 数据较少的集合
	 * @param moreCollection 数据较多的集合
	 * @return
	 */
	public static Collection<String> getCollectionIntersection(Collection<String> lessCollection,Collection<String> moreCollection){
		//交集集合
		Set<String> intersectionSet = new HashSet<String>(lessCollection);
		
		if(lessCollection == null || lessCollection.size()==0){
			return new HashSet<String>();
		}
		if(moreCollection == null || moreCollection.size()==0){
			return new HashSet<String>();
		}
		
		Set<String> moreSet = new HashSet<String>(moreCollection);
		
		intersectionSet.removeAll(moreSet);
		lessCollection.removeAll(intersectionSet);
		return lessCollection;
	}
	
	/**
	 * 字符型集合转大写
	 * @param collection 要转大写的集合
	 * @return 返回转大写后的集合
	 */
	public static Collection<String> collectionToUpperCase(Collection<String> collection){
		if(collection == null){
			return collection;
		}
		List<String> list = new ArrayList<String>();
		
		for (String value : collection) {
			list.add(value.toUpperCase());
		}
		return list;
	}

}
