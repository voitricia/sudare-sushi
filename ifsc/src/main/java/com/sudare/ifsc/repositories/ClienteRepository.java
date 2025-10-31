package com.sudare.ifsc.repositories;

import org.springframework.data.jpa.repository.JpaRepository; 
import com.sudare.ifsc.model.Cliente; 

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
}
