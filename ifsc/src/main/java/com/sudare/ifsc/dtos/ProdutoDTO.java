package com.sudare.ifsc.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

// DTO utilizado para criar ou editar produtos.
// Inclui validações para garantir que nome, categoria e preço sejam informados corretamente.
public record ProdutoDTO(
        Long id,

        @NotBlank(message = "O nome é obrigatório")
        String nome,
        
        @NotBlank(message = "A categoria é obrigatória")
        String categoria,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que R$ 0,00")
        BigDecimal preco,

        boolean ativo
) {
}