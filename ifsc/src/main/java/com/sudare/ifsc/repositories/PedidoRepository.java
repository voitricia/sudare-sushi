package com.sudare.ifsc.repositories;

import org.springframework.data.jpa.repository.JpaRepository; 
import com.sudare.ifsc.model.Pedido; 

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

}
