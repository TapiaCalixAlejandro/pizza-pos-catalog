package com.msvc.catalog.shared.validation;

import com.msvc.catalog.shared.exception.InvalidImageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

@Component
public class ImageValidator {

    private static final long MAX_SIZE = 5 * 1024 *1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png"
    );

    public void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Image is required.");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new InvalidImageException("Image must not exceed 5MB.");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidImageException("Only JPG and PNG images are allowed.");
        }

        validateImageContent(file);
    }

    private void validateImageContent(MultipartFile file) {

        try {

            BufferedImage image = ImageIO.read(
                    file.getInputStream()
            );

            if (image == null) {
                throw new InvalidImageException("The uploaded file is not a valid image.");
            }

        } catch (IOException exception) {
            throw new InvalidImageException("Could not validate image.");
        }

    }

}
