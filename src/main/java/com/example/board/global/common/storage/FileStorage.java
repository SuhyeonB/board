package com.example.board.global.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    String upload(MultipartFile file);
    String toPublicUrl(String key);
}
