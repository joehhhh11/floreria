package com.lulu;

import com.lulu.product.model.CategoryModel;
import com.lulu.product.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        insertCategoryIfNotExists("Flores", "Flores frescas y variadas");
        insertCategoryIfNotExists("Ramos", "Ramos personalizados para toda ocasión");
        insertCategoryIfNotExists("Globos", "Globos de helio y decorativos");
        insertCategoryIfNotExists("Peluches", "Peluches suaves y adorables");
    }

    private void insertCategoryIfNotExists(String nombre, String descripcion) {
        if (!categoryRepository.existsByNombre(nombre)) {
            CategoryModel category = new CategoryModel();
            category.setNombre(nombre);
            category.setDescripcion(descripcion);
            categoryRepository.save(category);
        }
    }
}
