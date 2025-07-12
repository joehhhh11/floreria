package com.lulu.reviews.repository;

import com.lulu.reviews.model.ReviewModel;
import com.lulu.product.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewModel, Long> {
    List<ReviewModel> findByProducto(ProductModel producto);
}
