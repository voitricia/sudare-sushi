package com.sudare.ifsc.repositories;

import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import org.springframework.data.domain.Pageable; // Importar
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importar
import org.springframework.data.repository.query.Param; // Importar
import java.util.List; // Importar

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca pedidos por status, já trazendo o Cliente junto (JOIN FETCH)
     * para evitar N+1 queries na view.
     * Ordena pelos mais antigos (ASC) para a fila.
     */
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente c WHERE p.status = :status ORDER BY p.criadoEm ASC")
    List<Pedido> findAllByStatusWithCliente(@Param("status") StatusPedido status);

    /**
     * Busca todos os pedidos, trazendo o Cliente junto (JOIN FETCH)
     * e usando Pageable para limitar (ex: "Top 5").
     * O countQuery é importante para paginação com JOIN FETCH.
     */
    @Query(value = "SELECT p FROM Pedido p JOIN FETCH p.cliente c",
           countQuery = "SELECT COUNT(p) FROM Pedido p")
    List<Pedido> findAllWithCliente(Pageable pageable);
}