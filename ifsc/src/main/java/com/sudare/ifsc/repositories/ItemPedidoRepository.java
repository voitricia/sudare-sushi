package com.sudare.ifsc.repositories;

import com.sudare.ifsc.dtos.ItemTopDTO; 
import com.sudare.ifsc.model.ItemPedido;
import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param; 
import java.time.OffsetDateTime;
import java.util.List; 

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    @Query("SELECT new com.sudare.ifsc.dtos.ItemTopDTO(i.produto.nome, SUM(i.quantidade)) " +
           "FROM ItemPedido i JOIN i.pedido p " +
           "WHERE p.criadoEm >= :inicioDoDia " +
           "GROUP BY i.produto.nome " +
           "ORDER BY SUM(i.quantidade) DESC")
    List<ItemTopDTO> findTopSellingItems(@Param("inicioDoDia") OffsetDateTime inicioDoDia, Pageable pageable);
}