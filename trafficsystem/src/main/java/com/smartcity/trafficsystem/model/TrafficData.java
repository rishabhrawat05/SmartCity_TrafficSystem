package com.smartcity.trafficsystem.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "traffic_data")
public class TrafficData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "traffic_data_id")
	private Long id;

	@Column(name = "location")
	private String location;
	
	@Column(name = "timestamp")
	private LocalDateTime timestamp;
	
	@Column(name = "vehicle_count")
	private int vehicleCount;
	
	@Column(name = "traffic_status")
	private String trafficStatus;
	
	@OneToOne(mappedBy = "trafficData")
	private TrafficPrediction trafficPrediction;
	
	@OneToMany(mappedBy = "trafficData")
	private List<RouteSuggestion> routeSuggestions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public int getVehicleCount() {
		return vehicleCount;
	}

	public void setVehicleCount(int vehicleCount) {
		this.vehicleCount = vehicleCount;
	}

	public String getTrafficStatus() {
		return trafficStatus;
	}

	public void setTrafficStatus(String trafficStatus) {
		this.trafficStatus = trafficStatus;
	}
	
	
	
}
