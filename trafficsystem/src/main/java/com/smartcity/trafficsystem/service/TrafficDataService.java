package com.smartcity.trafficsystem.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.smartcity.trafficsystem.dto.TrafficDataDto;
import com.smartcity.trafficsystem.model.TrafficData;
import com.smartcity.trafficsystem.repository.TrafficDataRepository;

@Service
public class TrafficDataService {

	private TrafficDataRepository trafficDataRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(TrafficDataService.class);
	
	public TrafficDataService(TrafficDataRepository trafficDataRepository) {
		this.trafficDataRepository = trafficDataRepository;
	}
	
	public String saveTrafficData(TrafficDataDto trafficDataDto) {
		TrafficData newTrafficData = new TrafficData();
		newTrafficData.setLocation(trafficDataDto.getLocation());
		newTrafficData.setTimestamp(LocalDateTime.now());
		newTrafficData.setTrafficStatus(trafficDataDto.getTrafficStatus());
		newTrafficData.setVehicleCount(trafficDataDto.getVehicleCount());
		trafficDataRepository.save(newTrafficData);
		logger.info("Traffic Data Saved Sucessfully");
		return new String("Traffic Data Saved Sucessfully");
		
	}
	
	@Cacheable(value = "trafficCache", key = "#location")
	public Page<TrafficData> getTrafficData(String location, Pageable pageable){
		return trafficDataRepository.findAllByLocation(location, pageable);
	}
	
	@CacheEvict(value = "trafficCache", key = "#location")
	public String clearTrafficCache(String location) {
		logger.info("Traffic Cache with location name " + location + "has been evicted!!");
		return new String("Traffic Cache with location name " + location + "has been evicted!!");
	}
}
