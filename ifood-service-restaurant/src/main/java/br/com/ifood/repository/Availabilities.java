package br.com.ifood.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifood.domain.Unavailability;

@Repository
public interface Availabilities extends JpaRepository<Unavailability, Long> {

	@Modifying
	@Query("delete from Unavailability u where u.restaurant.id = ?1 and start =?2")
	@Transactional
	  void deleteByIdRestaurantAndStartDate(long restaurantId, LocalDateTime start);
}
