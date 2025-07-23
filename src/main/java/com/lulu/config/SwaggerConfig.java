package com.lulu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("https://floreria-production.up.railway.app")
                                .description("Servidor de Producción"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local")
                ))
                .info(new Info()
                        .title("🌸 Floreria API")
                        .version("1.0.0")
                        .description("API REST para el sistema de gestión de floristería con funcionalidades de e-commerce, autenticación JWT, gestión de productos, pedidos y pagos con Stripe.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("admin@floreria.com")
                                .url("https://floreria-production.up.railway.app"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Introduce el token JWT obtenido del endpoint /api/auth/login")));
    }
}
