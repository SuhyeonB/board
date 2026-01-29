package com.example.board.global.security;

import com.example.board.domain.user.entity.User;
import com.example.board.domain.user.repository.UserRepository;
import com.example.board.global.exception.DomainException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "해당 이메일의 사용자를 찾을 수 없습니다."));

        // soft delete 사용자 차단
        if (user.isDeleted()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "삭제된 사용자 입니다.");
        }

        return CustomUserDetails.from(user);
    }
}
