package com.bjhy.data.sync.db.test;

import com.bjhy.data.sync.db.loader.xml.DataSourceLoaderXml;
import com.bjhy.data.sync.db.loader.xml.DataSourceLoaderXml.DataSources;

public class TestLoaderXml {
	
	public static void main(String[] args) {
		System.out.println();
		DataSourceLoaderXml dataSourceLoaderXml = new DataSourceLoaderXml();
		DataSources dataSource = dataSourceLoaderXml.loadFileXml();
		System.out.println(dataSource);
	}

}
