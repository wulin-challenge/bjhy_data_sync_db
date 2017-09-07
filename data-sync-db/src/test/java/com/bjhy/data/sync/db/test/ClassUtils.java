package com.bjhy.data.sync.db.test;

import java.lang.reflect.ParameterizedType;  
import java.lang.reflect.Type;  
import java.util.HashMap;  
import java.util.Date;  
import java.util.Map;  
  
public class ClassUtils {  
      
    private static final Map<Class<?>, Class<?>> primitiveMap = new HashMap<>(9);  
      
    static {  
        primitiveMap.put(String.class, String.class);  
        primitiveMap.put(Boolean.class, boolean.class);  
        primitiveMap.put(Byte.class, byte.class);  
        primitiveMap.put(Character.class, char.class);  
        primitiveMap.put(Double.class, double.class);  
        primitiveMap.put(Float.class, float.class);  
        primitiveMap.put(Integer.class, int.class);  
        primitiveMap.put(Long.class, long.class);  
        primitiveMap.put(Short.class, short.class);  
                primitiveMap.put(Date.class, Date.class);  
       }  
      
    /** 
          * @description 判断基本类型 
          * @see     java.lang.String#TYPE 
          * @see     java.lang.Boolean#TYPE 
          * @see     java.lang.Character#TYPE 
          * @see     java.lang.Byte#TYPE 
          * @see     java.lang.Short#TYPE 
          * @see     java.lang.Integer#TYPE 
          * @see     java.lang.Long#TYPE 
          * @see     java.lang.Float#TYPE 
          * @see     java.lang.Double#TYPE 
          * @see     java.lang.Boolean#TYPE 
          * @see     char#TYPE 
          * @see     byte#TYPE 
          * @see     short#TYPE 
          * @see     int#TYPE 
          * @see     long#TYPE 
          * @see     float#TYPE 
          * @see     double#TYPE 
      * @param clazz 
      * @return boolean 
     */  
    public static boolean isPrimitive(Class<?> clazz) {  
        if (primitiveMap.containsKey(clazz)) {  
            return true;  
        }  
        return clazz.isPrimitive();  
    }  
      
    /** 
     * @description 获取方法返回值类型 
     * @param tartget 
     * @param fieldName 
     * @return 
     * @return Class<?> 
     */  
    public static Class<?> getElementType(Class<?> tartget, String fieldName) {  
        Class<?> elementTypeClass = null;  
        try {  
            Type type = tartget.getDeclaredField(fieldName).getGenericType();  
            ParameterizedType t = (ParameterizedType) type;  
            String classStr = t.getActualTypeArguments()[0].toString().replace("class ", "");  
            elementTypeClass = Thread.currentThread().getContextClassLoader().loadClass(classStr);  
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {  
            throw new RuntimeException("get fieldName[" + fieldName + "] error", e);  
        }  
        return elementTypeClass;  
    }  
  
}  
