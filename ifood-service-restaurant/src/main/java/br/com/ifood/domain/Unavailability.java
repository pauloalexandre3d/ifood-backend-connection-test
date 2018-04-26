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
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "UNAVAILABILITY")
public class Unavailability {

	public enum Reason {
		LACK_OF_DELIVERY_STAFF("lack of delivery staff"), CONNECTION_ISSUES(
				"connection issues"), OVERLOADED_DUE_TO_OFFLINE_ORDERS(
						"overloaded due to offline orders"), HOLIDAYS("holidays");

		String value;

		Reason(String value) {
			this.value = value;
		}

	}
	
	public Unavailability() {};

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "UNAVAILABILITY_ID")
	private Long id;

	private LocalDateTime start;
	private LocalDateTime end;
	private Reason reason;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RESTAURANT_ID", nullable = false)
	private Restaurant restaurant;

	@Transient
	private Long unavailableTime;
	
	void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public Unavailability(LocalDateTime when, Long unavailableTime, Reason reason) {
		this.start = when;
		this.setUnavailableTime(unavailableTime);
		this.end = this.start.plusMinutes(unavailableTime);
		this.reason = reason;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public Reason getReason() {
		return reason;
	}

	public void setUnavailableTime(Long unavailableTime) {
		this.unavailableTime = unavailableTime;
		this.end = this.start.plusMinutes(unavailableTime);
	}

}
