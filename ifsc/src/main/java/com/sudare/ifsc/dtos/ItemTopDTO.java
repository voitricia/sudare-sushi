package com.sudare.ifsc.dtos;

// DTO usado para relatório dos itens mais vendidos.
// Armazena apenas o nome do produto e a quantidade total vendida.
public record ItemTopDTO(String nome, Long quantidade) {
    
}
