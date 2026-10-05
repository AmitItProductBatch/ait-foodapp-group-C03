
package com.ait.app.serviceimpl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ait.app.entity.GeoLocation;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.service.GeocodingService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Service
public class GeocodingServiceImpl implements GeocodingService {

	private static final Logger log = LoggerFactory.getLogger(GeocodingServiceImpl.class);

	private final RestTemplate restTemplate;

	@Value("${geocoding.nominatim.base-url:https://nominatim.openstreetmap.org/search}")
	private String nominatimBaseUrl;

	@Autowired
	public GeocodingServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public GeoLocation resolve(String rawAddress) {

		if (rawAddress == null || rawAddress.isBlank()) {
			throw new InvalidRequestException("Address could not be resolved: address text is empty");
		}

		URI uri = UriComponentsBuilder.fromHttpUrl(nominatimBaseUrl)
				.queryParam("q", rawAddress)
				.queryParam("format", "json")
				.queryParam("limit", 1)
				.build()
				.encode()
				.toUri();

		HttpHeaders headers = new HttpHeaders();
	headers.set(HttpHeaders.USER_AGENT, "food-delivery-backend/1.0 (delivery-fee-service)");
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		NominatimResult[] results;
		try {
			ResponseEntity<NominatimResult[]> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
					NominatimResult[].class);
			results = response.getBody();
		} catch (RestClientException ex) {
		log.warn("Geocoding call failed for query '{}': {}", rawAddress, ex.getMessage());
			throw new InvalidRequestException("Unable to resolve address: " + rawAddress);
		}

		if (results == null || results.length == 0) {
			log.info("Geocoding returned no results for query '{}'", rawAddress);
			throw new InvalidRequestException("Unable to resolve address: " + rawAddress);
		}

		try {
			double lat = Double.parseDouble(results[0].lat);
			double lon = Double.parseDouble(results[0].lon);
			return new GeoLocation(lat, lon);
		} catch (NumberFormatException ex) {
			throw new InvalidRequestException("Unable to resolve address: " + rawAddress);
		}
	}

	@Override
	public GeoLocation resolveBestEffort(List<String> candidateQueries) {

		List<String> attempted = new ArrayList<>();

		for (String candidate : candidateQueries) {
			if (candidate == null || candidate.isBlank()) {
				continue;
			}
			attempted.add(candidate);
			try {
				GeoLocation result = resolve(candidate);
				log.info("Geocoding resolved on candidate '{}' (attempt {} of {})", candidate, attempted.size(),
						candidateQueries.size());
				return result;
			} catch (InvalidRequestException ex) {
				log.info("Geocoding candidate {} of {} failed: '{}'", attempted.size(), candidateQueries.size(),
						candidate);
			}
		}

		if (attempted.isEmpty()) {
			throw new InvalidRequestException("Unable to resolve address: no usable address details were provided");
		}

		throw new InvalidRequestException("Unable to resolve address after trying " + attempted.size()
				+ " variant(s): " + String.join("  |  ", attempted));
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class NominatimResult {
		public String lat;
		public String lon;
	}
}
