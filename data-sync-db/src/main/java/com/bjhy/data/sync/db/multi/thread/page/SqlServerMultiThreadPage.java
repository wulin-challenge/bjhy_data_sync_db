package com.bjhy.data.sync.db.multi.thread.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.AbstractMultiThreadPage;

/**
 * 没有多线程的实现
 * @author wubo
 *
 */
@SuppressWarnings({"unchecked" })
public class SqlServerMultiThreadPage extends AbstractMultiThreadPage{
	
	/**
	 * 每页分页数
	 */
	private Integer pageNumber = 50;
	
	public SqlServerMultiThreadPage(SyncLogicEntity syncLogicEntity) {
		super(syncLogicEntity);
	}
	
	@Override
	public List<Map<String, Object>> pageData(int index) {
	    List<Map<String, Object>> highPerformancePageData = null;
		String highPerformancePageColumn = syncLogicEntity.getSingleStepSyncConfig().getHighPerformancePageColumn();
		if(highPerformancePageColumn != null){
			 String highPerformancePageSql = getHighPerformancePageSql(index);
			  highPerformancePageData = getHighPerformancePageData(highPerformancePageSql);
		}else{
			throw new RuntimeException("sqlServer 只支持高性能的分页,不支持普通分页");
		}
		
		return highPerformancePageData;
	}

	/**
	 * 普通的分页sql
	 * @param index
	 * @return
	 */
	private String getGeneralPageSql(int index){
		
		
		return null;
	}
	
	
	/**
	 * 得到普通分页数据
	 * @param firstGeneralPageSql
	 * @return
	 */
	private List<Map<String, Object>> getGeneralPageData(String firstGeneralPageSql){
		List<Map<String, Object>> pageData = syncLogicEntity.getNamedFromTemplate().queryForList(firstGeneralPageSql, Collections.EMPTY_MAP);
		return pageData;
	}
	
	/**
	 * 第一次处理高性能的分页Sql
	 * @param index
	 * @return
	 */
	private String getHighPerformancePageSql(int index){
		String highPerformancePageColumn = syncLogicEntity.getSingleStepSyncConfig().getHighPerformancePageColumn();
		String fromSql = syncLogicEntity.getSingleStepSyncConfig().getFromSql();
		
		String firstHighPerformancePageSql = "select top "+pageNumber+" o."+highPerformancePageColumn+" "
				+" from ( "
				+" "+fromSql+" "
				+" ) o "
				+" where "+highPerformancePageColumn+" not in "
				+" ( "
				+"  select top "+getStartPageIndex(index)+" a_."+highPerformancePageColumn+" from ("+fromSql+") a_ order by a_."+highPerformancePageColumn+" "
				+" ) ";
		return firstHighPerformancePageSql;
	}
	
	/**
	 * 深度处理高性能的分页sql,然后得到数据
	 * @param firstHighPerformancePageSql
	 * @return
	 */
	private List<Map<String, Object>> getHighPerformancePageData(String firstHighPerformancePageSql){
		String highPerformancePageColumn = syncLogicEntity.getSingleStepSyncConfig().getHighPerformancePageColumn();
		List<Map<String, Object>> pageColumnData = syncLogicEntity.getNamedFromTemplate().queryForList(firstHighPerformancePageSql, Collections.EMPTY_MAP);
		
		List<Object> pageColumnList = getPageColumnList(pageColumnData);
		
		Map<String,Object> params = new HashMap<String,Object>();
		
		params.put(highPerformancePageColumn, pageColumnList);
		
		String fromSql = syncLogicEntity.getSingleStepSyncConfig().getFromSql();
		
		String pageFramSql = "SELECT * FROM ("+fromSql+") a_ where a_."+highPerformancePageColumn+" IN(:"+highPerformancePageColumn+")";
		List<Map<String, Object>> queryForList = syncLogicEntity.getNamedFromTemplate().queryForList(pageFramSql, params);
		return queryForList;
	}
	
	/**
	 * 得到分页列的参数数据
	 * @param pageColumnData
	 * @return
	 */
	private List<Object> getPageColumnList(List<Map<String, Object>> pageColumnData){
		List<Object> pageColumnList = new ArrayList<Object>();
		for (Map<String, Object> map : pageColumnData) {
			Set<Entry<String, Object>> entrySet = map.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				pageColumnList.add(entry.getValue());
			}
		}
		return pageColumnList;
	}
	
	/**
	 * 开始分页的索引
	 * @param index
	 * @return
	 */
	private Integer getStartPageIndex(Integer index){
		index +=1;
		int start = ((index-1)*pageNumber);
		return start;
	}
	
	/**
	 * 结束分页的索引
	 * @param index
	 * @return
	 */
	private Integer getEndPageIndex(Integer index){
		index +=1;
		int end = index*pageNumber;
		return end;
	}

	/**
	 * 迭代次数
	 */
	
	@Override
	public Integer pageIterations() {
		NamedParameterJdbcTemplate namedFromTemplate = syncLogicEntity.getNamedFromTemplate();
		
		String fromCountSql = syncLogicEntity.getSingleStepSyncConfig().getFromCountSql();
		Integer dataTotal = namedFromTemplate.queryForObject(fromCountSql, Collections.EMPTY_MAP,Integer.class);
		int totalPages = getTotalPages(dataTotal);
		return totalPages;
	}
	
	/**
	 * 得到总共的页数
	 * @param allRows
	 * @return
	 */
	public int getTotalPages(Integer dataTotal){
		int totalPages = 0;
		
		if((dataTotal%pageNumber) == 0){
			totalPages = (int) (dataTotal/pageNumber);
		}else{
			totalPages = (int) ((dataTotal/pageNumber)+1);
		}
		return totalPages;
	}

	@Override
	public Integer stepMaxThreadNumber() {
		return syncLogicEntity.getSingleStepSyncConfig().getSingleRunEntity().getBaseRunEntity().getTablePageMaxThreadNum();
	}

}
