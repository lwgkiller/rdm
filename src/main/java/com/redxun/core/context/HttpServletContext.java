package com.redxun.core.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http上下文对象。
 * @author ray
 *
 */
public class HttpServletContext {
	
	private static ThreadLocal<HttpServletRequest> requestLocal=new ThreadLocal<HttpServletRequest>();
	
	private static ThreadLocal<HttpServletResponse> responseLocal=new ThreadLocal<HttpServletResponse>();
	
	public static void setContext(HttpServletRequest req,HttpServletResponse response){
		requestLocal.set(req);
		responseLocal.set(response);
	}
	
	/**
	 * 清除http上下文。
	 */
	public static void cleanContext(){
		requestLocal.remove();
		responseLocal.remove();
	}
	
	/**
	 * 获取HTTP请求对象。
	 * @return
	 */
	public static HttpServletRequest getRequest(){
		return requestLocal.get();
	}
	
	/**
	 * 获取HTTP响应对象。
	 * @return
	 */
	public static HttpServletResponse getResponse(){
		return responseLocal.get();
	}
}
