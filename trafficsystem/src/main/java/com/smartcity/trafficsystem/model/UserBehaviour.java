package com.smartcity.trafficsystem.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_behaviour")
public class UserBehaviour {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_behaviour_id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
    private User userId;
    
    @Column(name = "behaviour_pattern")
    private String behaviorPattern;
    
    @Column(name = "analysis_time")
    private LocalDateTime analysisTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public String getBehaviorPattern() {
		return behaviorPattern;
	}

	public void setBehaviorPattern(String behaviorPattern) {
		this.behaviorPattern = behaviorPattern;
	}

	public LocalDateTime getAnalysisTime() {
		return analysisTime;
	}

	public void setAnalysisTime(LocalDateTime analysisTime) {
		this.analysisTime = analysisTime;
	}
    
    
}
