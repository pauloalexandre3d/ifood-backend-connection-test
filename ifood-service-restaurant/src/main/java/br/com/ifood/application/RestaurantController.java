package br.com.ifood.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifood.domain.Restaurant;
import br.com.ifood.repository.Restaurants;

@RestController
public class RestaurantController {

	private Restaurants restaurantsRepository;
	
	@Autowired
	public RestaurantController(Restaurants restaurants) {
		this.restaurantsRepository = restaurants;
	}

	@GetMapping("/restaurants/")
	public List<Restaurant> listAllRestaurants() {
		return restaurantsRepository.findAll();
	}
	
	@GetMapping("/restaurants/status")
	public Map<String, String> listRestaurantsStatus(@RequestParam(value="restaurantIds")  List<String>  restaurants) {
		
		Map<String, String> restaurantsStatus = new HashMap<>();
		
		for (String restaurantId : restaurants) {
			Optional<Restaurant> restaurant = restaurantsRepository.findById(Long.valueOf(restaurantId));
			if (restaurant.isPresent()) {
				restaurantsStatus.put(restaurantId, restaurant.get().getStatus().name());
			}else {
				restaurantsStatus.put(restaurantId, "NOT FOUND");
			}
		}
		
		return restaurantsStatus;
	}


}
