package com.sudare.ifsc.controller;

import com.sudare.ifsc.dtos.ProdutoDTO;
import com.sudare.ifsc.model.Produto;
import com.sudare.ifsc.services.ProdutoService;
import jakarta.validation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;
    public ProdutoController(ProdutoService produtoService){ 
        this.produtoService = produtoService; 
    }

    @GetMapping
    public List<Produto> listar(){ 
        return produtoService.listarProdutos(); 
    }

    @GetMapping("/{id}")
    public Produto buscar(@PathVariable Long id){ 
        return produtoService.buscar(id); 
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto criar(@RequestBody @Valid Produto p){ 
        return produtoService.criar(p); 
    }

    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoDTO dto){ 
        return produtoService.atualizar(id, dto); 
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){ 
        produtoService.deletar(id); 
    }
}