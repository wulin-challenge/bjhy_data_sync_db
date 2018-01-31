package com.bjhy.data.sync.db.loader.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bjhy.data.sync.db.domain.ConnectConfig;
import com.bjhy.data.sync.db.log.LogCache;
import com.bjhy.data.sync.db.util.FileUtil;
import com.bjhy.data.sync.db.util.LoggerUtils;

@SuppressWarnings("unchecked")
public class DataSourceLoaderXml {
	
	/**
	 * 根路径
	 */
	private final String ROOT_PATH = FileUtil.replaceSpritAndEnd(System.getProperty("user.dir"))+"config/";
	
	/**
	 * 主配置文件
	 */
	private final String dbXml = "db.xml";
	
	/**
	 * 加载xml文件
	 */
	public DataSources loadFileXml(){
		DataSources dataSources = new DataSources();
		//加载所有DataSource的配置文件
		loadAllDataSourcesFile(dataSources);
		//加载并解析所有import-data-source配置文件
		for (String dataSourcePath : dataSources.getImportDataSources()) {
			Element rootElement = loadRootElement(dataSourcePath, true);
			parseDataSourcesElement(rootElement, dataSources);
		}
		
		return dataSources;
	}
	
	/**
	 * 加载所有DataSource的配置文件
	 * @param dataSources
	 */
	private void loadAllDataSourcesFile(DataSources dataSources){
		
		//加载根文件
		String rootFile = ROOT_PATH+FileUtil.replaceSpritAndNotStart(dbXml);
		dataSources.getImportDataSources().add(rootFile);
		Element rootElement = loadRootElement(rootFile, false);
		if(rootElement != null){
			parseAndLoadImportDataSources(rootElement, dataSources);
		}
	}
	
	/**
	 * 加载根元素
	 * @param filePath
	 * @param logFlag 是否打印成功的日志
	 * @return
	 */
	private Element loadRootElement(String filePath,Boolean logFlag){
		Element root = null;
		
		try {
			File dbFile = new File(filePath);
			SAXReader reader = new SAXReader();
			Document document = reader.read(dbFile);
			root = document.getRootElement();
			if(logFlag){
				LogCache.addDataSourceLog("通过xml的形式加载所有数据源成功!!,配置文件:"+filePath);
				LoggerUtils.info("通过xml的形式加载所有数据源成功!!,配置文件:"+filePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogCache.addDataSourceLog("通过xml的形式加载所有数据源失败!!,失败的配置文件:"+filePath);
			LoggerUtils.error("通过xml的形式加载所有数据源失败!!,失败的配置文件:"+filePath);
		}
		return root;
	}
	
	/**
	 * 解析并加载 import-data-sources 标签元素
	 * @param parent
	 * @param dataSources
	 */
	private void parseAndLoadImportDataSources(Element parent,DataSources dataSources){
		//获取import-data-sources 元素
		Element importsElement = parent.element("import-data-sources");
		if(importsElement == null){
			return;
		}
		//获取import-data-source 元素
		List<Element> importElementList = importsElement.elements("import-data-source");
		if(importElementList == null || importElementList.isEmpty()){
			return;
		}
		
		for (Element element : importElementList) {
			String importPath = element.getText();
			if(StringUtils.isNotBlank(importPath)){
				importPath = importPath.trim();
				importPath = ROOT_PATH+FileUtil.replaceSpritAndNotStart(importPath);
				//判断该文件是否已经被加载过,防止死递归
				if(!dataSources.getImportDataSources().contains(importPath)){
					dataSources.getImportDataSources().add(importPath);
					//这里是递归加载import-data-source文件
					Element rootElement = loadRootElement(importPath, false);
					if(rootElement != null){
						parseAndLoadImportDataSources(rootElement, dataSources);
					}
				}
			}
		}
	}
	
	/**
	 * 解析DataSources子元素
	 * @param parent
	 * @param dataSources
	 */
	private void parseDataSourcesElement(Element parent,DataSources dataSources){
		
		//解析本地template
		Element nativeElement = parent.element("native-template");
		if(nativeElement != null){
			ConnectConfig nativeTemplate = parseConnectConfigElement(nativeElement);
			dataSources.setNativeTemplate(nativeTemplate);
		}
		
		//解析来源template
		Element fromElement = parent.element("from-template");
		if(fromElement != null){
			List<Element> fromElements = fromElement.elements("data-source");
			FromTemplate fromTemplate = dataSources.getFromTemplate();
			for (Element dataSourceElement : fromElements) {
				ConnectConfig connectConfig = parseConnectConfigElement(dataSourceElement);
				fromTemplate.getConnectConfigList().add(connectConfig);
			}
		}
		
		
		//解析目标template
		Element toElement = parent.element("to-template");
		if(toElement != null){
			List<Element> toElements = toElement.elements("data-source");
			ToTemplate toTemplate = dataSources.getToTemplate();
			for (Element dataSourceElement : toElements) {
				ConnectConfig connectConfig = parseConnectConfigElement(dataSourceElement);
				toTemplate.getConnectConfigList().add(connectConfig);
			}
		}
	}
	
	private ConnectConfig parseConnectConfigElement(Element parent){
		ConnectConfig connectConfig = new ConnectConfig();
		Attribute task = parent.attribute("task");
		Attribute isEnable = parent.attribute("isEnable");
		Attribute databaseType = parent.attribute("databaseType");
		
		Element sortNumber = parent.element("sort-number");
		Element dataSourceName = parent.element("data-source-name");
		Element dataSourceNumber = parent.element("data-source-number");
		Element connectDriver = parent.element("connect-driver");
		Element connectUrl = parent.element("connect-url");
		Element connectUsername = parent.element("connect-username");
		Element connectPassword = parent.element("connect-password");
		Element connectDialect = parent.element("connect-dialect");
		
		connectConfig.setTask(task.getText());
		
		String strIsEnable = isEnable.getText();
		if("true".equals(strIsEnable)){
			connectConfig.setIsEnable(true);
		}else if("false".equals(strIsEnable)){
			connectConfig.setIsEnable(false);
		}
		
		connectConfig.setDatabaseType(databaseType.getText());
		
		String strSortNumber = sortNumber.getText();
		if(StringUtils.isNotEmpty(strSortNumber)){
			Integer intSortNumber = Integer.parseInt(strSortNumber);
			connectConfig.setSortNumber(intSortNumber);
		}else{
			connectConfig.setSortNumber(null);
		}
		
		connectConfig.setDataSourceName(dataSourceName.getText());
		connectConfig.setDataSourceNumber(dataSourceNumber.getText());
		connectConfig.setConnectDriver(connectDriver.getText());
		connectConfig.setConnectUrl(connectUrl.getText());
		connectConfig.setConnectUsername(connectUsername.getText());
		connectConfig.setConnectPassword(connectPassword.getText());
		connectConfig.setConnectDialect(connectDialect.getText());
		return connectConfig;
	}
	
	/**
	 * 数据源bean
	 * @author Administrator
	 *
	 */
	public class DataSources{
		/**
		 * 本地template
		 */
		private ConnectConfig nativeTemplate;
		
		/**
		 * 来源Template
		 */
		private FromTemplate fromTemplate = new FromTemplate();
		
		/**
		 * 目标Template
		 */
		private ToTemplate toTemplate = new ToTemplate();
		
		/**
		 * 导入dataSource文件
		 */
		private List<String> importDataSources = new ArrayList<String>();

		public ConnectConfig getNativeTemplate() {
			return nativeTemplate;
		}

		public void setNativeTemplate(ConnectConfig nativeTemplate) {
			this.nativeTemplate = nativeTemplate;
		}

		public FromTemplate getFromTemplate() {
			return fromTemplate;
		}

		public void setFromTemplate(FromTemplate fromTemplate) {
			this.fromTemplate = fromTemplate;
		}

		public ToTemplate getToTemplate() {
			return toTemplate;
		}

		public void setToTemplate(ToTemplate toTemplate) {
			this.toTemplate = toTemplate;
		}

		public List<String> getImportDataSources() {
			return importDataSources;
		}

		public void setImportDataSources(List<String> importDataSources) {
			this.importDataSources = importDataSources;
		}
	}
	
	/**
	 * 来源Template
	 * @author wubo
	 *
	 */
	public class FromTemplate{
		private List<ConnectConfig> connectConfigList = new ArrayList<ConnectConfig>();

		public List<ConnectConfig> getConnectConfigList() {
			return connectConfigList;
		}
		
	}
	
	/**
	 * 目标Template
	 * @author wubo
	 *
	 */
	public class ToTemplate{
		private List<ConnectConfig> connectConfigList = new ArrayList<ConnectConfig>();

		public List<ConnectConfig> getConnectConfigList() {
			return connectConfigList;
		}
		
	}

}
