package com.sudare.ifsc.repositories;

import org.springframework.data.jpa.repository.JpaRepository; 
import com.sudare.ifsc.model.Produto; 

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
}
