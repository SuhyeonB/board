package com.example.board.global.security;

import com.example.board.domain.user.entity.User;
import com.example.board.domain.user.entity.UserRole;
import com.example.board.global.exception.DomainException;
import com.example.board.global.exception.ErrorCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {

    private final Long userId;    // 게시글/댓글 작성자 검증에 매번 필요함
    private final String email;
    private final String password;
    private final UserRole role;

    private final boolean enabled;

    public CustomUserDetails(Long userId, String email, String password, UserRole role, boolean enabled) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.enabled = enabled;
    }

    //Entity -> CustomUserDetails
    public static CustomUserDetails from(User user) {
        if (user.isDeleted()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "삭제된 사용자입니다.");
            // 만약 ErrorCode에 적절한 코드가 없다면:
            // throw new DomainException(ErrorCode.INVALID_REQUEST, "삭제된 사용자입니다.");
        }

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getUserRole(),
                true
        );
    }

    public Long getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        // Security에서 username으로 사용하는 값: email
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
