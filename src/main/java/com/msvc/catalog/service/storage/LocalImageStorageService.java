package com.msvc.catalog.service.storage;

import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalImageStorageService implements ImageStorageService {

    private final Path storageLocation;

    public LocalImageStorageService(
            @Value("${app.storage.location}")
            String storageLocation
    ) {
            this.storageLocation = Paths.get(storageLocation)
                    .toAbsolutePath()
                    .normalize();

            try {
                Files.createDirectories(this.storageLocation);
            } catch (IOException e) {
                throw new IllegalStateException("Could not initialize image storage.", e);
            }
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;

        Path targetLocation = storageLocation
                .resolve("products")
                .resolve(fileName)
                .normalize();

        Files.createDirectories(targetLocation.getParent());

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return "products/" + fileName;
    }

    @Override
    public void delete(String imageKey) throws IOException {
        if (imageKey == null || imageKey.isBlank()) {
            return;
        }

        Path targetLocation = storageLocation
                .resolve(imageKey)
                .normalize();

        if (!targetLocation.startsWith(storageLocation)) {
            throw new IllegalArgumentException("Invalid image path.");
        }

        Files.deleteIfExists(targetLocation);
    }

    @Override
    public StoredImage load(String imageKey) throws IOException {
        Path targetLocation = storageLocation
                .resolve(imageKey)
                .normalize();

        if (!targetLocation.startsWith(storageLocation)) {
            throw new IllegalArgumentException("Invalid image path.");
        }

        if (!Files.exists(targetLocation)) {
            throw new ResourceNotFoundException("Image not found.");
        }

        String contentType = Files.probeContentType(targetLocation);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return new StoredImage(
                Files.readAllBytes(targetLocation),
                contentType
        );
    }

    private String getExtension(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);

        if (extension == null || extension.isBlank()) {
            return "";
        }

        return "." + extension.toLowerCase();
    }
}
