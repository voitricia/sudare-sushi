package com.sudare.ifsc.repositories;

import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal; // Importar
import java.time.OffsetDateTime; // Importar
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // --- (Métodos que já tínhamos) ---
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente c WHERE p.status = :status ORDER BY p.criadoEm ASC")
    List<Pedido> findAllByStatusWithCliente(@Param("status") StatusPedido status);

    @Query(value = "SELECT p FROM Pedido p JOIN FETCH p.cliente c",
           countQuery = "SELECT COUNT(p) FROM Pedido p")
    List<Pedido> findAllWithCliente(Pageable pageable);


    // --- (NOVOS MÉTODOS PARA O DASHBOARD) ---

    /**
     * Conta quantos pedidos existem com um determinado status.
     * (O Spring Data JPA cria a query sozinho)
     */
    Long countByStatus(StatusPedido status);

    /**
     * Conta quantos pedidos foram criados DEPOIS de uma data/hora.
     */
    Long countByCriadoEmAfter(OffsetDateTime inicioDoDia);

    /**
     * Soma o TOTAL de todos os pedidos criados DEPOIS de uma data/hora.
     * COALESCE garante que retorne 0 em vez de NULL se não houver pedidos.
     */
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.criadoEm >= :inicioDoDia")
    BigDecimal sumTotalByCriadoEmAfter(@Param("inicioDoDia") OffsetDateTime inicioDoDia);
}