package com.bjhy.data.sync.db.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class SeriUtil {

	/**
	 * 序列化对象
	 * @param obj 序列化对象
	 * @param clazz 要序列化对象的class
	 * @return 返回被序列化对象的二进制数组
	 */
	public static <T> byte[] serializeProtoStuffTobyteArray(T obj,Class<T> clazz) {

		Schema<T> schema = RuntimeSchema.getSchema(clazz);
		LinkedBuffer buffer = LinkedBuffer.allocate(4096);
		byte[] byteArray = ProtostuffIOUtil.toByteArray(obj, schema, buffer);

		return byteArray;
	}

	/**
	 * 反序列化对象
	 * @param data  被反序列化对象的二进制数组
	 * @param clazz 要反序列化对象的class
	 * @return 返回真正的对象
	 */
	public static <T> T unserializeProtoStuffToObj(byte[] data, Class<T> clazz) {
		T newInstance = null;
		try {
			newInstance = clazz.newInstance();
			Schema<T> schema = RuntimeSchema.getSchema(clazz);
			ProtostuffIOUtil.mergeFrom(data, newInstance, schema);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newInstance;
	}

}
