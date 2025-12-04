package com.sudare.ifsc.dtos;

import com.sudare.ifsc.model.Pedido;

import java.math.BigDecimal;
import java.util.List;

// DTO para relatório de pedidos.
public record RelatorioDTO(
    BigDecimal faturamentoTotal,
    Long numeroDePedidos,
    List<Pedido> pedidos
) {}