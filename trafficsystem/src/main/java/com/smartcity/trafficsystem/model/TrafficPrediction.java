package com.smartcity.trafficsystem.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "traffic_prediction")
public class TrafficPrediction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "traffic_prediction_id")
	private Long id;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "predicted_time")
	private LocalDateTime predictedTime;
	
	@Column(name = "predicted_status")
	private String predictedStatus;
	
	@OneToOne
	@JoinColumn(name = "traffic_data_id")
	private TrafficData trafficData;

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

	public LocalDateTime getPredictedTime() {
		return predictedTime;
	}

	public void setPredictedTime(LocalDateTime predictedTime) {
		this.predictedTime = predictedTime;
	}

	public String getPredictedStatus() {
		return predictedStatus;
	}

	public void setPredictedStatus(String predictedStatus) {
		this.predictedStatus = predictedStatus;
	}
	
	
}
