package com.example.board.domain.user.service;

import com.example.board.domain.user.dto.request.UpdateRequestDto;
import com.example.board.domain.user.dto.response.UserResponseDto;
import com.example.board.domain.user.entity.User;
import com.example.board.domain.user.repository.UserRepository;
import com.example.board.global.exception.DomainException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."));

        if (user.isDeleted()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "삭제된 사용자입니다.");
        }

        return new UserResponseDto(user.getNickname(), user.getEmail());
    }

    @Transactional
    public void updatePassword(Long id, UpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."));

        if (user.isDeleted()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "삭제된 사용자입니다.");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new DomainException(ErrorCode.INVALID_REQUEST, "비밀번호가 올바르지 않습니다.");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new DomainException(ErrorCode.INVALID_REQUEST, "기존 비밀번호와 동일합니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

        user.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."));

        if (user.isDeleted()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "이미 삭제된 사용자입니다.");
        }

        user.delete();
    }
}
