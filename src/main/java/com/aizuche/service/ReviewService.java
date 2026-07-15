package com.aizuche.service;

import com.aizuche.entity.Review;
import com.aizuche.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public List<Review> findPopular() {
        return reviewRepository.findAllOrderByPopular();
    }

    public List<Review> findByVehicleId(Long vehicleId) {
        return reviewRepository.findByVehicleId(vehicleId);
    }

    public List<Review> findByCustomerId(Long customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public boolean hasReviewed(Long orderId) {
        return reviewRepository.existsByOrderId(orderId);
    }

    @Transactional
    public Review save(Review review) {
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        if (review.getLikes() == null) review.setLikes(0);
        if (review.getViews() == null) review.setViews(0);
        return reviewRepository.save(review);
    }

    @Transactional
    public void incrementLikes(Long id) {
        reviewRepository.findById(id).ifPresent(r -> {
            r.setLikes((r.getLikes() != null ? r.getLikes() : 0) + 1);
            r.setUpdateTime(LocalDateTime.now());
            reviewRepository.save(r);
        });
    }

    @Transactional
    public void incrementViews(Long id) {
        reviewRepository.findById(id).ifPresent(r -> {
            r.setViews((r.getViews() != null ? r.getViews() : 0) + 1);
            reviewRepository.save(r);
        });
    }
}
