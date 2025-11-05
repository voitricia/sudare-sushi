package com.sudare.ifsc.services;

import com.sudare.ifsc.dtos.ItemTopDTO;
import com.sudare.ifsc.dtos.StatsDTO;
import com.sudare.ifsc.model.StatusPedido;
import com.sudare.ifsc.repositories.ItemPedidoRepository;
import com.sudare.ifsc.repositories.PedidoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DashboardService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public DashboardService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    /**
     * Busca e calcula todas as estatísticas para o dashboard da Home.
     */
    @Transactional(readOnly = true)
    public StatsDTO getDashboardStats() {
        // Define o início do "dia de hoje" (meia-noite)
        OffsetDateTime inicioDoDia = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);

        // 1. Pedidos Hoje (COUNT)
        Long pedidosHoje = pedidoRepository.countByCriadoEmAfter(inicioDoDia);

        // 2. Faturamento (SUM)
        BigDecimal faturamento = pedidoRepository.sumTotalByCriadoEmAfter(inicioDoDia);

        // 3. Em Preparo (COUNT por Status)
        Long emPreparo = pedidoRepository.countByStatus(StatusPedido.EM_PREPARO);

        // 4. Item Top (GROUP BY e LIMIT 1)
        List<ItemTopDTO> topItems = itemPedidoRepository.findTopSellingItems(inicioDoDia, PageRequest.of(0, 1));
        
        String itemTopNome = "–"; // Valor padrão (hífen)
        Long itemTopQtd = 0L;
        
        if (!topItems.isEmpty()) {
            itemTopNome = topItems.get(0).nome();
            itemTopQtd = topItems.get(0).quantidade();
        }

        // 5. Monta o DTO final e retorna
        return new StatsDTO(pedidosHoje, faturamento, emPreparo, itemTopNome, itemTopQtd);
    }
}