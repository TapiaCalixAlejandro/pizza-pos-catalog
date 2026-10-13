package com.msvc.catalog.service.storage;

public record StoredImage(
        byte[] content,
        String contentType
) {
}
