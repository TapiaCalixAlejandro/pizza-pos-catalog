package com.msvc.catalog.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {

    String store(MultipartFile file) throws IOException;

    void delete(String imageKey) throws IOException;

    StoredImage load(String imageKey) throws IOException;

}
