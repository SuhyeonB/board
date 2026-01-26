package com.example.board.user.service;

import com.example.board.user.dto.SignupRequestDto;
import com.example.board.user.dto.UpdateRequestDto;
import com.example.board.user.dto.SigninRequestDto;
import com.example.board.user.dto.UserResponseDto;
import com.example.board.user.entity.User;
import com.example.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto signup(SignupRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User(dto.getUsername(), dto.getEmail(), dto.getPassword());
        User savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getUsername(),  savedUser.getEmail(), savedUser.getPassword());
    }

    @Transactional
    public void signin(@RequestBody SigninRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("This email does not exist."));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new IllegalArgumentException("Passwords don't match.");
        }

        System.out.println("Signing in with username " + user.getUsername());
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This user does not exist."));

        return new UserResponseDto(user.getUsername(), user.getEmail(), user.getPassword());
    }

    @Transactional
    public UserResponseDto update(Long id, UpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This user does not exist."));

        user.update(dto.getUsername(), dto.getPassword());
        return new UserResponseDto(user.getUsername(), user.getEmail(), user.getPassword());
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
