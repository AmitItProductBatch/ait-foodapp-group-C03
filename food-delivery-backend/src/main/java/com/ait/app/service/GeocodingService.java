
package com.ait.app.service;

import java.util.List;

import com.ait.app.entity.GeoLocation;


public interface GeocodingService {

	GeoLocation resolve(String rawAddress);

	GeoLocation resolveBestEffort(List<String> candidateQueries);
}

