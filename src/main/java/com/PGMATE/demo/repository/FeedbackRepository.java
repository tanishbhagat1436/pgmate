package com.PGMATE.demo.repository;

import com.PGMATE.demo.dto.FeedbackDTO;
import com.PGMATE.demo.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    @Query("SELECT new com.PGMATE.demo.dto.FeedbackDTO(p.pgName, u.name, f.rating, f.comment, f.createdAt) " +
           "FROM Feedback f " +
           "JOIN Pg p ON f.pgId = p.pgId " +
           "JOIN User u ON f.tenantId = u.userId " +
           "WHERE f.ownerId = :ownerId " +
           "ORDER BY f.createdAt DESC")
    List<FeedbackDTO> findFeedbacksByOwnerId(@Param("ownerId") Integer ownerId);
}