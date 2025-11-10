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

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    Optional<Pedido> findByIdCompleto(@Param("id") Long id);

    // ATUALIZADO: Removido "JOIN FETCH p.cliente"
    @Query("SELECT p FROM Pedido p")
    List<Pedido> findAllWithCliente(Pageable pageable);

    // ATUALIZADO: Removido "JOIN FETCH p.cliente"
    @Query("SELECT p FROM Pedido p WHERE p.status IN :statuses")
    List<Pedido> findAllByStatusInWithCliente(@Param("statuses") Collection<StatusPedido> statuses);

    // ATUALIZADO: Removido "JOIN FETCH p.cliente c"
    @Query("SELECT p FROM Pedido p " +
           "WHERE p.status = :status AND p.criadoEm BETWEEN :inicio AND :fim " +
           "ORDER BY p.criadoEm DESC")
    List<Pedido> findPedidosPorStatusEData(
        @Param("status") StatusPedido status,
        @Param("inicio") OffsetDateTime inicio,
        @Param("fim") OffsetDateTime fim
    );

    // ATUALIZADO: Removido "JOIN FETCH p.cliente c"
    @Query("SELECT p FROM Pedido p " +
           "WHERE p.status = :status ORDER BY p.criadoEm DESC")
    List<Pedido> findAllPedidosPorStatus(@Param("status") StatusPedido status);
    
}