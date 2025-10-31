package com.sudare.ifsc.dtos; 

import java.math.BigDecimal; 

public record ProdutoDTO(Long id, String nome, String descricao, BigDecimal preco, Integer estoque, boolean ativo) {
    
}