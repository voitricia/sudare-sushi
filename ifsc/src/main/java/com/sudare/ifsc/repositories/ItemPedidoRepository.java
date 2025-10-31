package com.sudare.ifsc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sudare.ifsc.model.ItemPedido;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    
}
