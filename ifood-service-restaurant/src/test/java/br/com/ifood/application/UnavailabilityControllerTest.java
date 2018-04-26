package br.com.ifood.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ifood.domain.Restaurant;
import br.com.ifood.domain.Unavailability;
import br.com.ifood.repository.Restaurants;
import io.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class UnavailabilityControllerTest {

	@Autowired
	private Restaurants restaurants;

	@LocalServerPort
	private int port;

	final String url = "/restaurants";

	@Autowired
	private TestRestTemplate restTemplate;

	private Restaurant restaurant;

	@Before
	public void setUp() throws Exception {
		RestAssured.baseURI = url;

		restaurants.deleteAll();

		restaurant = new Restaurant("Tanuki");
		restaurant.addScheduled(new Unavailability(LocalDateTime.now(), 2L, Unavailability.Reason.HOLIDAYS));
		restaurant = restaurants.save(restaurant);
	}

	@Test
	public void testShoudAssertAScheduleOfUnavailability() {
		Unavailability unavailability = new Unavailability(LocalDateTime.now(), 2L,
				Unavailability.Reason.CONNECTION_ISSUES);
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("restaurantId", restaurant.getId());
		ResponseEntity<Object> response = this.restTemplate.postForEntity(url.concat("/{restaurantId}/unavailability"),
				unavailability, Object.class, urlVariables);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void testShoudAssertAScheduleOfUnavailabilityByRestaurantNonexistent() {
		Unavailability unavailability = new Unavailability(LocalDateTime.now(), 2L,
				Unavailability.Reason.CONNECTION_ISSUES);
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("restaurantId", 99L);
		ResponseEntity<Object> response = this.restTemplate.postForEntity(url.concat("/{restaurantId}/unavailability"),
				unavailability, Object.class, urlVariables);
		assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}
	
	@Test
	public void testShouldAsserReadScheduleUnavailabilityByRestaurant() throws Exception {
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("restaurantId", restaurant.getId());
		
		ResponseEntity<String> response = restTemplate.getForEntity(url.concat("/{restaurantId}/unavailability"), String.class, urlVariables);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response.getBody());
		JsonNode name = root.findValue("reason");
		assertThat(name.asText(), equalTo("HOLIDAYS"));
	}
	
	@Test
	public void testShouldAsserReadScheduleUnavailabilityByRestaurantNonexistent() throws Exception {
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("restaurantId", 99L);
		ResponseEntity<HttpEntity> responseEntity = this.restTemplate.getForEntity(url.concat("/{restaurantId}/unavailability"), HttpEntity.class, urlVariables);
		assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
	}
	
	@Test
	public void testShouldAssertDeleteScheduledUnavailability() throws Exception {
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("restaurantId", restaurant.getId());
		HttpEntity<Unavailability> request = new HttpEntity<>(new Unavailability(LocalDateTime.now(), 2L, Unavailability.Reason.HOLIDAYS));
		ResponseEntity<Unavailability> response = restTemplate.exchange(url.concat("/{restaurantId}/unavailability"), HttpMethod.DELETE, request, Unavailability.class, urlVariables);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
	}
	
	@Test
	public void testShouldAssertDeleteScheduledUnavailabilityByRestaurantNonexistent() throws Exception {
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("restaurantId", 99L);
		HttpEntity<Unavailability> request = new HttpEntity<>(new Unavailability(LocalDateTime.now(), 2L, Unavailability.Reason.HOLIDAYS));
		ResponseEntity<Unavailability> response = restTemplate.exchange(url.concat("/{restaurantId}/unavailability"), HttpMethod.DELETE, request, Unavailability.class, urlVariables);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
	}

}
