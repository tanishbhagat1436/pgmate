package com.PGMATE.demo.controller;

import com.PGMATE.demo.model.Amenity;
import com.PGMATE.demo.model.Pg;
import com.PGMATE.demo.repository.AmenityRepository;
import com.PGMATE.demo.repository.PgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class PgController {

    @Autowired
    private PgRepository pgRepository;

    @Autowired
    private AmenityRepository amenityRepository; // <-- INJECT AMENITY REPO

    @GetMapping("/pgs")
    public List<Pg> getAllPgs() {
        return pgRepository.findAll();
    }

    @GetMapping("/pgs/{id}")
    public ResponseEntity<Pg> getPgById(@PathVariable Integer id) {
        Pg pg = pgRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PG not found with id: " + id));
        return ResponseEntity.ok(pg);
    }

    @GetMapping("/pgs/owner/{ownerId}")
    public ResponseEntity<List<Pg>> getPgsByOwnerId(@PathVariable Integer ownerId) {
        List<Pg> pgs = pgRepository.findByOwnerId(ownerId);
        if (pgs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pgs);
    }

    @PostMapping("/pgs")
    public Pg createPg(@RequestBody Map<String, Object> payload) {
        Pg pg = mapPayloadToPg(new Pg(), payload);
        return pgRepository.save(pg);
    }

    @PutMapping("/pgs/{id}")
    public ResponseEntity<Pg> updatePg(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        Pg pg = pgRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PG not found with id: " + id));
        
        pg = mapPayloadToPg(pg, payload);
        
        final Pg updatedPg = pgRepository.save(pg);
        return ResponseEntity.ok(updatedPg);
    }

    @DeleteMapping("/pgs/{id}")
    public ResponseEntity<Void> deletePg(@PathVariable Integer id) {
        pgRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Pg mapPayloadToPg(Pg pg, Map<String, Object> payload) {
        pg.setOwnerId((Integer) payload.get("ownerId"));
        pg.setPgName((String) payload.get("pgName"));
        pg.setCity((String) payload.get("city"));
        pg.setAddress((String) payload.get("address"));
        pg.setPgType((String) payload.get("pgType"));
        pg.setContactPhone((String) payload.get("contactPhone"));
        pg.setContactEmail((String) payload.get("contactEmail"));
        pg.setRoomType((String) payload.get("roomType"));
        pg.setRent(Integer.parseInt(payload.get("rent").toString())); 
        pg.setDescription((String) payload.get("description")); 
        pg.setRules((String) payload.get("rules"));             

        String image = (String) payload.get("image");
        if (image != null && !image.isEmpty()) {
            pg.setImage(image);
        }

        List<Integer> amenityIds = (List<Integer>) payload.get("amenities");
        if (amenityIds != null && !amenityIds.isEmpty()) {
            Set<Amenity> amenities = amenityRepository.findByAmenityIdIn(new HashSet<>(amenityIds));
            pg.setAmenities(amenities);
        } else {
            pg.setAmenities(new HashSet<>()); 
        }

        return pg;
    }
}