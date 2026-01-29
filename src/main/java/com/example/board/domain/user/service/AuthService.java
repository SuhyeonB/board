package com.example.board.domain.user.service;

import com.example.board.domain.user.dto.response.AuthTokenResponseDto;
import com.example.board.domain.user.dto.request.SigninRequestDto;
import com.example.board.domain.user.dto.request.SignupRequestDto;
import com.example.board.domain.user.entity.User;
import com.example.board.domain.user.entity.UserRole;
import com.example.board.domain.user.repository.UserRepository;
import com.example.board.global.exception.DomainException;
import com.example.board.global.exception.ErrorCode;
import com.example.board.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(SignupRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DomainException(ErrorCode.INVALID_REQUEST, "이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .userRole(UserRole.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthTokenResponseDto signin(SigninRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if(user.isDeleted()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "삭제된 사용자입니다.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new DomainException(ErrorCode.INVALID_REQUEST, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getUserRole()
        );

        return  new AuthTokenResponseDto(accessToken);
    }
}
