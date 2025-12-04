package com.sudare.ifsc.controller;

import com.sudare.ifsc.dtos.ProdutoDTO;
import com.sudare.ifsc.model.Produto;
import com.sudare.ifsc.services.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Controller REST responsável por expor a API de produtos
@RestController
@RequestMapping("/api/produtos") 
public class ProdutoController {

    // Service que contém as regras de negócio relacionadas aos produtos
    private final ProdutoService produtoService;

    // Injeção via construtor
    public ProdutoController(ProdutoService produtoService){
        this.produtoService = produtoService;
    }

    // Retorna todos os produtos cadastrados
    @GetMapping
    public List<Produto> listar(){
        return produtoService.listarProdutos();
    }

    // Busca um produto específico pelo ID
    @GetMapping("/{id}")
    public Produto buscar(@PathVariable Long id){
        return produtoService.buscar(id);
    }

    // Cria um novo produto a partir de um DTO enviado no corpo da requisição
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Retorna 201 Created
    public Produto criar(@RequestBody @Validated ProdutoDTO dto){
        return produtoService.criar(dto);
    }

    // Atualiza um produto existente usando o ID e os dados enviados no DTO
    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Long id, @RequestBody @Validated ProdutoDTO dto){
        return produtoService.atualizar(id, dto);
    }

    // Remove um produto do sistema pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna 204 sem corpo
    public void deletar(@PathVariable Long id){
        produtoService.deletar(id);
    }
    
    // Atualiza apenas o campo "ativo" do produto (PATCH = atualização parcial)
    @PatchMapping("/{id}/ativo")
    public Produto atualizarAtivo(@PathVariable Long id, @RequestParam boolean ativo) {
        return produtoService.atualizarAtivo(id, ativo);
    }
}
