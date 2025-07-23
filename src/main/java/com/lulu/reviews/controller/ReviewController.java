package com.lulu.reviews.controller;

import com.lulu.auth.model.UserModel;
import com.lulu.auth.security.AuthenticatedUserProvider;
import com.lulu.product.model.ProductModel;
import com.lulu.product.repository.ProductRepository;
import com.lulu.product.service.ProductService;
import com.lulu.reviews.dto.ReviewRequest;
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
    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<ReviewModel> createReview(@RequestBody ReviewRequest request) {
        UserModel currentUser = authenticatedUserProvider.getCurrentUser();

        if (currentUser == null || currentUser.getId() == null) {
            throw new RuntimeException("Usuario no autenticado o sin ID válido");
        }

        ProductModel product = productRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));


        ReviewModel review = new ReviewModel();
        review.setComentario(request.getComentario());
        review.setPuntuacion(request.getPuntuacion());
        review.setUser(currentUser);
        review.setProducto(product);
        review.setFechaReview(LocalDateTime.now());

        return ResponseEntity.ok(reviewService.saveReview(review));
    }

}
