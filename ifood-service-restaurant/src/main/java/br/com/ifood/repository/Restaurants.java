package br.com.ifood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ifood.domain.Restaurant;

@Repository
public interface Restaurants extends JpaRepository<Restaurant, Long> {

}
