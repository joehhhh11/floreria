package com.lulu.product.controller;

import com.lulu.product.model.CategoryModel;
import com.lulu.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<CategoryModel> getAllCategories() {
        return productService.getCategory();
    }
}
