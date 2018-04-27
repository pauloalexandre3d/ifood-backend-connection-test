package br.com.ifood.application;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration {
	
	@Value("${mqtt.host}")
	private String mqttHost;

	@Value("${mqtt.port}")
	private String mqttPort;
	
	@Value("${mqtt.topic.restaurants.status}")
	private String topic;

	public String getMqttHost() {
		return mqttHost;
	}

	public String getMqttPort() {
		return mqttPort;
	}

	public String getTopic() {
		return topic;
	}

}
