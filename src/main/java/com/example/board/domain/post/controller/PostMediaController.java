package com.example.board.domain.post.controller;

import com.example.board.global.common.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/media")
public class PostMediaController {

    private final FileStorage fileStorage;

    @PostMapping("/images")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image) {
        String key = fileStorage.upload(image);
        String url = fileStorage.toPublicUrl(key);

        return ResponseEntity.ok().body(Map.of("key", key, "url", url));
    }
}
