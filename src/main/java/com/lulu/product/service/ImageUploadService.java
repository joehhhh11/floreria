package com.lulu.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageUploadService {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);
    private final String UPLOAD_DIR = "src/uploads/";

    public String upload(MultipartFile file) {
        logger.info("Iniciando subida de archivo: {} (size: {} bytes)", 
                   file.getOriginalFilename(), file.getSize());
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Directorio de uploads creado: {}", uploadPath);
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String resultPath = "/uploads/" + filename;
            logger.info("Archivo subido exitosamente: {}", resultPath);
            return resultPath;

        } catch (IOException e) {
            logger.error("Error al guardar archivo {}: {}", 
                        file.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }
    }
}
