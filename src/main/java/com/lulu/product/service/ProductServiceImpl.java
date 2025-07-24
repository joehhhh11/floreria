package com.lulu.product.service;

import com.lulu.product.dto.ProductRequest;
import com.lulu.product.dto.ProductResponse;
import com.lulu.product.mapper.ProductMapper;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ProductModel;
import com.lulu.product.repository.CategoryRepository;
import com.lulu.product.repository.ProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private double getNumericValue(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) return Double.parseDouble(cell.getStringCellValue());
        return 0;
    }

    private boolean getBooleanValue(Cell cell) {
        if (cell == null) return false;
        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case STRING -> cell.getStringCellValue().equalsIgnoreCase("true");
            case NUMERIC -> cell.getNumericCellValue() != 0;
            default -> false;
        };
    }

    @Override

    public void importFromExcel(MultipartFile file) throws Exception {
        List<ProductModel> products = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // saltar encabezado

                // Validar celdas no nulas
                if (row.getCell(0) == null) continue;

                String name = getStringValue(row.getCell(0));
                String description = getStringValue(row.getCell(1));
                double price = getNumericValue(row.getCell(2));
                int stock = (int) getNumericValue(row.getCell(3));
                boolean destacado = getBooleanValue(row.getCell(4));
                long categoriaId = (long) getNumericValue(row.getCell(5));
                String rawLinks = getStringValue(row.getCell(6));

                List<String> urls = Arrays.stream(rawLinks.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                ProductRequest request = new ProductRequest();
                request.setName(name);
                request.setDescription(description);
                request.setPrice(price);
                request.setStock(stock);
                request.setDestacado(destacado);
                request.setCategoriaId(categoriaId);
                request.setImageUrls(urls); // <--- Aquí está el punto importante

                ProductModel model = productMapper.toEntityFromLinks(request);
                products.add(model);
            }

            productRepository.saveAll(products);
        }
    }

    }



