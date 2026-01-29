package com.example.board.domain.user.controller;

import com.example.board.domain.user.dto.request.UpdateRequestDto;
import com.example.board.domain.user.dto.response.UserResponseDto;
import com.example.board.domain.user.service.UserService;
import com.example.board.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(userService.getMyInfo(user.getUserId()));
    }

    @PutMapping("/me/update")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody UpdateRequestDto dto
    ) {
        userService.updatePassword(user.getUserId(), dto);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/me/delete")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails user) {
        userService.deleteUser(user.getUserId());
        return ResponseEntity.ok().build();
    }
}
