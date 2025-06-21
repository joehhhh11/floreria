package com.lulu.auth.controller;

import com.lulu.auth.model.ProductoModel;
import com.lulu.auth.repository.ProductoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // POST: Crear producto
    @PostMapping
    public ProductoModel crearProducto(@RequestBody ProductoModel producto) {
        return productoRepository.save(producto);
    }

    // GET: Listar todos los productos
    @GetMapping
    public List<ProductoModel> listarProductos() {
        return productoRepository.findAll();
    }

    // GET: Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoModel> obtenerProducto(@PathVariable Long id) {
        Optional<ProductoModel> producto = productoRepository.findById(id);
        return producto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT: Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoModel> actualizarProducto(@PathVariable Long id, @RequestBody ProductoModel producto) {
        return productoRepository.findById(id)
                .map(p -> {
                    p.setNombre(producto.getNombre());
                    p.setDescripcion(producto.getDescripcion());
                    p.setPrecio(producto.getPrecio());
                    p.setStock(producto.getStock());
                    productoRepository.save(p);
                    return ResponseEntity.ok(p);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE: Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
