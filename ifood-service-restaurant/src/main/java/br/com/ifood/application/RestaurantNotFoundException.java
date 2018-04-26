package br.com.ifood.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RestaurantNotFoundException extends RuntimeException {

	public RestaurantNotFoundException(Long restaurantId) {
		super(String.format("Restaurant ID: %d not foud", restaurantId));
	}

	private static final long serialVersionUID = -5564277332737987077L;

}
