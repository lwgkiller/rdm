package com.redxun.core.exception;

/**
 * License异常。
 * @author ray
 */
public class LicenseException extends RuntimeException {

	private String message="";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4676406508892342771L;
	
	
	public LicenseException(){}
	
	public LicenseException(String msg){
		super(msg);
		this.message=msg;
	}

	

	public String getMessage() {
		return message;
	}
	
	
	

}
