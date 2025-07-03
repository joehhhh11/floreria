package com.lulu.product.service;

import com.lulu.product.dto.ProductRequest;
import com.lulu.product.dto.ProductResponse;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ProductModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    ProductResponse getProduct(Long id);
    List<ProductResponse> getAllProducts();

    List<CategoryModel> getCategory();

    List<ProductModel> getProductsByCategory(Long categoriaId);

    void importFromExcel(MultipartFile file) throws Exception;
}
