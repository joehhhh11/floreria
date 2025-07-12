package com.lulu.reviews.controller;

import com.lulu.auth.model.UserModel;
import com.lulu.auth.security.AuthenticatedUserProvider;
import com.lulu.product.service.ProductService;
import com.lulu.reviews.model.ReviewModel;
import com.lulu.reviews.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewModel> createReview(@RequestBody ReviewModel reviewRequest) {
        UserModel currentUser = authenticatedUserProvider.getCurrentUser();

        if (currentUser == null || currentUser.getId() == null) {
            throw new RuntimeException("Usuario no autenticado o sin ID válido");
        }

        reviewRequest.setUser(currentUser);

        reviewRequest.setFechaReview(LocalDateTime.now());

        return ResponseEntity.ok(reviewService.saveReview(reviewRequest));
    }

}
