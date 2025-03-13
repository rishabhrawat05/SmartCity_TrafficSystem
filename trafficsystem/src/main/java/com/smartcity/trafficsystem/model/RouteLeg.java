package com.smartcity.trafficsystem.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "route_leg")
public class RouteLeg {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "distance")
	private double distance;

	@Column(name = "time")
	private double time;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "steps")
	private List<RouteStep> steps;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public List<RouteStep> getSteps() {
		return steps;
	}

	public void setSteps(List<RouteStep> steps) {
		this.steps = steps;
	}
}
