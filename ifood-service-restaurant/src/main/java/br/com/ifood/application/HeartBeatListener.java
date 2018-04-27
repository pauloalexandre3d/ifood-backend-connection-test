package br.com.ifood.application;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.ifood.domain.Restaurant;
import br.com.ifood.repository.Restaurants;

@Service
public class HeartBeatListener implements MqttCallback {

	private Restaurants restaurants;

	private MqttClient mqttClient;

	private Configuration configuration;

	private static final Log LOGGER = LogFactory.getLog(HeartBeatListener.class);

	@Autowired
	public HeartBeatListener(Restaurants restaurants, MqttClient mqttClient, Configuration configuration) {
		this.restaurants = restaurants;
		this.mqttClient = mqttClient;
		this.configuration = configuration;

		try {
			this.mqttClient.setCallback(this);

			if (mqttClient.isConnected()) {
				mqttClient.subscribe(this.configuration.getTopic(), 2);
				LOGGER.debug("MQTT Client connected..");
			}

		} catch (MqttException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		LOGGER.warn("Callback - connectionLost: " + cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {

		if (message.getPayload() == null || message.getPayload().length == 0) {
			return;
		}

		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(topic);
		m.find();
		Long restaurantID = Long.parseLong(m.group());

		Optional<Restaurant> restaurant = restaurants.findById(restaurantID);
		if (!restaurant.isPresent()) {
			throw new RestaurantNotFoundException(restaurantID);
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		JsonNode root = mapper.readTree(message.getPayload());
		String restaurantStatus = root.findValue("status").asText();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m");
		LocalDateTime messageTime = LocalDateTime.parse(root.findValue("dateTime").textValue(), formatter);
		
		LocalTime openingHour = LocalTime.of(10, 00);
		LocalTime closingHour = LocalTime.of(23, 00);
		
		//it must be inside the opening hour
		if (messageTime.toLocalTime().isBefore(openingHour) || messageTime.toLocalTime().isAfter(closingHour)) {
			return;
		}
		
		restaurant.get().addStatus(Restaurant.Status.valueOf(restaurantStatus), messageTime);
		this.restaurants.saveAndFlush(restaurant.get());
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// NOT APPLY

	}

}
