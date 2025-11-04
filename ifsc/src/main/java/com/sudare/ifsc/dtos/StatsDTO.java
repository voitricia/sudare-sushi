package com.sudare.ifsc.dtos;

import java.math.BigDecimal;

public record StatsDTO(
        Long pedidosHoje,
        BigDecimal faturamento,
        Long emPreparo,
        String itemTop,
        Long itemTopQtd
) {
}