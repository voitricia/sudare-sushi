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

    /**
     * Método para buscar Pedido com Itens (usado em 'buscarCompletoParaEdicao')
     */
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    Optional<Pedido> findByIdCompleto(@Param("id") Long id);

    /**
     * Método para 'buscarUltimosPedidos'
     */
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente")
    List<Pedido> findAllWithCliente(Pageable pageable);

    /**
     * Método para 'buscarFilaPreparo' (usando EM_PREPARO e PRONTO)
     */
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.status IN :statuses")
    List<Pedido> findAllByStatusInWithCliente(@Param("statuses") Collection<StatusPedido> statuses);

    /**
     * Método para Relatório: Busca pedidos FINALIZADOS num intervalo de datas.
     */
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente c " +
           "WHERE p.status = :status AND p.criadoEm BETWEEN :inicio AND :fim " +
           "ORDER BY p.criadoEm DESC")
    List<Pedido> findPedidosPorStatusEData(
        @Param("status") StatusPedido status,
        @Param("inicio") OffsetDateTime inicio,
        @Param("fim") OffsetDateTime fim
    );

    /**
     * Método para Relatório: Busca TODOS os pedidos FINALIZADOS (para o filtro "Tudo").
     */
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente c " +
           "WHERE p.status = :status ORDER BY p.criadoEm DESC")
    List<Pedido> findAllPedidosPorStatus(@Param("status") StatusPedido status);
    
}