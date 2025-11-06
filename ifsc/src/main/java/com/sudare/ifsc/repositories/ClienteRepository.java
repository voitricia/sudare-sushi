package com.sudare.ifsc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sudare.ifsc.model.Cliente;
import java.util.Optional; // <-- ADICIONE ESTE IMPORT

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * ADICIONE ESTE MÉTODO
     * Busca um cliente pelo seu nome exato.
     * Usado para encontrar o "Consumidor Final"
     */
    Optional<Cliente> findByNome(String nome);
}