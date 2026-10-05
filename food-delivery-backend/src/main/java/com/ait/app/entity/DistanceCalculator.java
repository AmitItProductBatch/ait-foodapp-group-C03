package com.ait.app.entity;


public final class DistanceCalculator {

	private static final double EARTH_RADIUS_KM = 6371.0;

	private DistanceCalculator() {

	}

	
	public static double haversineKm(GeoLocation from, GeoLocation to) {

		double lat1 = Math.toRadians(from.getLatitude());
		double lat2 = Math.toRadians(to.getLatitude());
		double deltaLat = Math.toRadians(to.getLatitude() - from.getLatitude());
		double deltaLon = Math.toRadians(to.getLongitude() - from.getLongitude());

		double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return EARTH_RADIUS_KM * c;
	}

	public static double round2(double distanceKm) {
		return Math.round(distanceKm * 100.0) / 100.0;
	}
}
