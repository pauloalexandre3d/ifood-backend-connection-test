package br.com.ifood.restaurantclient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPingReq;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestaurantClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantClientApplication.class, args);

		try {
			MqttClient client = new MqttClient("tcp://iot.eclipse.org:1883", MqttClient.generateClientId(),	new MemoryPersistence());
			
//			MqttClient client = new MqttClient("tcp://test.mosquitto.org:1883", MqttClient.generateClientId(),	new MemoryPersistence());

			
			MqttConnectOptions options = new MqttConnectOptions();
			options.setWill("ifood/restaurants/restaurant1", "payload".getBytes(), 2, true);
			options.setKeepAliveInterval(30);
			options.setConnectionTimeout(300);
			client.connect(options);
			
			client.setCallback(new MqttCallback() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					System.out.println(message); 					
				}
				
				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					
				}
				
				@Override
				public void connectionLost(Throwable cause) {
					System.out.println(cause); 		
				}
			});

			if (client.isConnected()) {
				System.out.println("RestaurantClient conected.");
			}
			
			IMqttMessageListener messageListener = new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					System.out.println(message); 					
				}
			};
			client.subscribe("ifood/restaurants", messageListener );
			MqttMessage message = new MqttMessage();
			message.setPayload("{\"restaurantId\":1, \"status\":\"online\"}".getBytes());
			client.publish("ifood/restaurants/restaurant1", message);

		} catch (MqttException e) {
			e.printStackTrace();
		}

	}
}
