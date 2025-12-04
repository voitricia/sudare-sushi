package com.sudare.ifsc.controller;

import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import com.sudare.ifsc.services.PedidoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Controller REST responsável por expor a API de pedidos
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    // Service que contém toda a regra de negócio dos pedidos
    private final PedidoService service;

    // Injeção via construtor
    public PedidoController(PedidoService service){ 
        this.service = service; 
    }

    // Retorna a lista de todos os pedidos cadastrados
    @GetMapping
    public List<Pedido> listar(){ 
        return service.listar(); 
    }

    // Busca um pedido específico pelo ID
    @GetMapping("/{id}")
    public Pedido buscar(@PathVariable Long id){ 
        return service.buscar(id); 
    }

    // Atualiza somente o status do pedido (ABERTO, EM_PREPARO, FINALIZADO...)
    @PatchMapping("/{id}/status")
    public Pedido atualizarStatus(@PathVariable Long id, @RequestParam StatusPedido status){
        return service.atualizarStatus(id, status);
    } 
}
