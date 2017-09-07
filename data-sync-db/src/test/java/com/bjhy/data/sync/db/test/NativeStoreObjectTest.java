package com.bjhy.data.sync.db.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.bjhy.data.sync.db.domain.ConnectConfig;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunThread;
import com.bjhy.data.sync.db.loader.DataSourceLoader;
import com.bjhy.data.sync.db.thread.ThreadControl;
import com.bjhy.data.sync.db.util.SeriUtil;

/**
 * 本地存储对象缓存
 * @author wubo
 *
 */
public class NativeStoreObjectTest {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		for (int i = 0; i < 10; i++) {
			SyncTemplate dataObj = (SyncTemplate) getDataObj();
			
			byte[] serializeProtoStuffTobyteArray = SeriUtil.serializeProtoStuffTobyteArray(dataObj, SyncTemplate.class);
			System.out.println(serializeProtoStuffTobyteArray.length);
			SyncTemplate unserializeProtoStuffToObj = SeriUtil.unserializeProtoStuffToObj(serializeProtoStuffTobyteArray, SyncTemplate.class);
			System.out.println(serializeProtoStuffTobyteArray.length);
		}
		
//		exportObject(dataObj,SyncTemplate.class );
//		
//		importObject();
		
//		System.out.println();
		
//		testDataSource();
	}
	
	private static void testDataSource(){
		final List<SyncTemplate> syncList = new ArrayList<SyncTemplate>();
		
		
		
			ThreadControl control = new ThreadControl(100);
			control.forRunStart(1000000, new ForRunThread(){

				@Override
				public void currentThreadRunning(int iterations, int i) {
					SyncTemplate dataObj = (SyncTemplate) getDataObj();
					syncList.add(dataObj);
					
					if(syncList.size() >100){
						System.out.println(syncList.size());
						syncList.clear();
					}
				}
				
			});
			
			
			
//			SyncTemplate dataObj = (SyncTemplate) getDataObj();
		System.out.println();
	}
	
	private static Object getDataObj(){
		
		ConnectConfig conn = new ConnectConfig();
		conn.setConnectDialect("org.hibernate.dialect.OracleDialect");
		conn.setConnectDriver("oracle.jdbc.driver.OracleDriver");
		conn.setConnectPassword("wulin");
		conn.setConnectUrl("jdbc:oracle:thin:@localhost:1521:orcl");
		conn.setConnectUsername("wulin");
		conn.setDatabaseType("oracle");
		conn.setDataSourceDirection("from");
		conn.setDataSourceName("我是数据源11");
		conn.setDataSourceNumber("111111111");
		conn.setIsEnable(true);
		conn.setSortNumber(1);
		conn.setTask("task1");
		
		
		SyncTemplate syncTemplate = new SyncTemplate();
		syncTemplate.setConnectConfig(conn);
		
		DataSource dataSource = DataSourceLoader.getInstance().getDataSource(conn);
		DataSource driverManagerDataSource = DataSourceLoader.getInstance().getDriverManagerDataSource(conn);
		
//		syncTemplate.setDataSource(dataSource);
//		syncTemplate.setDriverManagerDataSource(driverManagerDataSource);
		return syncTemplate;
	}
	
	public static void importObject() throws IOException, ClassNotFoundException{
		File cacheDataFile = new File(System.getProperty("user.dir")+"/cacheData/xx.data");
		
		FileInputStream fis = new FileInputStream(cacheDataFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		SyncTemplate syncTemplate = (SyncTemplate) ois.readObject();
		System.out.println();
		
	}
	
	public static <T> void exportObject(T obj,Class<T> clazz) throws IOException{
		File cacheDataDir = new File(System.getProperty("user.dir")+"/cacheData");
		
		if(!cacheDataDir.exists()){
			cacheDataDir.mkdirs();
		}
		
		File cacheDataFile = new File(System.getProperty("user.dir")+"/cacheData/xx.data");
		
		
		FileOutputStream fos = new FileOutputStream(cacheDataFile);  
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        System.out.println();
        byte[] data = SeriUtil.serializeProtoStuffTobyteArray(obj, clazz);
        T unserializeProtoStuffToObj = SeriUtil.unserializeProtoStuffToObj(data, clazz);
        System.out.println();
//        byte[] serialize = ProtoStuffSerializerUtil.serialize(obj);
//        byte[] serializeProtoStuffProductsList = SeriUtil.serializeProtoStuffProductsList(obj, clazz);
//		oos.writeObject(serializeProtoStuffProductsList);
		oos.close();
	}
	
	
	
	

}
