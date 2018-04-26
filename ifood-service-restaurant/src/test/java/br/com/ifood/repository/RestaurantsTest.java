package br.com.ifood.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.ifood.application.Application;
import br.com.ifood.domain.Restaurant;
import br.com.ifood.domain.Unavailability;

/**
 * store and delete schedules of unavailabilities test
 * 
 * @author paulo
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
@DirtiesContext
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@ActiveProfiles("test")
public class RestaurantsTest {

	@Autowired
	private Restaurants restaurants;
	
	@Before
	public void setUp() throws Exception {
		restaurants.deleteAll();
	}

	@Test
	public void testShoudPersistOneRestaurantWithUnavailability() throws Exception {
		Restaurant restaurant = new Restaurant("Lapero");
		Unavailability unavailability = new Unavailability(LocalDateTime.now(), 60L, Unavailability.Reason.HOLIDAYS);
		restaurant.addScheduled(unavailability );
		restaurants.saveAndFlush(restaurant);

		Optional<Restaurant> restaurantFound = restaurants.findById(1L);
		assertThat(restaurantFound.get().getName(), equalTo("Lapero"));
		assertThat(restaurant.getUnavailabilitySchedule().size(), is(1));
	}

}
