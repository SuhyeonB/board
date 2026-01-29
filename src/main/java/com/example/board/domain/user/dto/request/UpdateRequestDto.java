package com.example.board.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UpdateRequestDto {
    private String oldPassword;
    private String newPassword;
}
