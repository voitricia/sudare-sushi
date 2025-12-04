package com.sudare.ifsc.dtos;

import java.math.BigDecimal;

// DTO usado para representar um item dentro de um pedido.
// Carrega apenas os dados necessários para criar ou atualizar um item.
public record ItemPedidoDTO(Long produtoId, Integer quantidade, BigDecimal precoUnitario) {

}