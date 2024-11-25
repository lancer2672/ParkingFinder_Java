package com.project.parkingfinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.model.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByReviewId(Long reviewId);
}
