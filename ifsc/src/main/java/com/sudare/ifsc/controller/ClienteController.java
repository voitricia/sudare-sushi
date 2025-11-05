package com.sudare.ifsc.controller;

import com.sudare.ifsc.dtos.ClienteDTO;
import com.sudare.ifsc.model.Cliente;
import com.sudare.ifsc.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService clienteService;
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Long id){
        return clienteService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // CORRIGIDO: Recebe ClienteDTO, não a entidade Cliente
    public Cliente criar(@RequestBody @Valid ClienteDTO dto){
        return clienteService.criar(dto);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @RequestBody @Valid ClienteDTO dto){
        return clienteService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        clienteService.deletar(id);
    }
}