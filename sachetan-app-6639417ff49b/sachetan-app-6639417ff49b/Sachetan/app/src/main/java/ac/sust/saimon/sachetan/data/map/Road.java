package ac.sust.saimon.sachetan.data.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Provided by Nebir.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Road {
    Point end_location,start_location;

    public Point getEnd_location() {
        return end_location;
    }

    public Point getStart_location() {
        return start_location;
    }

    public void setEnd_location(Point end_location) {
        this.end_location = end_location;
    }

    public void setStart_location(Point start_location) {
        this.start_location = start_location;
    }
}