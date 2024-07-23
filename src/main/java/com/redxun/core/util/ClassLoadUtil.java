package com.redxun.core.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;



public class ClassLoadUtil {
	
	static{
		String arch = System.getProperty("os.arch");
		String os = System.getProperty("os.name").toLowerCase();
		String path=ClassLoadUtil.class.getResource("/").getPath() ;
		path=path.replace("classes/", "dll/");
		
		if(os.contains("win")){
			if(arch.contains("64")){
				// windows 64位
				System.load(path+"ClassLoader64.dll");
			}
			else{
				// windows 32位
				System.load(path+"ClassLoader.dll");
			}
		}
		else if(!os.contains("mac")){// linux
			System.load(path+"ClassLoader.so");
		}
	}

	
	
	private static native Class<?>[] getClass(String[] className, boolean encrypt);
	
	private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
	
	
	private static void getClass(String className) {
		String[] classAry=new String[1];
		String packageName=className.replace(".", "/");
		String classPath= getClassPath() +packageName +","+packageName;
		classAry[0]=classPath;
		Class<?>[] result = getClass(classAry, false);
		if(result==null || result.length==0) return;
		for(Class<?> c:result){
			classMap.put(c.getName(), c);
		}
	}
	
	/**
	 * 通过反射调用类的方法。
	 * @param className		类名 com.redxun.util.HelloWorld
	 * @param methodName	方法名 hello
	 * @param parameters	参数
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static Object execute(String className, String methodName, Object... parameters) throws Exception{
		Class<?> cls = classMap.get(className);
		if(cls==null){
			getClass(className);
			cls = classMap.get(className);
		}
		Method method =BeanUtil.getMethod(cls, methodName, parameters);
		Object result= method.invoke(cls, parameters);
		
		return result;
	}
	
	/**
	 * 执行类的方法。
	 * @param className
	 * @param methodName
	 * @return
	 * @throws Exception 
	 */
	public static Object execute(String className, String methodName) throws Exception{
		return execute( className,  methodName);
	}
	

	/**
	 * 取得类路径。
	 * @return
	 */
	private static String  getClassPath(){
		String classPath = ClassLoadUtil.class.getResource("/").getPath() ;
		if(File.separator.equals("\\")){
			classPath=classPath.substring(1);
		}
		return classPath;
	}
	
	
	

}


