package com.sudare.ifsc.repositories;

import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection; // Importe java.util.Collection
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // (Você já deve ter este método)
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    Optional<Pedido> findByIdCompleto(@Param("id") Long id);

    // (Você já deve ter este método)
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente")
    List<Pedido> findAllWithCliente(Pageable pageable);

    // (Você já deve ter este método)
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.status = :status")
    List<Pedido> findAllByStatusWithCliente(@Param("status") StatusPedido status);


    // =================================================================
    // ▼▼▼ ADICIONE ESTE MÉTODO NOVO ▼▼▼
    // =================================================================
    
    /**
     * Busca pedidos por uma LISTA de status, com o cliente.
     */
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.status IN :statuses")
    List<Pedido> findAllByStatusInWithCliente(@Param("statuses") Collection<StatusPedido> statuses);
    
    // =================================================================
    // ▲▲▲ FIM DO MÉTODO NOVO ▲▲▲
    // =================================================================
}