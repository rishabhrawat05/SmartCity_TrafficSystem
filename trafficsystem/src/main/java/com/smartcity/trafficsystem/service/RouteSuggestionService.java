package com.smartcity.trafficsystem.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.smartcity.trafficsystem.dto.RouteDto;
import com.smartcity.trafficsystem.model.RouteLeg;
import com.smartcity.trafficsystem.model.RouteStep;
import com.smartcity.trafficsystem.model.RouteSuggestion;
import com.smartcity.trafficsystem.repository.RouteSuggestionRepository;

@Service
public class RouteSuggestionService {

	private static final Logger logger = LoggerFactory.getLogger(RouteSuggestionService.class);
	private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
	private final RouteSuggestionRepository routeSuggestionRepository;
	private final RestTemplate restTemplate;

	@Value("${geoapify.api-key}")
	private String GEOAPIFY_API_KEY;

	private String url = "https://nominatim.openstreetmap.org/search.php?q={location}&format=jsonv2";

	private String routeApi = "https://api.geoapify.com/v1/routing?waypoints={startLat},{startLon}|{endLat},{endLon}&mode={mode}&apiKey={YOUR_API_KEY}";

	private String trafficApi = "https://api.tomtom.com/traffic/services/4/flowSegmentData/absolute/10/json?key={YOUR_API_KEY}&point={latitude},{longitude}";

	private HttpHeaders headers = new HttpHeaders();
	private HttpEntity<?> requestEntity = new HttpEntity<>(headers);

	@Value("${tomtom.api-key}")
	private String TOMTOM_API_KEY;

	public RouteSuggestionService(RouteSuggestionRepository routeSuggestionRepository, RestTemplate restTemplate) {
		this.routeSuggestionRepository = routeSuggestionRepository;
		this.restTemplate = restTemplate;
	}

	@Cacheable(value = "routeSuggestion", key = "#routeDto.getStartLocation() + '_' + #routeDto.getEndLocation() + '_' + #routeDto.getModeOfTransport()")
	public RouteSuggestion suggestRoute(RouteDto routeDto) {
		CompletableFuture<ResponseEntity<List<Map<String, Object>>>> startFuture = getLatitudeAndLongitude(
				routeDto.getStartLocation());
		CompletableFuture<ResponseEntity<List<Map<String, Object>>>> endFuture = getLatitudeAndLongitude(
				routeDto.getEndLocation());

		CompletableFuture.allOf(startFuture, endFuture).join();

		ResponseEntity<List<Map<String, Object>>> startResponse = startFuture.join();
		ResponseEntity<List<Map<String, Object>>> endResponse = endFuture.join();

		if (startResponse.getBody() == null || startResponse.getBody().isEmpty()) {
			throw new RuntimeException("Failed to get start location coordinates.");
		}

		double startLatitude = Double.parseDouble(startResponse.getBody().get(0).get("lat").toString());
		double startLongitude = Double.parseDouble(startResponse.getBody().get(0).get("lon").toString());

		if (endResponse.getBody() == null || endResponse.getBody().isEmpty()) {
			throw new RuntimeException("Failed to get end location coordinates.");
		}

		double endLatitude = Double.parseDouble(endResponse.getBody().get(0).get("lat").toString());
		double endLongitude = Double.parseDouble(endResponse.getBody().get(0).get("lon").toString());

		CompletableFuture<ResponseEntity<Map<String, Object>>> routeFuture = getRouteResponse(startLatitude, startLongitude, endLatitude,
				endLongitude, routeDto.getModeOfTransport());
		
		ResponseEntity<Map<String, Object>> routeResponse = routeFuture.join();
		
		if (routeResponse.getBody() == null) {
			throw new RuntimeException("Failed to get route information.");
		}
		String startLocation = startResponse.getBody().get(0).get("display_name").toString();
		String endLocation = endResponse.getBody().get(0).get("display_name").toString();
		Map<String, Object> geoapifyResponse = routeResponse.getBody();
		logger.info("Geoapify response: {}", geoapifyResponse);
		return getRouteSuggestion(geoapifyResponse, startLocation, endLocation, startLatitude, startLongitude,
				endLatitude, endLongitude, routeDto);

	}

	public String calculateEstimatedTime(Map<String, Object> geoapifyResponse) {
		List<Double[]> allCoordinates = extractCoordinates(geoapifyResponse);
		List<CompletableFuture<Double>> segmentTravelTimeFutures = new ArrayList<>();

		for (int i = 0; i < allCoordinates.size() - 1; i++) {
			Double[] coord1 = allCoordinates.get(i);
			Double[] coord2 = allCoordinates.get(i + 1);
			CompletableFuture<Double> segmentDistanceFuture = CompletableFuture
					.supplyAsync(() -> calculateHaversineDistance(coord1[0], coord1[1], coord2[0], coord2[1]), executorService);

			CompletableFuture<ResponseEntity<Map<String, Object>>> trafficFuture = getTrafficResponse(coord1[0],
					coord1[1]);

			CompletableFuture<Double> segmentTravelTimeFuture = segmentDistanceFuture.thenCombine(trafficFuture,
					(distance, trafficResponse) -> {
						double currentSpeed = 50.0;
						if (trafficResponse.getBody() != null
								&& trafficResponse.getBody().containsKey("flowSegmentData")) {
							Map<String, Object> flowSegmentData = (Map<String, Object>) trafficResponse.getBody()
									.get("flowSegmentData");

							if (flowSegmentData.containsKey("currentSpeed")) {
								currentSpeed = ((Number) flowSegmentData.get("currentSpeed")).doubleValue();
							}
						}
						double segmentTravelTime = distance / currentSpeed * 3600;
						logger.info(String.format("Segment Distance: %.2f km, Current Speed: %.2f km/h, Segment Travel Time: %.2f sec",
		                        distance, currentSpeed, segmentTravelTime));
						return segmentTravelTime;
						});
			segmentTravelTimeFutures.add(segmentTravelTimeFuture);
			

	}
	Double totalTravelTime = Math.ceil(segmentTravelTimeFutures.stream().map(CompletableFuture::join).reduce(0.0, Double::sum));

	logger.info("Final Estimated Time: "+totalTravelTime);
	return totalTravelTime.toString();
	
	}

	public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
		final int R = 6371;
		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c;

		return distance;
	}

	@Cacheable(value = "locationCache", key = "#location")
	public CompletableFuture<ResponseEntity<List<Map<String, Object>>>> getLatitudeAndLongitude(String location) {
		Map<String, String> params = new HashMap<>();
		params.put("location", location);
		return CompletableFuture.supplyAsync(() -> restTemplate.exchange(url, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				}, params), executorService);

	}

	public CompletableFuture<ResponseEntity<Map<String, Object>>> getRouteResponse(double startLatitude, double startLongitude,
			double endLatitude, double endLongitude, String modeOfTransport) {
		Map<String, String> routeParams = new HashMap<>();
		routeParams.put("startLat", String.valueOf(startLatitude));
		routeParams.put("startLon", String.valueOf(startLongitude));
		routeParams.put("endLat", String.valueOf(endLatitude));
		routeParams.put("endLon", String.valueOf(endLongitude));
		routeParams.put("mode", modeOfTransport);
		routeParams.put("YOUR_API_KEY", GEOAPIFY_API_KEY);
		return CompletableFuture.supplyAsync(() -> restTemplate.exchange(routeApi, HttpMethod.GET,
				requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {
				}, routeParams), executorService);
		
	}

	public RouteSuggestion getRouteSuggestion(Map<String, Object> geoapifyResponse, String startLocation,
			String endLocation, double startLatitude, double startLongitude, double endLatitude, double endLongitude,
			RouteDto routeDto) {
		if (geoapifyResponse.containsKey("features")) {
			List<Map<String, Object>> features = (List<Map<String, Object>>) geoapifyResponse.get("features");
			if (!features.isEmpty() && features.get(0).containsKey("properties")) {
				Map<String, Object> properties = (Map<String, Object>) features.get(0).get("properties");
				if (properties.containsKey("legs")) {
					List<Map<String, Object>> legMaps = (List<Map<String, Object>>) properties.get("legs");
					if (legMaps != null) {
						List<RouteLeg> legs = legMaps.parallelStream().map(legMap -> {
							RouteLeg leg = new RouteLeg();
							leg.setDistance(Double.valueOf(legMap.get("distance").toString()));
							leg.setTime(Double.valueOf(legMap.get("time").toString()));

							List<Map<String, Object>> stepMaps = (List<Map<String, Object>>) legMap.get("steps");
							logger.info("Leg steps: {}", stepMaps);
							List<RouteStep> steps = stepMaps.parallelStream().map(stepMap -> {
								RouteStep step = new RouteStep();
								step.setFromIndex(
										stepMap.get("from_index") != null ? (Integer) stepMap.get("from_index") : 0);
								step.setToIndex(
										stepMap.get("to_index") != null ? (Integer) stepMap.get("to_index") : 0);
								step.setDistance(stepMap.get("distance") != null
										? Double.valueOf(stepMap.get("distance").toString())
										: 0.0);
								step.setTime(
										stepMap.get("time") != null ? Double.valueOf(stepMap.get("time").toString())
												: 0.0);
								step.setInstruction(stepMap.get("instruction") != null
										? ((Map<String, String>) stepMap.get("instruction")).get("text")
										: "No instruction available");
								return step;
							}).collect(Collectors.toList());

							leg.setSteps(steps);
							return leg;
						}).collect(Collectors.toList());

						RouteSuggestion routeSuggestion = new RouteSuggestion();
						routeSuggestion.setStartLocation(startLocation);
						routeSuggestion.setEndLocation(endLocation);
						routeSuggestion.setDistance(Double.valueOf(properties.get("distance").toString()));
						routeSuggestion.setEstimatedTime(calculateEstimatedTime(geoapifyResponse));
						routeSuggestion.setLegs(legs);
						routeSuggestion.setStartLatitude(startLatitude);
						routeSuggestion.setStartLongitude(startLongitude);
						routeSuggestion.setEndLatitude(endLatitude);
						routeSuggestion.setEndLongitude(endLongitude);

						logger.info(
								"Route Found from " + routeDto.getStartLocation() + " to " + routeDto.getEndLocation());
						routeSuggestionRepository.save(routeSuggestion);
						return routeSuggestion;
					}
				}
			}
		}

	throw new RuntimeException("No legs information available in the Geoapify response.");

	}

	public List<Double[]> extractCoordinates(Map<String, Object> geoapifyResponse) {
		List<Double[]> allCoordinates = new ArrayList<>();
		if (geoapifyResponse.containsKey("features")) {
			List<Map<String, Object>> features = (List<Map<String, Object>>) geoapifyResponse.get("features");
			if (!features.isEmpty() && features.get(0).containsKey("geometry")) {
				Map<String, Object> geometry = (Map<String, Object>) features.get(0).get("geometry");
				if (geometry.containsKey("coordinates")) {
					List<List<List<Double>>> coordinateList = (List<List<List<Double>>>) geometry.get("coordinates");
					if (coordinateList != null) {
						for (List<List<Double>> line : coordinateList) {
							for (List<Double> point : line) {
								if (point.size() >= 2) {
									Double longitude = point.get(0);
									Double latitude = point.get(1);
									allCoordinates.add(new Double[] { latitude, longitude });
								}
							}
						}
					}
				}
			}
		}
		return allCoordinates;
	}

	public CompletableFuture<ResponseEntity<Map<String, Object>>> getTrafficResponse(Double lat, Double lon) {
		Map<String, String> trafficParams = new HashMap<>();
		trafficParams.put("YOUR_API_KEY", TOMTOM_API_KEY);
		trafficParams.put("latitude", lat.toString());
		trafficParams.put("longitude", lon.toString());
		logger.info("Making request to TomTom API with coordinates: " + lat + ", " + lon);
		return CompletableFuture.supplyAsync(() -> restTemplate.exchange(trafficApi, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<Map<String, Object>>() {
				}, trafficParams), executorService);
	}

}
