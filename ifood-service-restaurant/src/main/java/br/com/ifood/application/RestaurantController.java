package br.com.ifood.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

}
