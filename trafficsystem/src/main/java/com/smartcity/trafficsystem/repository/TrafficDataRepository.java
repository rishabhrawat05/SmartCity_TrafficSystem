package com.smartcity.trafficsystem.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartcity.trafficsystem.model.TrafficData;

@Repository
public interface TrafficDataRepository extends JpaRepository<TrafficData, Long> {

	Page<TrafficData> findAllByLocation(String location, Pageable pageable);
}
