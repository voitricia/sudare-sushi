package com.sudare.ifsc.services;

import com.sudare.ifsc.dtos.ProdutoDTO;
import com.sudare.ifsc.exceptions.NotFoundException;
import com.sudare.ifsc.model.Produto;
import com.sudare.ifsc.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    public Produto buscar(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado"));
    }

    public Produto criar (Produto p) {
        return produtoRepository.save(p);
    }

    public Produto atualizar(Long id, ProdutoDTO dto) {
        Produto produto = buscar(id);
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());
        produto.setAtivo(dto.ativo());
        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        Produto produto = buscar(id);
        produtoRepository.delete(produto);
    }
}
