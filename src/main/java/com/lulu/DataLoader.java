package com.lulu;

import com.lulu.product.model.CategoryModel;
import com.lulu.product.repository.CategoryRepository;
import com.lulu.auth.model.UserModel;
import com.lulu.auth.model.RolModel;
import com.lulu.auth.repository.UserRepository;
import com.lulu.auth.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Insertar categorías
        insertCategoryIfNotExists("Flores", "Flores frescas y variadas");
        insertCategoryIfNotExists("Ramos", "Ramos personalizados para toda ocasión");
        insertCategoryIfNotExists("Globos", "Globos de helio y decorativos");
        insertCategoryIfNotExists("Peluches", "Peluches suaves y adorables");
        
        // Crear roles por defecto
        createRolesIfNotExists();
        
        // Crear usuario admin por defecto
        createAdminUserIfNotExists();
    }

    private void insertCategoryIfNotExists(String nombre, String descripcion) {
        if (!categoryRepository.existsByNombre(nombre)) {
            CategoryModel category = new CategoryModel();
            category.setNombre(nombre);
            category.setDescripcion(descripcion);
            categoryRepository.save(category);
        }
    }
    
    private void createRolesIfNotExists() {
        // Crear rol admin
        if (!rolRepository.findByTipoRol("admin").isPresent()) {
            RolModel adminRole = new RolModel();
            adminRole.setTipoRol("admin");
            rolRepository.save(adminRole);
            System.out.println("✅ Rol admin creado");
        }
        
        // Crear rol usuario
        if (!rolRepository.findByTipoRol("usuario").isPresent()) {
            RolModel userRole = new RolModel();
            userRole.setTipoRol("usuario");
            rolRepository.save(userRole);
            System.out.println("✅ Rol usuario creado");
        }
    }
    
    private void createAdminUserIfNotExists() {
        if (!userRepository.existsByUsername("admin")) {
            RolModel adminRole = rolRepository.findByTipoRol("admin")
                .orElseThrow(() -> new RuntimeException("Rol admin no encontrado"));
            
            UserModel admin = new UserModel();
            admin.setUsername("admin");
            admin.setNombre("Administrador");
            admin.setApellidos("Sistema");
            admin.setCorreo("admin@floreria.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(adminRole);
            admin.setTelefono("999999999");
            admin.setDni("12345678");
            admin.setEstado("activo");
            userRepository.save(admin);
            System.out.println("✅ Usuario admin creado: username=admin, password=admin123");
        }
    }
}
