package br.com.ifood.application;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifood.domain.Restaurant;
import br.com.ifood.domain.Unavailability;
import br.com.ifood.repository.Availabilities;
import br.com.ifood.repository.Restaurants;

@RestController
public class UnavailabilityController {

	private Restaurants restaurantsRepository;

	private Availabilities availabilities;

	@Autowired
	public UnavailabilityController(Restaurants restaurants, Availabilities availabilities) {
		this.restaurantsRepository = restaurants;
		this.availabilities = availabilities;
	}

	@PostMapping("/restaurants/{restaurantId}/unavailability")
	private ResponseEntity<?> addScheduleUnavailability(@RequestBody Unavailability unavailability,
			@PathVariable Long restaurantId) {

		Optional<Restaurant> restaurant = restaurantsRepository.findById(restaurantId);

		if (!restaurant.isPresent())
			return ResponseEntity.notFound().build();

		restaurant.get().addScheduled(unavailability);
		restaurantsRepository.saveAndFlush(restaurant.get());

		return ResponseEntity.ok().build();
	}

	@GetMapping("/restaurants/{restaurantId}/unavailability")
	Collection<Unavailability> readScheduleUnavailability(@PathVariable Long restaurantId) {
		Optional<Restaurant> restaurant = this.restaurantsRepository.findById(restaurantId);

		if (!restaurant.isPresent())
			throw new RestaurantNotFoundException(restaurantId);

		return restaurant.get().getUnavailabilitySchedule();
	}

	@DeleteMapping("/restaurants/{restaurantId}/unavailability")
	public ResponseEntity<Object> deleteScheduledUnavailability(@RequestBody Unavailability unavailability,
			@PathVariable long restaurantId) {
		Optional<Restaurant> restaurant = restaurantsRepository.findById(restaurantId);

		if (!restaurant.isPresent())
			throw new RestaurantNotFoundException(restaurantId);

		availabilities.deleteByIdRestaurantAndStartDate(restaurantId, unavailability.getStart());

		return ResponseEntity.accepted().build();

	}

}
