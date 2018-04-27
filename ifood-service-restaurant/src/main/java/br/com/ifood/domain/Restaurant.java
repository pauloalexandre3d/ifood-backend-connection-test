package br.com.ifood.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "RESTAURANT")
public class Restaurant {

	public enum Status {
		ONLINE("online"),
		OFFLINE("ofline");
		
		String value;

		Status(String value) {
			this.value = value;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RESTAURANT_ID")
	private Long id;

	@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Unavailability> unavailabilitySchedule;
	private String name;
	@Transient
	private LocalDateTime now;

	@OneToMany(fetch=FetchType.EAGER,  mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StatusHistory> statusHistory  = new ArrayList<StatusHistory>();

	@Transient
	private long keepAliveInterval = 120;

	public Restaurant(LocalDateTime now) {
		this.now = now;
		this.unavailabilitySchedule = new ArrayList<>();
	}

	public Restaurant() {
		
	}

	public Restaurant(String name) {
		this.name = name;
		this.unavailabilitySchedule = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Unavailability> getUnavailabilitySchedule() {
		return unavailabilitySchedule;
	}

	public void addScheduled(Unavailability unavailability) {
		unavailability.setRestaurant(this);
		this.unavailabilitySchedule.add(unavailability);

	}

	public boolean isAvailable() {
		if (this.now == null) {
			this.now = LocalDateTime.now();
		}

		if (this.unavailabilitySchedule.isEmpty()) {
			return true;
		} else {
			Predicate<? super Unavailability> isUnavailable = u -> (this.now.isAfter(u.getStart())
					&& this.now.isBefore(u.getEnd()));
			Optional<Unavailability> findAny = unavailabilitySchedule.stream().filter(isUnavailable).findAny();
			return !findAny.isPresent();
		}
	}

	public void addStatus(Status status, LocalDateTime messageTime) {
		StatusHistory statusNow = new StatusHistory(messageTime, status);
		statusNow.setRestaurant(this);
		this.statusHistory.size();
		this.statusHistory.add(statusNow);
	}

	public Status getStatus() {
		if (this.statusHistory.isEmpty()) {
			return Status.OFFLINE;
		}
		
		long seconds = Duration.between(this.statusHistory.get(this.statusHistory.size() - 1).getWhen(), LocalDateTime.now()).getSeconds();
		
		if (seconds > this.keepAliveInterval && !isAvailable()) {
			return Status.OFFLINE;
		}
		return Status.ONLINE;
	}

	public void setKeepAliveInterval(long seconds) {
		this.keepAliveInterval = seconds;
	}

}
