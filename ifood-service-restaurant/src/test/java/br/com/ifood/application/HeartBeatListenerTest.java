package br.com.ifood.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.ifood.domain.Restaurant;
import br.com.ifood.repository.Restaurants;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {Application.class})
@DirtiesContext
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class HeartBeatListenerTest {

	@MockBean
	private Restaurants restaurants;
	@MockBean
	private MqttClient mqttClient;
	@MockBean
	private Configuration configuration;
	private String topic;

	@Before
	public void setUp() throws Exception {
		topic = "ifood/restaurants/1/status";
	}

	@Test
	public void testShouldAssertHeartbeatsWorks() throws Exception {
		given(mqttClient.isConnected()).willReturn(true);
		given(configuration.getTopic()).willReturn(topic);
		
		Restaurant restaurant = new Restaurant();
		given(restaurants.findById(1L)).willReturn(Optional.of(restaurant));

		HeartBeatListener heartBeatListener = new HeartBeatListener(restaurants, mqttClient, configuration);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m");
		MqttMessage message = new MqttMessage();
		message.setPayload(String.format("{\"dateTime\": \"%s\", \"status\": \"%s\"}",
				LocalDateTime.now().minusMinutes(2).format(formatter), "ONLINE").getBytes());

		heartBeatListener.messageArrived(topic, message);
		
		verify(mqttClient, atLeast(2)).setCallback(any(HeartBeatListener.class));
		verify(mqttClient).subscribe(topic, 2);
		verify(restaurants).findById(1L);
		verify(restaurants).saveAndFlush(any(Restaurant.class));
	}

}
