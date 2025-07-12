package com.lulu.product.mapper;

import com.lulu.product.dto.ProductRequest;
import com.lulu.product.dto.ProductResponse;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ProductModel;
import com.lulu.product.repository.CategoryRepository;
import com.lulu.product.service.ImageUploadService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    @Mock
    private ImageUploadService imageUploadService;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductMapper productMapper;

    private ProductModel sampleProduct;
    private ProductRequest sampleRequest;
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
        sampleRequest.setImagenes(Arrays.asList()); // Lista vacía en lugar de null
    }

    @Test
    void toEntity_ShouldCreateProductModel_WhenValidRequest() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        // Act
        ProductModel result = productMapper.toEntity(sampleRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Rosa Roja", result.getName());
        assertEquals("Hermosa rosa roja", result.getDescription());
        assertEquals(25.99, result.getPrice());
        assertEquals(50, result.getStock());
        assertTrue(result.getDestacado());
        assertEquals(sampleCategory, result.getCategoria());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void toEntity_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productMapper.toEntity(sampleRequest)
        );

        assertEquals("Categoría no encontrada con id: 1", exception.getMessage());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateAllFields() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        ProductModel existingProduct = new ProductModel();
        existingProduct.setName("Nombre Anterior");
        existingProduct.setPrice(10.0);

        // Act
        productMapper.updateEntityFromRequest(existingProduct, sampleRequest);

        // Assert
        assertEquals("Rosa Roja", existingProduct.getName());
        assertEquals("Hermosa rosa roja", existingProduct.getDescription());
        assertEquals(25.99, existingProduct.getPrice());
        assertEquals(50, existingProduct.getStock());
        assertTrue(existingProduct.getDestacado());
        assertEquals(sampleCategory, existingProduct.getCategoria());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void updateEntityFromRequest_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ProductModel existingProduct = new ProductModel();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productMapper.updateEntityFromRequest(existingProduct, sampleRequest)
        );

        assertEquals("Categoría no encontrada con id: 1", exception.getMessage());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void toResponse_ShouldCreateProductResponse_WhenValidProduct() {
        // Act
        ProductResponse result = productMapper.toResponse(sampleProduct);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Rosa Roja", result.getName());
        assertEquals("Hermosa rosa roja", result.getDescription());
        assertEquals(25.99, result.getPrice());
        assertEquals(50, result.getStock());
        assertTrue(result.getDestacado());
        
        // Verificar la categoría en la respuesta
        assertNotNull(result.getCategoria());
        assertEquals(1L, result.getCategoria().getId());
        assertEquals("Rosas", result.getCategoria().getNombre());
    }

    @Test
    void toEntityFromLinks_ShouldCreateProductWithImageUrls() {
        // Arrange
        List<String> imageUrls = Arrays.asList(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg"
        );
        sampleRequest.setImageUrls(imageUrls);
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        // Act
        ProductModel result = productMapper.toEntityFromLinks(sampleRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Rosa Roja", result.getName());
        assertEquals("Hermosa rosa roja", result.getDescription());
        assertEquals(25.99, result.getPrice());
        assertEquals(50, result.getStock());
        assertTrue(result.getDestacado());
        assertEquals(sampleCategory, result.getCategoria());
        
        // Verificar las imágenes
        assertNotNull(result.getImagenes());
        assertEquals(2, result.getImagenes().size());
        assertEquals("https://example.com/image1.jpg", result.getImagenes().get(0).getImagenUrl());
        assertEquals("https://example.com/image2.jpg", result.getImagenes().get(1).getImagenUrl());
        
        // Verificar que cada imagen tiene referencia al producto
        result.getImagenes().forEach(image -> {
            assertEquals(result, image.getProducto());
        });

        verify(categoryRepository).findById(1L);
    }

    @Test
    void toEntityFromLinks_ShouldHandleNullImageUrls() {
        // Arrange
        sampleRequest.setImageUrls(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        // Act
        ProductModel result = productMapper.toEntityFromLinks(sampleRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Rosa Roja", result.getName());
        assertNotNull(result.getImagenes());
        assertTrue(result.getImagenes().isEmpty());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void toEntityFromLinks_ShouldHandleEmptyImageUrls() {
        // Arrange
        sampleRequest.setImageUrls(Arrays.asList());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        // Act
        ProductModel result = productMapper.toEntityFromLinks(sampleRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Rosa Roja", result.getName());
        assertNotNull(result.getImagenes());
        assertTrue(result.getImagenes().isEmpty());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void toEntityFromLinks_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productMapper.toEntityFromLinks(sampleRequest)
        );

        assertEquals("Categoría no encontrada", exception.getMessage());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void toResponse_ShouldHandleProductWithNullCategory() {
        // Arrange
        sampleProduct.setCategoria(null);

        // Act
        ProductResponse result = productMapper.toResponse(sampleProduct);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Rosa Roja", result.getName());
        assertNull(result.getCategoria());
    }

    @Test
    void toResponse_ShouldHandleProductWithEmptyImages() {
        // Arrange
        sampleProduct.setImagenes(Arrays.asList());

        // Act
        ProductResponse result = productMapper.toResponse(sampleProduct);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Rosa Roja", result.getName());
        assertNotNull(result.getImageUrls());
        assertTrue(result.getImageUrls().isEmpty());
    }
}
