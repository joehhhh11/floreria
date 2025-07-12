package com.lulu.product.mapper;

import com.lulu.product.dto.CategoryResponse;
import com.lulu.product.dto.ProductRequest;
import com.lulu.product.dto.ProductResponse;
import com.lulu.product.model.CategoryModel;
import com.lulu.product.model.ImageModel;
import com.lulu.product.model.ProductModel;
import com.lulu.product.service.ImageUploadService;
import com.lulu.product.repository.CategoryRepository;
import com.lulu.reviews.dto.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private CategoryRepository categoryRepository;

    public ProductModel toEntity(ProductRequest request) {
        ProductModel product = new ProductModel();
        updateEntityFromRequest(product, request);
        return product;
    }

    public void updateEntityFromRequest(ProductModel product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDestacado(request.getDestacado());

        List<ImageModel> imagenes = procesarImagenes(request.getImagenes(), product);
        product.setImagenes(imagenes);

        CategoryModel category = categoryRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id: " + request.getCategoriaId()));
        product.setCategoria(category);
    }

    public ProductResponse toResponse(ProductModel product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setImageUrls(
                product.getImagenes().stream()
                        .map(ImageModel::getImagenUrl)
                        .collect(Collectors.toList())
        );
        if (product.getCategoria() != null) {
            CategoryResponse cat = new CategoryResponse();
            cat.setId(product.getCategoria().getId());
            cat.setNombre(product.getCategoria().getNombre());
            cat.setDescripcion(product.getCategoria().getDescripcion());
            response.setCategoria(cat);
        }
        response.setDestacado(product.getDestacado());
        response.setFechaCreacion(product.getFechaCreacion());

        if (product.getReviews() != null) {
            List<ReviewResponse> reviewResponses = product.getReviews().stream().map(review -> {
                ReviewResponse r = new ReviewResponse();
                r.setId(review.getId());
                r.setComentario(review.getComentario());
                r.setPuntuacion(review.getPuntuacion());
                r.setFechaReview(review.getFechaReview());
                r.setUsuario(review.getUser().getUsername());
                return r;
            }).collect(Collectors.toList());

            response.setReviews(reviewResponses);
        }
        return response;
    }

    private List<ImageModel> procesarImagenes(List<MultipartFile> archivos, ProductModel producto) {
        return archivos.stream()
                .map(file -> {
                    String url = imageUploadService.upload(file);
                    ImageModel img = new ImageModel();
                    img.setImagenUrl(url);
                    img.setProducto(producto);
                    return img;
                }).collect(Collectors.toList());
    }
    public ProductModel toEntityFromLinks(ProductRequest request) {
        ProductModel product = new ProductModel();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDestacado(request.getDestacado());

        // Validar que imageUrls no sea null
        List<ImageModel> imagenes = Optional.ofNullable(request.getImageUrls())
                .orElse(List.of()) // si es null, usa una lista vacía
                .stream()
                .map(url -> {
                    ImageModel img = new ImageModel();
                    img.setImagenUrl(url);
                    img.setProducto(product);
                    return img;
                })
                .collect(Collectors.toList());

        product.setImagenes(imagenes);

        CategoryModel category = categoryRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        product.setCategoria(category);

        return product;
    }


}
