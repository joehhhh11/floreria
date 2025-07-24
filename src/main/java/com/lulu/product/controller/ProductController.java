package com.lulu.product.controller;

import com.lulu.product.dto.ProductRequest;
import com.lulu.product.dto.ProductResponse;
import com.lulu.product.mapper.ProductMapper;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ProductModel;
import com.lulu.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ProductResponse> createProduct(@ModelAttribute ProductRequest request) {
        try {
            logger.info("Creando nuevo producto: name={}, categoryId={}, price={}, stock={}", 
                       request.getName(), request.getCategoriaId(), request.getPrice(), request.getStock());
            
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                logger.error("Nombre del producto es requerido");
                return ResponseEntity.badRequest().build();
            }
            if (request.getCategoriaId() == null) {
                logger.error("CategoryId es requerido");
                return ResponseEntity.badRequest().build();
            }
            
            ProductResponse response = productService.createProduct(request);
            logger.info("Producto creado exitosamente con ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al crear producto: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(
            value = "/json",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ProductResponse> createProductJson(@RequestBody ProductRequest request) {
        logger.info("Creando nuevo producto desde JSON");
        ProductResponse response = productService.createProduct(request);
        logger.info("Producto creado exitosamente con ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        logger.info("Actualizando producto ID: {}", id);
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        logger.warn("Eliminando producto ID: {}", id);
        productService.deleteProduct(id);
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        logger.debug("Consultando producto ID: {}", id);
        return productService.getProduct(id);
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/category/{id}")
    public List<ProductResponse> getByCategory(@PathVariable Long id) {
        return productService.getProductsByCategory(id)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
    @PostMapping("/import")
    public ResponseEntity<String> importFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Iniciando importación de productos desde archivo: {}", 
                       file.getOriginalFilename());
            long startTime = System.currentTimeMillis();
            
            productService.importFromExcel(file);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Importación completada en {} ms", duration);
            
            return ResponseEntity.ok("Archivo importado correctamente");
        } catch (Exception e) {
            logger.error("Error durante importación de archivo {}: {}", 
                        file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error al importar: " + e.getMessage());
        }
    }

}
