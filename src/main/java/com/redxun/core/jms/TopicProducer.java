package com.redxun.core.jms;

import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.core.JmsTemplate;

public class TopicProducer {
	@Resource(name = "topicJmsTemplate")
    private JmsTemplate jmsTemplate;
	private Map<String,Destination> topicMap=new java.util.concurrent.ConcurrentHashMap<>();
    /**
     * 说明:发送的时候如果这里没有显示的指定destination.将用spring xml中配置的destination
     * @param destination
     * @param message
     */
    public void sendMqMessage(String topicName, Object model){
    	Destination dest=null;
    	if(!topicMap.containsKey(topicName)){
    		dest=new ActiveMQTopic(topicName);
    		topicMap.put(topicName, dest);
    	}
    	else{
    		dest=topicMap.get(topicName);
    	}
        jmsTemplate.convertAndSend(dest, model);
    }


    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
