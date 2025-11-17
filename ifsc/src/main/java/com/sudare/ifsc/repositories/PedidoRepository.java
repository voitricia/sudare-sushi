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

    // --- MÉTODOS EXISTENTES (Edição e Relatórios) ---
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    Optional<Pedido> findByIdCompleto(@Param("id") Long id);

    @Query("SELECT p FROM Pedido p " +
           "WHERE p.status = :status AND p.criadoEm BETWEEN :inicio AND :fim " +
           "ORDER BY p.criadoEm DESC")
    List<Pedido> findPedidosPorStatusEData(
        @Param("status") StatusPedido status,
        @Param("inicio") OffsetDateTime inicio,
        @Param("fim") OffsetDateTime fim
    );

    @Query("SELECT p FROM Pedido p " +
           "WHERE p.status = :status ORDER BY p.criadoEm DESC")
    List<Pedido> findAllPedidosPorStatus(@Param("status") StatusPedido status);

    
    // --- MÉTODOS ANTIGOS (Spring Data gerado) - Agora substituídos pelos de baixo ---
    // List<Pedido> findAllByStatusOrderByCriadoEmDesc(StatusPedido status, Pageable pageable);
    // List<Pedido> findAllByStatusInOrderByCriadoEmDesc(Collection<StatusPedido> statuses, Pageable pageable);
    // List<Pedido> findAllByOrderByCriadoEmDesc(Pageable pageable);


    // === NOVOS MÉTODOS PARA A HOME (COM JOIN FETCH) ===
    
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.produto prod " +
           "WHERE p.status = :status ORDER BY p.criadoEm DESC")
    List<Pedido> findHomeByStatusWithItems(@Param("status") StatusPedido status, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.produto prod " +
           "WHERE p.status IN :statuses ORDER BY p.criadoEm DESC")
    List<Pedido> findHomeByStatusInWithItems(@Param("statuses") Collection<StatusPedido> statuses, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.produto prod " +
           "ORDER BY p.criadoEm DESC")
    List<Pedido> findHomeAllWithItems(Pageable pageable);

}