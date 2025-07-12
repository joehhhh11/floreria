package com.lulu.product.service;

import com.lulu.product.dto.ProductRequest;
import com.lulu.product.dto.ProductResponse;
import com.lulu.product.mapper.ProductMapper;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ProductModel;
import com.lulu.product.repository.CategoryRepository;
import com.lulu.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductModel sampleProduct;
    private ProductRequest sampleRequest;
    private ProductResponse sampleResponse;
    private CategoryModel sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new CategoryModel();
        sampleCategory.setId(1L);
        sampleCategory.setNombre("Rosas");

        sampleProduct = new ProductModel();
        sampleProduct.setId(1L);
        sampleProduct.setName("Rosa Roja");
        sampleProduct.setDescription("Hermosa rosa roja");
        sampleProduct.setPrice(25.99);
        sampleProduct.setStock(50);
        sampleProduct.setDestacado(true);
        sampleProduct.setCategoria(sampleCategory);

        sampleRequest = new ProductRequest();
        sampleRequest.setName("Rosa Roja");
        sampleRequest.setDescription("Hermosa rosa roja");
        sampleRequest.setPrice(25.99);
        sampleRequest.setStock(50);
        sampleRequest.setDestacado(true);
        sampleRequest.setCategoriaId(1L);

        sampleResponse = new ProductResponse();
        sampleResponse.setId(1L);
        sampleResponse.setName("Rosa Roja");
        sampleResponse.setDescription("Hermosa rosa roja");
        sampleResponse.setPrice(25.99);
        sampleResponse.setStock(50);
        sampleResponse.setDestacado(true);
    }

    @Test
    void createProduct_ShouldReturnProductResponse_WhenValidRequest() {
        // Arrange
        when(productMapper.toEntity(sampleRequest)).thenReturn(sampleProduct);
        when(productRepository.save(sampleProduct)).thenReturn(sampleProduct);
        when(productMapper.toResponse(sampleProduct)).thenReturn(sampleResponse);

        // Act
        ProductResponse result = productService.createProduct(sampleRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Rosa Roja", result.getName());
        assertEquals(25.99, result.getPrice());
        assertEquals(50, result.getStock());
        assertTrue(result.getDestacado());

        verify(productMapper).toEntity(sampleRequest);
        verify(productRepository).save(sampleProduct);
        verify(productMapper).toResponse(sampleProduct);
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(sampleProduct)).thenReturn(sampleProduct);
        when(productMapper.toResponse(sampleProduct)).thenReturn(sampleResponse);

        // Act
        ProductResponse result = productService.updateProduct(1L, sampleRequest);

        // Assert
        assertNotNull(result);
        verify(productRepository).findById(1L);
        verify(productMapper).updateEntityFromRequest(sampleProduct, sampleRequest);
        verify(productRepository).save(sampleProduct);
        verify(productMapper).toResponse(sampleProduct);
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.updateProduct(1L, sampleRequest)
        );

        assertEquals("Producto no encontrado con id: 1", exception.getMessage());
        verify(productRepository).findById(1L);
        verify(productMapper, never()).updateEntityFromRequest(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_ShouldDeleteProduct_WhenProductExists() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        // Assert
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.deleteProduct(1L)
        );

        assertEquals("Producto no encontrado con id: 1", exception.getMessage());
        verify(productRepository).existsById(1L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void getProduct_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productMapper.toResponse(sampleProduct)).thenReturn(sampleResponse);

        // Act
        ProductResponse result = productService.getProduct(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Rosa Roja", result.getName());
        verify(productRepository).findById(1L);
        verify(productMapper).toResponse(sampleProduct);
    }

    @Test
    void getProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.getProduct(1L)
        );

        assertEquals("Producto no encontrado con id: 1", exception.getMessage());
        verify(productRepository).findById(1L);
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<ProductModel> products = Arrays.asList(sampleProduct);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponse(sampleProduct)).thenReturn(sampleResponse);

        // Act
        List<ProductResponse> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Rosa Roja", result.get(0).getName());
        verify(productRepository).findAll();
        verify(productMapper).toResponse(sampleProduct);
    }

    @Test
    void getCategory_ShouldReturnAllCategories() {
        // Arrange
        List<CategoryModel> categories = Arrays.asList(sampleCategory);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<CategoryModel> result = productService.getCategory();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Rosas", result.get(0).getNombre());
        verify(categoryRepository).findAll();
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsOfCategory() {
        // Arrange
        List<ProductModel> products = Arrays.asList(sampleProduct);
        when(productRepository.findByCategoriaId(1L)).thenReturn(products);

        // Act
        List<ProductModel> result = productService.getProductsByCategory(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Rosa Roja", result.get(0).getName());
        verify(productRepository).findByCategoriaId(1L);
    }
}
