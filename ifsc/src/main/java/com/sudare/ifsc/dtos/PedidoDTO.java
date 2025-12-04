package com.sudare.ifsc.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

// DTO que representa um pedido completo na API.
public record PedidoDTO(
    Long id, 
    Long clienteId, 
    List<ItemPedidoDTO> itens, 
    String status, 
    OffsetDateTime criadoEm, 
    BigDecimal total
) {

}