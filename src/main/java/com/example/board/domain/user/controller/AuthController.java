package com.example.board.domain.user.controller;

import com.example.board.domain.user.dto.response.AuthTokenResponseDto;
import com.example.board.domain.user.dto.request.SigninRequestDto;
import com.example.board.domain.user.dto.request.SignupRequestDto;
import com.example.board.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequestDto dto) {
        authService.signup(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthTokenResponseDto> signin(@Valid @RequestBody SigninRequestDto dto) {
        return ResponseEntity.ok(authService.signin(dto));
    }
}
