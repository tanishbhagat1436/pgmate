package com.PGMATE.demo.model;

import jakarta.persistence.*;
import java.util.Set; 

@Entity
@Table(name = "pgs")
public class Pg {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pg_id")
    private Integer pgId;
    
    @Column(name = "owner_id")
    private Integer ownerId;
    
    @Column(name = "pg_name")
    private String pgName;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    private String city;
    
    @Column(name = "pg_type")
    private String pgType;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String rules;

    private String roomType;
    private Integer rent;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "pg_amenities",
        joinColumns = @JoinColumn(name = "pg_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities;
    

    public Integer getPgId() { return pgId; }
    public void setPgId(Integer pgId) { this.pgId = pgId; }
    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
    public String getPgName() { return pgName; }
    public void setPgName(String pgName) { this.pgName = pgName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPgType() { return pgType; }
    public void setPgType(String pgType) { this.pgType = pgType; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public Integer getRent() { return rent; }
    public void setRent(Integer rent) { this.rent = rent; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public Set<Amenity> getAmenities() { return amenities; }
    public void setAmenities(Set<Amenity> amenities) { this.amenities = amenities; }
}