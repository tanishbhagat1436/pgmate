package com.PGMATE.demo.repository;

import com.PGMATE.demo.model.Pg;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PgRepository extends JpaRepository<Pg, Integer> {
    List<Pg> findByOwnerId(Integer ownerId);
}