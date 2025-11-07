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

    @Transactional(readOnly = true)
    public StatsDTO getDashboardStats() {
        OffsetDateTime inicioDoDia = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);

        Long pedidosHoje = pedidoRepository.countByCriadoEmAfter(inicioDoDia);

        BigDecimal faturamento = pedidoRepository.sumTotalByCriadoEmAfter(inicioDoDia);

        Long emPreparo = pedidoRepository.countByStatus(StatusPedido.EM_PREPARO);

        List<ItemTopDTO> topItems = itemPedidoRepository.findTopSellingItems(inicioDoDia, PageRequest.of(0, 1));
        
        String itemTopNome = "–"; 
        Long itemTopQtd = 0L;
        
        if (!topItems.isEmpty()) {
            itemTopNome = topItems.get(0).nome();
            itemTopQtd = topItems.get(0).quantidade();
        }

        return new StatsDTO(pedidosHoje, faturamento, emPreparo, itemTopNome, itemTopQtd);
    }
}