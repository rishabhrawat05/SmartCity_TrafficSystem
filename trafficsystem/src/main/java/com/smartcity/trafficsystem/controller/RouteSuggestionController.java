package com.smartcity.trafficsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcity.trafficsystem.dto.RouteDto;
import com.smartcity.trafficsystem.service.RouteSuggestionService;

@RestController
@RequestMapping("/api/route")
public class RouteSuggestionController {

	private RouteSuggestionService routeSuggestionService;

	public RouteSuggestionController(RouteSuggestionService routeSuggestionService) {
		this.routeSuggestionService = routeSuggestionService;
	}

	@PostMapping("/suggest")
	public ResponseEntity<?> suggestRoute(@RequestBody RouteDto routeDto) {
		return ResponseEntity.ok(routeSuggestionService.suggestRoute(routeDto));
	}
}