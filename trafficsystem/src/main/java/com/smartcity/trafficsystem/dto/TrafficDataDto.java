package com.smartcity.trafficsystem.dto;

import java.time.LocalDateTime;


public class TrafficDataDto {

	
	private String location;
	
	private LocalDateTime timestamp;

	private int vehicleCount;
	
	private String trafficStatus;


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
