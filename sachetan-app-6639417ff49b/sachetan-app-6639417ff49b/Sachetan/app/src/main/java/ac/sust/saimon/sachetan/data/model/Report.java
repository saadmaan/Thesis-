package ac.sust.saimon.sachetan.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.io.Serializable;
import java.util.Date;


public class Report implements Serializable {

    private String id;
    private IncidentType incidentType;
    private Integer severity;
    private String description;
    private Date incidentDate;
    private Date createdAt;
    private Date updatedAt;
    private Double location[];

    public Report(){
    }

    public Report(IncidentType incidentType, Integer severity, Double location[]){
        setIncidentType(incidentType);
        setSeverity(severity);
        setLocation(location);
    }

    public void setReport(Report report){
        if (report.description != null)
            this.description = report.description;

        if (report.severity != null);
        this.severity = report.severity;

        if (report.location != null){
            this.location = report.location;
        }
    }

    public void setIncidentType(IncidentType incidentType){
        this.incidentType = incidentType;
    }
    public IncidentType getIncidentType() { return this.incidentType; }

    public void setSeverity(Integer severity){ this.severity = severity; }
    public Integer getSeverity(){ return severity; }

    public Date getIncidentDate() {
        return incidentDate;
    }
    public void setIncidentDate(Date incidentDate) {
        this.incidentDate = incidentDate;
    }

    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }
    public Date getCreatedAt(){
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt){
        this.updatedAt = updatedAt;
    }
    public Date getUpdateAt(){
        return updatedAt;
    }

    public void setLocation(Double[] location){
        this.location = location;
    }
    public Double[] getLocation(){
        return location;
    }

    public void setDescription(String description) { this.description = description; }
    public String getDescription(){ return description; }

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return id;
    }

    @Override
    public String toString(){
        return "[" + id + "," + severity + "]" + ": " + incidentType.toString();
    }

    @JsonIgnore
    public WeightedLatLng getAsWeightedLagLng(){
        return new WeightedLatLng(
                new LatLng(location[1], location[0]),
                severity);
    }

    @Override
    public boolean equals(Object other){
        if(other == null || getClass() != other.getClass()){
            return false;
        }
        return this.id.equals(((Report) other).id);
    }


}
