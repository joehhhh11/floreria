package com.lulu.reviews.service;

import com.lulu.reviews.model.ReviewModel;
import com.lulu.reviews.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ReviewModel saveReview(ReviewModel review) {
        return reviewRepository.save(review);
    }

    public List<ReviewModel> getReviewsByProduct(Long productId) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getProducto().getId().equals(productId))
                .toList();
    }
}
