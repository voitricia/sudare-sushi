package com.sudare.ifsc.repositories;

import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal; 
import java.time.OffsetDateTime; 
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente c WHERE p.status = :status ORDER BY p.criadoEm ASC")
    List<Pedido> findAllByStatusWithCliente(@Param("status") StatusPedido status);

    @Query(value = "SELECT p FROM Pedido p JOIN FETCH p.cliente c",
           countQuery = "SELECT COUNT(p) FROM Pedido p")
    List<Pedido> findAllWithCliente(Pageable pageable);


    Long countByStatus(StatusPedido status);


    Long countByCriadoEmAfter(OffsetDateTime inicioDoDia);


    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.criadoEm >= :inicioDoDia")
    BigDecimal sumTotalByCriadoEmAfter(@Param("inicioDoDia") OffsetDateTime inicioDoDia);

    @Query("SELECT p FROM Pedido p " +
           "LEFT JOIN FETCH p.cliente c " +
           "LEFT JOIN FETCH p.itens i " +
           "LEFT JOIN FETCH i.produto prod " +
           "WHERE p.id = :id")
    Optional<Pedido> findByIdCompleto(@Param("id") Long id);
}