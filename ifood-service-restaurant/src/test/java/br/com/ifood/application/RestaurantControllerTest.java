package br.com.ifood.application;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.com.ifood.domain.Restaurant;
import br.com.ifood.repository.Restaurants;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class RestaurantControllerTest {

	@Autowired
	private Restaurants restaurants;

	@LocalServerPort
	private int port;

	final String uri = "/restaurants";
	
	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	public RestaurantControllerTest() {
	}

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build();
		
		restaurants.deleteAll();

		Restaurant restaurant = new Restaurant("Tanuki");
		restaurants.save(restaurant);
		Restaurant restaurant2 = new Restaurant("Lapero");
		restaurants.save(restaurant2);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m");

		String message1 = String.format("{\"dateTime\": \"%s\", \"status\": \"%s\"}",
				LocalDateTime.now().minusMinutes(2).format(formatter), "ONLINE");
		String message2 = String.format("{\"dateTime\": \"%s\", \"status\": \"%s\"}",
				LocalDateTime.now().minusMinutes(2).format(formatter), "OFFLINE");
		publishHeatBeat(message1, message2);
	}

	@Test
	public void testShouldAssertListAllRestaurants() throws Exception {
		//@formatter:off
		this.mockMvc.perform(get(uri))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("\"name\" : \"Tanuki\"")));
		//@formatter:on
	}

	@Test
	public void testShouldAssertListRestaurantsStatus() throws Exception {

		//@formatter:off
		this.mockMvc.perform(get(uri.concat("/status"))
	            .param("restaurantIds", "1")
	            .param("restaurantIds", "2")
				 )
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("1\":\"NOT FOUND\"")));
		//@formatter:on
	}

	private void publishHeatBeat(String message1, String message2) throws MqttException {
		MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
		MqttConnectOptions options = new MqttConnectOptions();
		options.setKeepAliveInterval(10);
		client.connect(options);

		MqttMessage mqttMessage1 = new MqttMessage();
		mqttMessage1.setPayload(message1.getBytes());

		MqttMessage mqttMessage2 = new MqttMessage();
		mqttMessage2.setPayload(message2.getBytes());

		client.publish("ifood/restaurants/1/status", new MqttMessage());
		client.publish("ifood/restaurants/2/status", new MqttMessage());
		client.publish("ifood/restaurants/1/status", mqttMessage1);
		client.publish("ifood/restaurants/2/status", mqttMessage2);
		client.disconnect();
	}

}
