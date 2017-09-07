package com.bjhy.data.sync.db.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import com.bjhy.data.sync.db.domain.ConnectConfig;
import com.bjhy.data.sync.db.domain.SingleStepSyncConfig;
import com.bjhy.data.sync.db.domain.SyncTemplate;
import com.bjhy.data.sync.db.loader.DataSourceLoader;
import com.bjhy.data.sync.db.util.SeriUtil;

/**
 * 测试序列化
 * @author wubo
 *
 */
public class TestSeri {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		for (int i = 0; i < 100000; i++) {
//			Thread.sleep(50);
			SyncTemplate syncTemplate = getSyncTemplate(); //得到数据
			exportObject(syncTemplate, SyncTemplate.class); //序列化对象,并将序列化后的对象存储于本地
			System.out.println();
//			SyncTemplate importObject = importObject(SyncTemplate.class);//读取本地存储的二进制数据,并将其反序列化为对象
		}

//		System.out.println();
		
//
		
	}
	
	/**
	 * 读取本地存储的二进制数据,并将其反序列化为对象
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> T importObject(Class<T> clazz) throws IOException, ClassNotFoundException{
		String string = UUID.randomUUID().toString();
		File cacheDataFile = new File(System.getProperty("user.dir")+"/cacheData/"+string+".data");
		
		FileInputStream fis = new FileInputStream(cacheDataFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		byte[] data = (byte[]) ois.readObject();
		T syncTemplate = SeriUtil.unserializeProtoStuffToObj(data, clazz);
		fis.close();
		ois.close();//这个写法是为了方便,但不合理
		return syncTemplate;
	}
	
	
	/**
	 * 序列化对象,并将序列化后的对象存储于本地
	 * @param obj
	 * @param clazz
	 * @throws IOException
	 */
	public static <T> void exportObject(T obj,Class<T> clazz) throws IOException{
		String string = UUID.randomUUID().toString();
		File cacheDataDir = new File(System.getProperty("user.dir")+"/cacheData");
		
		if(!cacheDataDir.exists()){
			cacheDataDir.mkdirs();
		}
		
		File cacheDataFile = new File(System.getProperty("user.dir")+"/cacheData/"+string+".data");
		
		
		FileOutputStream fos = new FileOutputStream(cacheDataFile);  
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        byte[] data = SeriUtil.serializeProtoStuffTobyteArray(obj, clazz);
		oos.writeObject(data);
		oos.close();
	}

	private static SyncTemplate getSyncTemplate(){
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
		
		//切记:序列化中最好不要序列化 数据源DataSource ,因为DataSource太复杂了
//		syncTemplate.setDataSource(dataSource);
//		syncTemplate.setDriverManagerDataSource(driverManagerDataSource);
		return syncTemplate;
	}
}
