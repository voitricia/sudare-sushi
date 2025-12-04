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

// Repositório para operações de banco de dados relacionadas aos pedidos.
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

// Consulta personalizada para buscar um pedido completo com seus itens pelo ID.
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    Optional<Pedido> findByIdCompleto(@Param("id") Long id);

    // Consulta personalizada para buscar pedidos por status e intervalo de datas.
    @Query("SELECT p FROM Pedido p " +
           "WHERE p.status = :status AND p.criadoEm BETWEEN :inicio AND :fim " +
           "ORDER BY p.criadoEm DESC")
    List<Pedido> findPedidosPorStatusEData(
        @Param("status") StatusPedido status,
        @Param("inicio") OffsetDateTime inicio,
        @Param("fim") OffsetDateTime fim
    );

    // Consulta personalizada para buscar todos os pedidos por status.
    @Query("SELECT p FROM Pedido p " +
           "WHERE p.status = :status ORDER BY p.criadoEm DESC")
    List<Pedido> findAllPedidosPorStatus(@Param("status") StatusPedido status);
    
    // Consultas personalizadas para a tela inicial com diferentes filtros de status.
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.produto prod " +
           "WHERE p.status = :status ORDER BY p.criadoEm DESC")
    List<Pedido> findHomeByStatusWithItems(@Param("status") StatusPedido status, Pageable pageable);

    // Consulta personalizada para a tela inicial com múltiplos status.
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.produto prod " +
           "WHERE p.status IN :statuses ORDER BY p.criadoEm DESC")
    List<Pedido> findHomeByStatusInWithItems(@Param("statuses") Collection<StatusPedido> statuses, Pageable pageable);

    // Consulta personalizada para a tela inicial sem filtro de status.
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.produto prod " +
           "ORDER BY p.criadoEm DESC")
    List<Pedido> findHomeAllWithItems(Pageable pageable);

}