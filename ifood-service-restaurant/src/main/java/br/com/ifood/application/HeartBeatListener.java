package br.com.ifood.application;

import java.util.Optional;

import javax.transaction.Transactional;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ifood.domain.Restaurant;
import br.com.ifood.repository.Restaurants;

@Component
@Transactional
public class HeartBeatListener implements MqttCallbackExtended{

	private Restaurants restaurants;
	
	private MqttClient client;
	
	private String topic;
	
	@Autowired
	public HeartBeatListener(Restaurants restaurants) {
		this.restaurants = restaurants;
		
		MqttClient client = null;
		try {
			client = new MqttClient("tcp://test.mosquitto.org:1883", "ifood-service-restaurant");
		} catch (MqttException e) {
			e.printStackTrace();
		}
		MqttConnectOptions options = new MqttConnectOptions();
		options.setKeepAliveInterval(120);
		String topic = "ifood/heartbeat";
		
		client.setCallback(this );
		System.out.println("Client connected.");
		try {
			client.connect(options);
			client.subscribe(topic);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public HeartBeatListener() {
		
	}

	public void setClient(MqttClient client) {
		this.client = client;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	

	public void connectionLost(Throwable throwable) {
		System.out.println("Connection to MQTT broker lost!");
		System.err.println(throwable);
	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {
		//Not necessary
	}

	public void messageArrived(String arg0, MqttMessage mqttMessage) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(mqttMessage.toString());
		Long restaurantID = root.findValue("restaurantId").asLong();
		String restaurantStatus = root.findValue("status").asText().toUpperCase();
		
		Optional<Restaurant> restaurant = restaurants.findById(restaurantID);
		if (!restaurant.isPresent()) {
			throw new RestaurantNotFoundException(restaurantID);
		}
		
		restaurant.get().addStatus(Restaurant.Status.valueOf(restaurantStatus));
		restaurants.saveAndFlush(restaurant.get());
	}

	public void connectComplete(boolean reconnect, String serverURI) {
		if(reconnect) {
			try {
				this.client.subscribe(topic, 1);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}

}
