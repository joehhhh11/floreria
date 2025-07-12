package com.lulu.product.controller;

import com.lulu.product.dto.ProductRequest;
import com.lulu.product.dto.ProductResponse;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ProductModel;
import com.lulu.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTestSimple {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;
    private ProductResponse sampleResponse;
    private ProductRequest sampleRequest;
    private CategoryModel sampleCategory;
    private ProductModel sampleProduct;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        
        sampleCategory = new CategoryModel();
        sampleCategory.setId(1L);
        sampleCategory.setNombre("Rosas");

        sampleProduct = new ProductModel();
        sampleProduct.setId(1L);
        sampleProduct.setName("Rosa Roja");
        sampleProduct.setDescription("Rosa roja hermosa");
        sampleProduct.setPrice(15.99);
        sampleProduct.setStock(10);
        sampleProduct.setCategoria(sampleCategory);

        sampleRequest = new ProductRequest();
        sampleRequest.setName("Rosa Roja");
        sampleRequest.setDescription("Rosa roja hermosa");
        sampleRequest.setPrice(15.99);
        sampleRequest.setStock(10);
        sampleRequest.setCategoriaId(1L);

        sampleResponse = new ProductResponse();
        sampleResponse.setId(1L);
        sampleResponse.setName("Rosa Roja");
        sampleResponse.setDescription("Rosa roja hermosa");
        sampleResponse.setPrice(15.99);
        sampleResponse.setStock(10);
    }

    @Test
    void getAllProducts_ShouldReturnProductList() throws Exception {
        List<ProductResponse> products = Arrays.asList(sampleResponse);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Rosa Roja"));

        verify(productService).getAllProducts();
    }

    @Test
    void getProduct_ShouldReturnProduct_WhenValidId() throws Exception {
        when(productService.getProduct(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Rosa Roja"));

        verify(productService).getProduct(1L);
    }

    @Test
    void getProductsByCategory_ShouldReturnProductList_WhenValidCategoryId() throws Exception {
        List<ProductModel> products = Arrays.asList(sampleProduct);
        when(productService.getProductsByCategory(1L)).thenReturn(products);

        mockMvc.perform(get("/api/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(productService).getProductsByCategory(1L);
    }

    @Test
    void updateProduct_ShouldReturnProductResponse_WhenValidRequest() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Rosa Roja"));

        verify(productService).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    void updateProduct_ShouldAcceptRequest_EvenWithMissingFields() throws Exception {
        ProductRequest invalidRequest = new ProductRequest();
        // En este controller no hay validación, así que acepta cualquier request
        when(productService.updateProduct(eq(1L), any(ProductRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_ShouldReturnOk_WhenValidId() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk());

        verify(productService).deleteProduct(1L);
    }
}
