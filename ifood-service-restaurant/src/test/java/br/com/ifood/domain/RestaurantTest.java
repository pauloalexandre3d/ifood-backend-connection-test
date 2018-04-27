package br.com.ifood.domain;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

public class RestaurantTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testShouldAssertRestaurantAvailableWithoutScheduledUnavailability() {
		LocalDateTime now = LocalDateTime.of(2018, 4, 19, 17, 0);
		Restaurant restaurant = new Restaurant(now);
		assertThat(restaurant.isAvailable(), is(true));
	}
	
	@Test
	public void testShouldAssertRestaurantUnavailabilityWithScheduledUnavailabilityInTheSamePeriodTime() {
		LocalDateTime now = LocalDateTime.of(2018, 4, 18, 17, 30);
		Restaurant restaurant = new Restaurant(now);
		
		LocalDateTime when = LocalDateTime.of(2018, 4, 18, 17, 0);
		Long unavailableTime = 120L;
		restaurant.addScheduled(new Unavailability(when, unavailableTime, Unavailability.Reason.OVERLOADED_DUE_TO_OFFLINE_ORDERS));
		
		assertThat(restaurant.isAvailable(), is(false));
	}
	
	@Test
	public void testShouldAssertRestaurantAvailableWithScheduledUnavailabilityInAnotherTime() {
		LocalDateTime now = LocalDateTime.of(2018, 4, 18, 17, 30);
		Restaurant restaurant = new Restaurant(now);
		
		LocalDateTime when = LocalDateTime.of(2018, 4, 19, 17, 0);
		Long unavailableTime = 120L;
		restaurant.addScheduled(new Unavailability(when, unavailableTime, Unavailability.Reason.CONNECTION_ISSUES));
		
		assertThat(restaurant.isAvailable(), is(true));
	}
	
	@Test
	public void testShouldAssertRestaurantAvailableWithScheduledUnavailabilityWithReason() {
		LocalDateTime now = LocalDateTime.of(2018, 4, 18, 17, 30);
		Restaurant restaurant = new Restaurant(now);
		
		LocalDateTime when = LocalDateTime.of(2018, 4, 19, 17, 0);
		Long unavailableTime = 120L;
		Unavailability unavailability = new Unavailability(when, unavailableTime, Unavailability.Reason.LACK_OF_DELIVERY_STAFF);
		restaurant.addScheduled(unavailability);
		
		assertThat(restaurant.isAvailable(), is(true));
		assertThat(unavailability.getReason(), is(Unavailability.Reason.LACK_OF_DELIVERY_STAFF));
	}
	
	@Test
	public void testShoudAssertRestaurantHasAHistoryStatus() {
		Restaurant restaurant = new Restaurant();
		restaurant.addStatus(Restaurant.Status.ONLINE, LocalDateTime.now());
		assertThat(restaurant.getStatus(), is(Restaurant.Status.ONLINE));
	}
	
	@Test
	public void testShouldAssertIfRestaurantIsOfflineAfterKeepAliveInterval() throws Exception {
		Restaurant restaurant = new Restaurant();
		restaurant.addStatus(Restaurant.Status.ONLINE, LocalDateTime.now());
		restaurant.setKeepAliveInterval(1L);
		Thread.sleep(2000);
		assertThat(restaurant.getStatus(), is(Restaurant.Status.OFFLINE));
	}
	
	@Test
	public void testShouldAssertIfRestaurantIsOnlineKeepAliveInterval() throws Exception {
		Restaurant restaurant = new Restaurant();
		restaurant.addStatus(Restaurant.Status.ONLINE, LocalDateTime.now());
		restaurant.setKeepAliveInterval(4L);
		Thread.sleep(2000);
		assertThat(restaurant.getStatus(), is(Restaurant.Status.ONLINE));
	}

}
