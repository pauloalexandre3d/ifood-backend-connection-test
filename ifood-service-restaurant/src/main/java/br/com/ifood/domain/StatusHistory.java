package br.com.ifood.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.ifood.domain.Restaurant.Status;

@Entity
public class StatusHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "STATUSHISTORY_ID")
	private Long id;
	private LocalDateTime when;
	private Status status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RESTAURANT_ID", nullable = false)
	private Restaurant restaurant;

	public StatusHistory() {
	}
	
	public StatusHistory(LocalDateTime when, Status status) {
		this.setWhen(when);
		this.setStatus(status);
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public LocalDateTime getWhen() {
		return when;
	}

	public void setWhen(LocalDateTime when) {
		this.when = when;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	

}
