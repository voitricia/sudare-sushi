package com.sudare.ifsc.dtos;
import com.sudare.ifsc.model.Pedido;
import java.math.BigDecimal;
import java.util.List;

public record RelatorioDTO(
    BigDecimal faturamentoTotal,
    Long numeroDePedidos,
    BigDecimal ticketMedio,
    List<Pedido> pedidos
) {}