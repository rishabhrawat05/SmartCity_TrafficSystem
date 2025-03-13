package com.smartcity.trafficsystem.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smartcity.trafficsystem.dto.TrafficDataDto;
import com.smartcity.trafficsystem.service.TrafficDataService;

@RestController
@RequestMapping("/api/traffic")
public class TrafficDataController {

	private  TrafficDataService trafficDataService;
	
	public TrafficDataController(TrafficDataService trafficDataService) {
		this.trafficDataService = trafficDataService;
	}
	
	@PostMapping("/data")
	public ResponseEntity<?> saveTrafficData(@RequestBody TrafficDataDto trafficDataDto){
		return ResponseEntity.ok(trafficDataService.saveTrafficData(trafficDataDto));
	}
	
	@GetMapping("/data/{location}")
	public ResponseEntity<?> getTrafficData(@PathVariable String location, @RequestParam (defaultValue = "0") int page, @RequestParam (defaultValue = "5") int size){
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(trafficDataService.getTrafficData(location, pageable));
	}
	@GetMapping("/data/clearCache/{location}")
	public ResponseEntity<?> clearTrafficCache(@PathVariable String location){
		return ResponseEntity.ok(trafficDataService.clearTrafficCache(location));
	}
}
