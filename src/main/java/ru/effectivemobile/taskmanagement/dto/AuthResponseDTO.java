package ru.effectivemobile.taskmanagement.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
public class AuthResponseDTO {
    private String token;
}
