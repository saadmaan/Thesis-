package com.sachetan.util;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

public class MapPoint {
	private double lat;
	private double lon;

	MapPoint() {
	}

	public MapPoint(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public double getLat() {
		return lat;
	}
}
