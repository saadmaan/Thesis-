package ac.sust.saimon.sachetan.data.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class IncidentType implements Serializable {

    protected String id;
    protected String name;
    protected String description;

    public IncidentType() {
    }

    public IncidentType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "[" + name + " - " + description + "]";
    }
}
