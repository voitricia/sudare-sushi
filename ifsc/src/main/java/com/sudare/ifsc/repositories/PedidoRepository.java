package com.sudare.ifsc.repositories;

import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Para a página de Edição
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    Optional<Pedido> findByIdCompleto(@Param("id") Long id);

    // Para os Relatórios
    @Query("SELECT p FROM Pedido p " +
           "WHERE p.status = :status AND p.criadoEm BETWEEN :inicio AND :fim " +
           "ORDER BY p.criadoEm DESC")
    List<Pedido> findPedidosPorStatusEData(
        @Param("status") StatusPedido status,
        @Param("inicio") OffsetDateTime inicio,
        @Param("fim") OffsetDateTime fim
    );

    // Para os Relatórios
    @Query("SELECT p FROM Pedido p " +
           "WHERE p.status = :status ORDER BY p.criadoEm DESC")
    List<Pedido> findAllPedidosPorStatus(@Param("status") StatusPedido status);

    
    // ==========================================================
    // === MÉTODOS NOVOS QUE CORRIGEM O ERRO (image_954616.png) ===
    // ==========================================================
    
    /**
     * Busca pedidos por UM status, ordenado por data e com paginação
     */
    List<Pedido> findAllByStatusOrderByCriadoEmDesc(StatusPedido status, Pageable pageable);

    /**
     * Busca pedidos por VÁRIOS status, ordenado por data e com paginação
     */
    List<Pedido> findAllByStatusInOrderByCriadoEmDesc(Collection<StatusPedido> statuses, Pageable pageable);

}