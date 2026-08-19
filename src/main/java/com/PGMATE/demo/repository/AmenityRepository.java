package com.PGMATE.demo.repository;

import com.PGMATE.demo.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
    Set<Amenity> findByAmenityIdIn(Set<Integer> amenityIds);
}