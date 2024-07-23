package com.redxun.core.jms.producer;

import javax.annotation.Resource;

import org.springframework.kafka.core.KafkaTemplate;

import com.redxun.core.jms.IMessageProducer;

public class KafkaProducer implements IMessageProducer{

	@Resource
    private KafkaTemplate kafkaTemplate;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void send(String topic, Object model) {
		kafkaTemplate.send(topic, model);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void send(Object model) {
		kafkaTemplate.send("defaultQueue", model);
	}

}
