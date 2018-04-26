package br.com.ifood.application;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.ifood.domain.Restaurant;
import br.com.ifood.repository.Restaurants;
import io.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class RestaurantControllerTest {

	@Autowired
	private Restaurants restaurants;

	@LocalServerPort
	private int port;

	final String uri = "/restaurants";

	@Autowired
	private TestRestTemplate restTemplate;

	public RestaurantControllerTest() {
	}

	@Before
	public void setUp() throws Exception {
		RestAssured.baseURI = uri;

		restaurants.deleteAll();

		Restaurant restaurant = new Restaurant("Tanuki");
		restaurants.save(restaurant);
	}

	@Test
	public void testShouldAssertListAllRestaurants() {
		//@formatter:off
		String body = this.restTemplate.getForObject(uri, String.class);
		assertThat(body, containsString("\"name\" : \"Tanuki\""));
		//@formatter:on
	}

}
