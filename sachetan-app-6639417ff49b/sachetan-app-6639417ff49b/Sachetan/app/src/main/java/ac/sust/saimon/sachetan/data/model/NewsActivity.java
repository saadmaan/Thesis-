package ac.sust.saimon.sachetan.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;

public class NewsActivity {

    private String id;
    private double lat;
    private double lon;
    private int weight;

    public NewsActivity(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @JsonIgnore
    public WeightedLatLng getAsWeightedLatLng(){
        return new WeightedLatLng(
                new LatLng(lat, lon),
                weight);
    }
}
