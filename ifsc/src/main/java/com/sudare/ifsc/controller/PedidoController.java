package com.sudare.ifsc.controller;

import com.sudare.ifsc.dtos.PedidoDTO;
import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import com.sudare.ifsc.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService service;
    public PedidoController(PedidoService service){ 
        this.service = service; 
    }

    @GetMapping
    public List<Pedido> listar(){ 
        return service.listar(); 
    }

    @GetMapping("/{id}")
    public Pedido buscar(@PathVariable Long id){ 
        return service.buscar(id); 
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido criar(@RequestBody @Valid PedidoDTO dto){ 
        return service.criar(dto); 
    }

    @PatchMapping("/{id}/status")
    public Pedido atualizarStatus(@PathVariable Long id, @RequestParam StatusPedido status){
        return service.atualizarStatus(id, status);
    }    
}
