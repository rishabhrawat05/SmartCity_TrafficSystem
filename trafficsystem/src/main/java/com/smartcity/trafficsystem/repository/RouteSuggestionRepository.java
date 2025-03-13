package com.smartcity.trafficsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartcity.trafficsystem.model.RouteSuggestion;

@Repository
public interface RouteSuggestionRepository extends JpaRepository<RouteSuggestion, Long> {

	Optional<RouteSuggestion> findByStartLocationAndEndLocation(String startLocation, String endLocation);
}
