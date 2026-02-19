package com.example.board.global.common.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class LocalFileStorage implements FileStorage{
    private final Path uploadRoot;
    private final String publicBaseUrl;

    public LocalFileStorage(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.public-base-url}") String publicBaseUrl
    ) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl;
    }

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String original = file.getOriginalFilename();
        String ext = getExtension(original);
        String filename = UUID.randomUUID().toString() + (ext.isBlank() ? "" : "." + ext);

        LocalDate now = LocalDate.now();
        String key = String.format("posts/%04d/%02d/%02d/%s", now.getYear(), now.getMonthValue(), now.getDayOfMonth(), filename);

        Path target = uploadRoot.resolve(key).normalize();

        try {
            Files.createDirectories(target.getParent());
            // 덮어쓰기 방지: REPLACE_EXISTING 빼고, 이미 있으면 예외나게
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }

        return key;
    }

    @Override
    public String toPublicUrl(String key) {
        //publicBaseUrl: "/files"
        return publicBaseUrl + "/" + key;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot == -1 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1).toLowerCase();
    }
}
