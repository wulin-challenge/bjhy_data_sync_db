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
import com.bjhy.data.sync.db.util.LoggerUtils;

@SuppressWarnings("unchecked")
public class DataSourceLoaderXml {
	
	private String dbXml = "/config/db.xml";
	
	/**
	 * 加载xml文件
	 */
	public DataSources loadFileXml(){
		DataSources dataSources = new DataSources();
		try {
			File dbFile = new File(System.getProperty("user.dir")+dbXml);
			SAXReader reader = new SAXReader();
			Document document = reader.read(dbFile);
			Element root = document.getRootElement();
			
			parseDataSourcesElement(root, dataSources);
			LogCache.addDataSourceLog("通过xml的形式加载所有数据源成功!!");
			LoggerUtils.info("通过xml的形式加载所有数据源成功!!");
		} catch (DocumentException e) {
			e.printStackTrace();
			LogCache.addDataSourceLog("通过xml的形式加载所有数据源失败!!");
			LoggerUtils.error("通过xml的形式加载所有数据源失败!!");
		}
		return dataSources;
	}
	
	/**
	 * 解析DataSources子元素
	 * @param parent
	 * @param dataSources
	 */
	private void parseDataSourcesElement(Element parent,DataSources dataSources){
		
		//解析本地template
		Element nativeElement = parent.element("native-template");
		ConnectConfig nativeTemplate = parseConnectConfigElement(nativeElement);
		dataSources.setNativeTemplate(nativeTemplate);
		
		//解析来源template
		Element fromElement = parent.element("from-template");
		List<Element> fromElements = fromElement.elements("data-source");
		FromTemplate fromTemplate = new FromTemplate();
		for (Element dataSourceElement : fromElements) {
			ConnectConfig connectConfig = parseConnectConfigElement(dataSourceElement);
			fromTemplate.getConnectConfigList().add(connectConfig);
		}
		dataSources.setFromTemplate(fromTemplate);
		
		//解析目标template
		Element toElement = parent.element("to-template");
		List<Element> toElements = toElement.elements("data-source");
		ToTemplate toTemplate = new ToTemplate();
		for (Element dataSourceElement : toElements) {
			ConnectConfig connectConfig = parseConnectConfigElement(dataSourceElement);
			toTemplate.getConnectConfigList().add(connectConfig);
		}
		dataSources.setToTemplate(toTemplate);
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
		private FromTemplate fromTemplate;
		
		/**
		 * 目标Template
		 */
		private ToTemplate toTemplate;

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
