package com.redxun.core.util;

public class KeyPairObject {

	private String privateKey="";
	
	private String publicKey="";
	
	public KeyPairObject(){}
	
	

	public KeyPairObject(String privateKey, String publicKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}



	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	
	
}
