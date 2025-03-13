package com.smartcity.trafficsystem.model;

import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "route_suggestion")
public class RouteSuggestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "route_suggestion_id")
	private Long id;

	@Column(name = "start_location")
	private String startLocation;

	@Column(name = "end_location")
	private String endLocation;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "legs")
    private List<RouteLeg> legs;

	@Column(name = "distance")
	private double distance;

	@Column(name = "estimated_time")
	private String estimatedTime;
	
	@Column(name = "start_latitude")
	private double startLatitude;
	
	@Column(name = "start_longitude")
	private double startLongitude;
	
	@Column(name = "end_latitude")
	private double endLatitude;
	
	@Column(name = "end_longitude")
	private double endLongitude;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User users;

	@ManyToOne
	@JoinColumn(name = "traffic_data")
	private TrafficData trafficData;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public String getEndLocation() {
		return endLocation;
	}

	public void setEndLocation(String endLocation) {
		this.endLocation = endLocation;
	}
	
	public List<RouteLeg> getLegs() {
		return legs;
	}

	public void setLegs(List<RouteLeg> legs) {
		this.legs = legs;
	}

	public String getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(String estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(double startLatitude) {
		this.startLatitude = startLatitude;
	}

	public double getStartLongitude() {
		return startLongitude;
	}

	public void setStartLongitude(double startLongitude) {
		this.startLongitude = startLongitude;
	}

	public double getEndLatitude() {
		return endLatitude;
	}

	public void setEndLatitude(double endLatitude) {
		this.endLatitude = endLatitude;
	}

	public double getEndLongitude() {
		return endLongitude;
	}

	public void setEndLongitude(double endLongitude) {
		this.endLongitude = endLongitude;
	}
	
	

}
