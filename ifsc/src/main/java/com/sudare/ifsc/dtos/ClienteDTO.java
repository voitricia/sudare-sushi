package com.sudare.ifsc.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteDTO(
        Long id,

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @Email(message = "Formato de e-mail inválido")
        String email,

        String telefone
) {
}