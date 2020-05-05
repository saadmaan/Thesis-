package ac.sust.saimon.sachetan.data.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Provided by Nebir.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Point {
    private double lat,lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}

