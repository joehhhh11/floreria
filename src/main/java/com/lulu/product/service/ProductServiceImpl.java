package com.lulu.product.service;

import com.lulu.product.dto.ProductRequest;
import com.lulu.product.dto.ProductResponse;
import com.lulu.product.mapper.ProductMapper;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ProductModel;
import com.lulu.product.repository.CategoryRepository;
import com.lulu.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        ProductModel product = productMapper.toEntity(request);
        ProductModel savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        ProductModel product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
        productMapper.updateEntityFromRequest(product, request);
        ProductModel updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponse getProduct(Long id) {
        ProductModel product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryModel> getCategory() {
        return categoryRepository.findAll();
    }
    @Override
    public List<ProductModel> getProductsByCategory(Long categoriaId) {
        return productRepository.findByCategoriaId(categoriaId);
    }
    public void importFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // salta encabezados

            Product product = new Product();
            product.setName(row.getCell(0).getStringCellValue());
            product.setDescription(row.getCell(1).getStringCellValue());
            product.setPrice(row.getCell(2).getNumericCellValue());
            product.setCategory(row.getCell(3).getStringCellValue());
            product.setImages(List.of(row.getCell(4).getStringCellValue())); // si es una URL

            productRepository.save(product);
        }

        workbook.close();
    }
}
