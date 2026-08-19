package com.PGMATE.demo.controller;

import com.PGMATE.demo.dto.FeedbackDTO;
import com.PGMATE.demo.model.Feedback;
import com.PGMATE.demo.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/feedbacks")
    public ResponseEntity<?> createFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        try {
            Feedback feedback = new Feedback();
            feedback.setPgId(feedbackDTO.getPgId());
            feedback.setTenantId(feedbackDTO.getTenantId());
            feedback.setOwnerId(feedbackDTO.getOwnerId());
            feedback.setRating(feedbackDTO.getRating());
            feedback.setComment(feedbackDTO.getComment());
            
            feedbackRepository.save(feedback);
            return new ResponseEntity<>("Feedback submitted successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to submit feedback", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/feedbacks/owner/{ownerId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackForOwner(@PathVariable Integer ownerId) {
        List<FeedbackDTO> feedbacks = feedbackRepository.findFeedbacksByOwnerId(ownerId);
        if (feedbacks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(feedbacks);
    }
}