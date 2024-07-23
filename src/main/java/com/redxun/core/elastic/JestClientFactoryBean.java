package com.redxun.core.elastic;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.FactoryBean;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class JestClientFactoryBean implements FactoryBean<JestClient> {
	
	private int curConnection=2;
	private int maxConnection=20;
	/**
	 * 使用逗号分隔。
	 */
	private String connections="";
	
	public void setConnections(String list){
		this.connections=list;
	}
	

	@Override
	public JestClient getObject() throws Exception {
		String[] conns=connections.split(",");
		Collection<String> list=new ArrayList<>();
		for(String con:conns){
			list.add(con);
		}
		HttpClientConfig.Builder builder=	new HttpClientConfig.Builder(list);
		JestClientFactory factory = new JestClientFactory();
		 factory.setHttpClientConfig(builder.multiThreaded(true)
					//Per default this implementation will create no more than 2 concurrent connections per given route
					.defaultMaxTotalConnectionPerRoute(this.curConnection)
					// and no more 20 connections in total
					.maxTotalConnection(this.maxConnection)
		            .build());
		 JestClient client = factory.getObject();
		return client;
	}

	@Override
	public Class<?> getObjectType() {
		return JestClient .class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	public void setCurConnection(int curConnection) {
		this.curConnection = curConnection;
	}


	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}



}
