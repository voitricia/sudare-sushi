package com.sudare.ifsc.dtos;

import java.math.BigDecimal;

public record ItemPedidoDTO(Long produtoId, Integer quantidade, BigDecimal precoUnitario) {

}