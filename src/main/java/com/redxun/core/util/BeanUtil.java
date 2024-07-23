
package com.redxun.core.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import com.redxun.core.bean.BeanDateConverter;
/**
 * 
 * @author mansan
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class BeanUtil {

	protected static Logger logger=LogManager.getLogger(BeanUtil.class);
	/**
	 * BeanUtil类型转换器
	 */
	public static ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

	private static BeanUtilsBean beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());

	static {
		convertUtilsBean.register(new BeanDateConverter(), Date.class);
		convertUtilsBean.register(new LongConverter(null), Long.class);
	}

	/**
	 * 拷贝一个bean中的非空属性于另一个bean中
	 * 
	 * @param dest
	 *            目标bean
	 * @param orig
	 *            源bean
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("rawtypes")
	public static void copyNotNullProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
		BeanUtilsBean beanUtils = BeanUtilsBean.getInstance();
		// Validate existence of the specified beans
		if (dest == null) {
			throw new IllegalArgumentException("No destination bean specified");
		}
		if (orig == null) {
			throw new IllegalArgumentException("No origin bean specified");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("BeanUtils.copyProperties(" + dest + ", " + orig + ")");
		}

		// Copy the properties, converting as necessary
		if (orig instanceof DynaBean) {
			DynaProperty[] origDescriptors = ((DynaBean) orig).getDynaClass().getDynaProperties();
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				// Need to check isReadable() for WrapDynaBean
				// (see Jira issue# BEANUTILS-61)
				if (beanUtils.getPropertyUtils().isReadable(orig, name) && beanUtils.getPropertyUtils().isWriteable(dest, name)) {
					Object value = ((DynaBean) orig).get(name);
					beanUtils.copyProperty(dest, name, value);
				}
			}
		} else if (orig instanceof Map) {
			Iterator entries = ((Map) orig).entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry entry = (Map.Entry) entries.next();
				String name = (String) entry.getKey();
				if (beanUtils.getPropertyUtils().isWriteable(dest, name)) {
					beanUtils.copyProperty(dest, name, entry.getValue());
				}
			}
		} else /* if (orig is a standard JavaBean) */{
			PropertyDescriptor[] origDescriptors = beanUtils.getPropertyUtils().getPropertyDescriptors(orig);
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if ("class".equals(name)) {
					continue; // No point in trying to set an object's class
				}
				if (beanUtils.getPropertyUtils().isReadable(orig, name) && beanUtils.getPropertyUtils().isWriteable(dest, name)) {
					try {
						Object value = beanUtils.getPropertyUtils().getSimpleProperty(orig, name);
						if (value != null) {
							if (value instanceof HashSet) {
								HashSet valMap = (HashSet) value;
								if (valMap.size() > 0) {
									beanUtils.copyProperty(dest, name, value);
								}
							} else {
								beanUtils.copyProperty(dest, name, value);
							}
						}
					} catch (NoSuchMethodException e) {
						// Should not happen
					}
				}
			}
		}

	}

	public static void copyProperties(Object dest, Object orig) {
		try {
			beanUtilsBean.copyProperties(dest, orig);
		} catch (Exception e) {
			ReflectionUtils.handleReflectionException(e);
			logger.error(e.getMessage());
		}
	}

	public static void copyProperty(Object bean, String name, Object value) {
		try {
			beanUtilsBean.copyProperty(bean, name, value);
		} catch (Exception e) {
			ReflectionUtils.handleReflectionException(e);
			logger.error(e.getMessage());
		}
	}

	/**
	 * 取得能转化类型的bean
	 * 
	 * @return
	 */
	public static BeanUtilsBean getBeanUtils() {
		BeanUtilsBean beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
		return beanUtilsBean;
	}

	/**
	 * 通过Map转化为entity
	 * 
	 * @param entity
	 * @param dataMap
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 *             Object
	 * @exception
	 * @since 1.0.0
	 */
	public static Object populateEntity(Object entity, Map<String, Object> dataMap) throws IllegalAccessException, InvocationTargetException {
		getBeanUtils().populate(entity, dataMap);
		return entity;
	}

	/**
	 * 对一个bean进行深度复制，所有的属性节点全部会被复制
	 * 
	 * @param source
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @see [类、类#方法、类#成员]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T deepCopyBean(T source) {
		if (source == null) {
			return null;
		}
		try {
			if (source instanceof Collection) {
				return (T) deepCopyCollection((Collection) source);
			}
			if (source.getClass().isArray()) {
				return (T) deepCopyArray(source);
			}
			if (source instanceof Map) {
				return (T) deepCopyMap((Map) source);
			}
			if (source instanceof Date) {
				return (T) new Date(((Date) source).getTime());
			}
			if (source instanceof Timestamp) {
				return (T) new Timestamp(((Timestamp) source).getTime());
			}
			// 基本类型直接返回原值
			if (source.getClass().isPrimitive() || source instanceof String || source instanceof Boolean || Number.class.isAssignableFrom(source.getClass())) {
				return source;
			}
			if (source instanceof StringBuilder) {
				return (T) new StringBuilder(source.toString());
			}
			if (source instanceof StringBuffer) {
				return (T) new StringBuffer(source.toString());
			}
			Object dest = source.getClass().newInstance();
			BeanUtilsBean bean = BeanUtilsBean.getInstance();
			PropertyDescriptor[] origDescriptors = bean.getPropertyUtils().getPropertyDescriptors(source);
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if ("class".equals(name)) {
					continue;
				}

				if (bean.getPropertyUtils().isReadable(source, name) && bean.getPropertyUtils().isWriteable(dest, name)) {
					try {
						Object value = deepCopyBean(bean.getPropertyUtils().getSimpleProperty(source, name));
						bean.getPropertyUtils().setSimpleProperty(dest, name, value);
					} catch (NoSuchMethodException e) {
					}
				}
			}
			return (T) dest;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Collection deepCopyCollection(Collection source) throws InstantiationException, IllegalAccessException {
		Collection dest = source.getClass().newInstance();
		for (Object o : source) {
			dest.add(deepCopyBean(o));
		}
		return dest;
	}

	private static Object deepCopyArray(Object source) throws InstantiationException, IllegalAccessException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
		int length = Array.getLength(source);
		Object dest = Array.newInstance(source.getClass().getComponentType(), length);
		if (length == 0) {
			return dest;
		}
		for (int i = 0; i < length; i++) {
			Array.set(dest, i, deepCopyBean(Array.get(source, i)));
		}
		return dest;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map deepCopyMap(Map source) throws InstantiationException, IllegalAccessException {
		Map dest = source.getClass().newInstance();
		for (Object o : source.entrySet()) {
			Entry e = (Entry) o;
			dest.put(deepCopyBean(e.getKey()), deepCopyBean(e.getValue()));
		}
		return dest;
	}

	/**
	 * 把实体类中的所有声明的字段及值转为Map
	 * 
	 * @param entity
	 *            实体对象
	 * @return Map<String,Object>
	 * @exception
	 * @since 1.0.0
	 */
	public static Map<String, Object> convertFieldToMap(Object entity) {
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		Class<?> cls = entity.getClass();
		for (; cls != Object.class; cls = cls.getSuperclass()) {
			Field[] fs = cls.getDeclaredFields();
			for (Field f : fs) {
				try {
					Method m = cls.getDeclaredMethod("get" + StringUtil.makeFirstLetterUpperCase(f.getName()), new Class[] {});
					Object fieldVal = m.invoke(entity, new Object[] {});
					fieldMap.put(f.getName(), fieldVal);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return fieldMap;
	}

	/**
	 * 从实体对象中获取属性值
	 * 
	 * @param obj
	 * @param attName
	 * @return Object
	 * @exception
	 * @since 1.0.0
	 */
	public static Object getFieldValueFromObject(Object obj, String attName) {
		if (obj == null)
			return null;
		Object val = null;
		try {
			Method getMethod = obj.getClass().getDeclaredMethod("get" + StringUtil.makeFirstLetterUpperCase(attName), new Class[] {});
			if(getMethod==null) return null;
			val = getMethod.invoke(obj, new Object[] {});
		} catch (Exception e) {
			//logger.warn(e.getMessage());
		}
		return val;
	}

	/**
	 * 设置字段值
	 * 
	 * @param instObj
	 * @param attName
	 * @param val
	 *            void
	 * @exception
	 * @since 1.0.0
	 */
	public static void setFieldValue(Object instObj, String attName, Object val) {
		if (instObj == null) return;

		Class<?> cls = instObj.getClass();
		Field field = null;
		Method setMethod = null;
		for (; cls != Object.class; cls = cls.getSuperclass()) {
			try {
				setMethod= getSetMethod(cls,attName);
				if(setMethod!=null){
					setMethod.invoke(instObj, val);
					break;
				}
				
				field = cls.getDeclaredField(attName);
				String attr=StringUtil.makeFirstLetterUpperCase(attName);
				if (field != null) {
					setMethod = cls.getDeclaredMethod("set" + attr, new Class[] { field.getType() });
					setMethod.invoke(instObj, val);
					break;
				}
				
			} catch (Exception e) {
				//logger.error(e.getMessage());
			}
		}
	}
	
	private static Method getSetMethod(Class cls, String attName){
		String attr=StringUtil.makeFirstLetterUpperCase(attName);
		Method[] aryMethod= cls.getDeclaredMethods();
		for(Method m : aryMethod){
			if( m.getName().equals("set" + attr)){
				return m;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		com.alibaba.druid.pool.DruidDataSource ds=new  com.alibaba.druid.pool.DruidDataSource();
		//ds.setUsername("root");
		BeanUtil.setFieldValue(ds, "username", "root");
		
		System.err.println("ok");
	}
	
	
	
	/**
	 * 可以用于判断 Map,Collection,String,Array,Long是否为空
	 * 
	 * @param o
	 *            java.lang.Object.
	 * @return boolean.
	 */
	@SuppressWarnings("unused")
	public static boolean isEmpty(Object o) {
		if (o == null)
			return true;
		if (o instanceof String) {
			if (((String) o).trim().length() == 0)
				return true;
		} else if (o instanceof Collection) {
			if (((Collection<?>) o).size() == 0)
				return true;
		} else if (o.getClass().isArray()) {
			if (((Object[]) o).length == 0)
				return true;
		} else if (o instanceof Map) {
			if (((Map<?, ?>) o).size() == 0)
				return true;
		}
		return false;

	}

	/**
	 * 可以用于判断 Map,Collection,String,Array是否不为空
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}
	
	
	/**
	 * 判断对象是否为数字
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isNumber(Object o) {
		if (o == null)
			return false;
		if (o instanceof Number)
			return true;
		if (o instanceof String) {
			try {
				Double.parseDouble((String) o);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 根据指定的类名判定指定的类是否存在。
	 * 
	 * @param className
	 * @return
	 */
	public static boolean validClass(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * 判定类是否继承自父类
	 * 
	 * @param cls
	 *            子类
	 * @param parentClass
	 *            父类
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean isInherit(Class cls, Class parentClass) {
		return parentClass.isAssignableFrom(cls);
	}
	
	/**
	 * 将数据进行转型。
	 * @param typeName 
	 * 可能的值为：
	 * int,
	 * short,
	 * long,
	 * float,
	 * double,
	 * boolean,
	 * String
	 * @param value
	 * @return
	 */
	public static Object convertByActType(String typeName,String value){
		Object o = null;
		if (typeName.equals("int")) {
			o = Integer.parseInt(value);
		} else if (typeName.equals("short")) {
			o = Short.parseShort(value);
		} else if (typeName.equals("long")) {
			o = Long.parseLong(value);
		} else if (typeName.equals("float")) {
			o = Float.parseFloat(value);
		} else if (typeName.equals("double")) {
			o = Double.parseDouble(value);
		} else if (typeName.equals("boolean")) {
			o = Boolean.parseBoolean(value);
		} else if (typeName.equals("String")) {
			o = value;
		}
		else{
			o=value;
		}
		return o;
	}
	
	
	/**
	 * @描述 list数据转Tree，大多使用在前台json中。
	 * @说明 实现接口 Tree即可 
	 * @扩展 可通过反射获取id,pid，目前只提供Tree接口排序的实现
	 */
	public static <T> List<T> listToTree(List<T> list){
		Map<String, Tree> tempMap = new LinkedHashMap<String, Tree>();
		if(BeanUtil.isEmpty(list) ) return Collections.emptyList();
		if(!(list.get(0) instanceof Tree)) {
			throw new RuntimeException("树形转换出现异常。数据必须实现Tree接口！");
		}
			
		List<T> returnList = new ArrayList<T>();
		for(Tree tree : (List<Tree>)list){
			tempMap.put(tree.getId(),tree);
		}
		
		for(Tree obj : (List<Tree>)list){
			String parentId = obj.getParentId();
			if(tempMap.containsKey(parentId) && !obj.getId().equals(parentId)){
				if(tempMap.get(parentId).getChildren()==null){
					tempMap.get(parentId).setChildren(new ArrayList());
				}
				tempMap.get(parentId).getChildren().add(obj);
			}else{
				returnList.add((T) obj);
			}
		}
		
		return returnList;
	}

	
	/**
	 * 获取类的方法。
	 * @param cls			类
	 * @param methodName	方法名
	 * @param parameters	参数数组
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getMethod(Class<?> cls, String methodName, Object[] parameters) throws NoSuchMethodException {
		Method[] methods =cls.getDeclaredMethods();
		for(Method method:methods){
			Class<?>[] parameterTypes = method.getParameterTypes();
			int len=parameters==null?0:parameters.length;
			if(methodName.equals(method.getName()) && parameterTypes.length==len){
				return method;
			}
		}
		throw new NoSuchMethodException();
	}
}