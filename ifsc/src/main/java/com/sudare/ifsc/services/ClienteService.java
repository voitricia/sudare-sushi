package com.sudare.ifsc.services;

import com.sudare.ifsc.dtos.ClienteDTO;
import com.sudare.ifsc.exceptions.NotFoundException;
import com.sudare.ifsc.model.Cliente;
import com.sudare.ifsc.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    public ClienteService(ClienteRepository clienteRepository){ this.clienteRepository = clienteRepository; }

    public List<Cliente> listar(){ 
        return clienteRepository.findAll(); 
    }

    public Cliente buscar(Long id){ 
        return clienteRepository.findById(id).orElseThrow(() -> new NotFoundException("Cliente não encontrado")); 
    }

    public Cliente criar(Cliente c){ 
        return clienteRepository.save(c); 
    }

    public Cliente atualizar(Long id, ClienteDTO dto){
        Cliente c = buscar(id);
        c.setNome(dto.nome());
        c.setEmail(dto.email());
        c.setTelefone(dto.telefone());
        return repo.save(c);
    }
    
    public void deletar(Long id){ 
        clienteRepository.delete(buscar(id)); 
    }
}
