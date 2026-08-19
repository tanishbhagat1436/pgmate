package com.PGMATE.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer amenityId;

    private String amenityName;

    @ManyToMany(mappedBy = "amenities")
    @JsonIgnore 
    private Set<Pg> pgs;


    public Integer getAmenityId() { return amenityId; }
    public void setAmenityId(Integer amenityId) { this.amenityId = amenityId; }
    public String getAmenityName() { return amenityName; }
    public void setAmenityName(String amenityName) { this.amenityName = amenityName; }
    public Set<Pg> getPgs() { return pgs; }
    public void setPgs(Set<Pg> pgs) { this.pgs = pgs; }
}