package com.project.parkingfinder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.parkingfinder.exception.ResourceNotFoundException;
import com.project.parkingfinder.model.Reply;
import com.project.parkingfinder.repository.ReplyRepository;

@Service
public class ReplyService {

    @Autowired
    private ReplyRepository replyRepository;

    public List<Reply> findAll() {
        return replyRepository.findAll();
    }

    public Reply findById(Long id) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy reply với id: " + id));

        return reply;
    }

    public List<Reply> findByReviewId(Long reviewId) {
        return replyRepository.findByReviewId(reviewId);
    }

    public Reply save(Reply reply) {
        return replyRepository.save(reply);
    }

    public Reply update(Long id, Reply reply) {
        reply.setId(id);
        return replyRepository.save(reply);
    }

    public void delete(Long id) {
        replyRepository.deleteById(id);
    }
}
