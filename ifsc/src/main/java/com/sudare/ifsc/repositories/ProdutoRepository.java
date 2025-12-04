package com.sudare.ifsc.repositories;

import org.springframework.data.jpa.repository.JpaRepository; 
import com.sudare.ifsc.model.Produto; 

// Repositório para operações de banco de dados relacionadas aos produtos.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
}
