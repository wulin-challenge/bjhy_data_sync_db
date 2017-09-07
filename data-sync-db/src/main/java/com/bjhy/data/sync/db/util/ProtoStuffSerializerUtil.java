//package com.bjhy.data.sync.db.util;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Date;
//import java.util.List;
//
//import com.dyuproject.protostuff.LinkedBuffer;
//import com.dyuproject.protostuff.ProtostuffIOUtil;
//import com.dyuproject.protostuff.Schema;
//import com.dyuproject.protostuff.runtime.RuntimeSchema;
//
///**
// * 
// * <pre>
// * 序列化工具
// * </pre>
// *
// */
//public class ProtoStuffSerializerUtil {
//
//	/**
//	 * 序列化对象 ：将对象转换为byte[]
//	 * @param obj
//	 * @return
//	 */
//    public static <T> byte[] serialize(T obj) {
//        if (obj == null) {
//            throw new RuntimeException("序列化对象(" + obj + ")!");
//        }
//        @SuppressWarnings("unchecked")
//        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
//        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
//        byte[] protostuff = null;
//        try {
//            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
//        } catch (Exception e) {
//            throw new RuntimeException("序列化(" + obj.getClass() + ")对象(" + obj + ")发生异常!", e);
//        } finally {
//            buffer.clear();
//            
////            messagePack;
//        }
//        return protostuff;
//    }
//
//    
//    /**
//     * 反序列化对象：将byte[] 转化为 对象
//     * @param paramArrayOfByte
//     * @param targetClass
//     * @return
//     */
//    public static <T> T deserialize(byte[] paramArrayOfByte, Class<T> targetClass) {
//        if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
//            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
//        }
//        T instance = null;
//        try {
//            instance = targetClass.newInstance();
//        } catch (InstantiationException | IllegalAccessException e) {
//            throw new RuntimeException("反序列化过程中依据类型创建对象失败!", e);
//        }
//        Schema<T> schema = RuntimeSchema.getSchema(targetClass);
//        ProtostuffIOUtil.mergeFrom(paramArrayOfByte, instance, schema);
//        return instance;
//    }
//    
//    /**
//     * 序列化对集合：将集合转换为byte[]
//     * @param objList
//     * @return
//     */
//    public static <T> byte[] serializeList(List<T> objList) {
//        if (objList == null || objList.isEmpty()) {
//            throw new RuntimeException("序列化对象列表(" + objList + ")参数异常!");
//        }
//        @SuppressWarnings("unchecked")
//        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objList.get(0).getClass());
//        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
//        byte[] protostuff = null;
//        ByteArrayOutputStream bos = null;
//        try {
//            bos = new ByteArrayOutputStream();
//            ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
//            protostuff = bos.toByteArray();
//        } catch (Exception e) {
//            throw new RuntimeException("序列化对象列表(" + objList + ")发生异常!", e);
//        } finally {
//            buffer.clear();
//            try {
//                if(bos!=null){
//                    bos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        
//        return protostuff;
//    }
//    
//    
//    /**
//     * 反序列化集合：将byte[] 转化为 集合对象
//     * @param paramArrayOfByte
//     * @param targetClass
//     * @return
//     */
//    public static <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
//        if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
//            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
//        }
//        
//        Schema<T> schema = RuntimeSchema.getSchema(targetClass);
//        List<T> result = null;
//        try {
//            result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
//        } catch (IOException e) {
//            throw new RuntimeException("反序列化对象列表发生异常!",e);
//        }
//        return result;
//    }
//
//    
////    public static void main(String[] args) throws  Exception {
////
////		JmsMessage jmsMessage = new JmsMessage();
////		File[] files = new File[2];
////		files[0] = new File("I:/temp/1.txt");
////		files[1] = new File("I:/temp/2.txt");
////		File zipFile = ZipFileUtil.zipFiles(files, "I:/temp/", "zipFile");
////		try(FileInputStream fis = new FileInputStream(zipFile);
////            	ByteArrayOutputStream bos = new ByteArrayOutputStream();){
////    		int temp = 0;
////        	while((temp = fis.read()) != -1){
////        		bos.write(temp);
////        	}
////        	byte[] attachment = bos.toByteArray();
////        	jmsMessage.setBytes(attachment);
////        	jmsMessage.setTime(new Date());
////		}
////		//byte[] bytes = MessagePackUtil.encode(jmsMessage);
////		byte[] bytes = ProtoStuffSerializerUtil.serialize(jmsMessage);
////		
////		JmsMessage msg = ProtoStuffSerializerUtil.deserialize(bytes, JmsMessage.class);
////		//JmsMessage msg = MessagePackUtil.read(bytes,JmsMessage.class );
////		System.out.println(msg);
////	
////	}
//    
//}