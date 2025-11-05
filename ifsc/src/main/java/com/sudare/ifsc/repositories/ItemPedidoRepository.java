package com.sudare.ifsc.repositories;

import com.sudare.ifsc.dtos.ItemTopDTO; // Importar
import com.sudare.ifsc.model.ItemPedido;
import org.springframework.data.domain.Pageable; // Importar
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importar
import org.springframework.data.repository.query.Param; // Importar

import java.time.OffsetDateTime; // Importar
import java.util.List; // Importar

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    /**
     * Busca o item mais vendido (SUM(quantidade)) de hoje.
     * Agrupa por produto, ordena pela soma e retorna o DTO (ItemTopDTO).
     */
    @Query("SELECT new com.sudare.ifsc.dtos.ItemTopDTO(i.produto.nome, SUM(i.quantidade)) " +
           "FROM ItemPedido i JOIN i.pedido p " +
           "WHERE p.criadoEm >= :inicioDoDia " +
           "GROUP BY i.produto.nome " +
           "ORDER BY SUM(i.quantidade) DESC")
    List<ItemTopDTO> findTopSellingItems(@Param("inicioDoDia") OffsetDateTime inicioDoDia, Pageable pageable);
}