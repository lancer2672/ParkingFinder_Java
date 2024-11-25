package com.project.parkingfinder.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkingfinder.dto.ReplyDTO;
import com.project.parkingfinder.model.Reply;
import com.project.parkingfinder.model.Review;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.service.ReplyService;
import com.project.parkingfinder.service.ReviewService;
import com.project.parkingfinder.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {

    private final ReplyService replyService;
    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public ReplyController(ReplyService replyService, UserService userService, ReviewService reviewService) {
        this.replyService = replyService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<List<Reply>> getRepliesByReviewId(@PathVariable Long reviewId) {
        List<Reply> replies = replyService.findByReviewId(reviewId);
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reply> getReplyById(@PathVariable Long id) {
        Reply reply = replyService.findById(id);
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Reply> createReply(@RequestBody @Valid ReplyDTO.CreateReplyRequest request) {
        Reply reply = new Reply();
        User user = userService.getUser(request.getUserId());
        Review review = reviewService.findById(request.getReviewId());
        reply.setUser(user);
        reply.setReview(review);
        reply.setComment(request.getComment());
        reply.setCreated(LocalDateTime.now());
        reply.setUpdated(LocalDateTime.now());

        Reply newReply = replyService.save(reply);
        return new ResponseEntity<>(newReply, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reply> updateReply(@PathVariable Long id, @RequestBody Reply reply) {
        Reply updatedReply = replyService.update(id, reply);
        return new ResponseEntity<>(updatedReply, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReply(@PathVariable Long id) {
        replyService.delete(id);
        return new ResponseEntity<>("Reply with ID " + id + " has been deleted", HttpStatus.OK);
    }
}
