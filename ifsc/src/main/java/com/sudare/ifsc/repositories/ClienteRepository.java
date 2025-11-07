package com.sudare.ifsc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sudare.ifsc.model.Cliente;
import java.util.Optional; 

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByNome(String nome);
}