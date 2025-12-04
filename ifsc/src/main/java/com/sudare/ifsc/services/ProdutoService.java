package com.sudare.ifsc.services;

import com.sudare.ifsc.dtos.ProdutoDTO;
import com.sudare.ifsc.exceptions.NotFoundException;
import com.sudare.ifsc.model.Produto;
import com.sudare.ifsc.repositories.ProdutoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Serviço para gerenciar operações relacionadas a produtos.
@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll(Sort.by("categoria", "nome"));
    }

    @Transactional(readOnly = true) // Lista produtos agrupados por categoria em uma ordem específica.
    public Map<String, List<Produto>> listarProdutosAgrupados() {
        List<String> ordemCategorias = List.of(
            "ENTRADAS", "SASHIMIS", "HOSSOMAKIS", "URAMAKIS", "FUTOMAKIS", 
            "NIGUIRIS", "GUNKANS", "COMBINADOS", "HOTS", "TEMAKIS", "BEBIDAS"
        );

        Sort sort = Sort.by("nome"); // Ordena os produtos por nome dentro de cada categoria.
        List<Produto> produtos = produtoRepository.findAll(sort);

        Map<String, List<Produto>> grouped = produtos.stream()
            .collect(Collectors.groupingBy(Produto::getCategoria));

        Map<String, List<Produto>> produtosAgrupados = new LinkedHashMap<>(); // Mantém a ordem de inserção.
        for (String categoria : ordemCategorias) {
            if (grouped.containsKey(categoria)) {
                produtosAgrupados.put(categoria, grouped.get(categoria));
            }
        }
        
        grouped.keySet().forEach(categoria -> { // Adiciona categorias não listadas na ordem específica.
            if (!produtosAgrupados.containsKey(categoria)) {
                produtosAgrupados.put(categoria, grouped.get(categoria));
            }
        });
        
        return produtosAgrupados;
    }

    // Busca um produto pelo ID, lançando uma exceção se não for encontrado.
    public Produto buscar(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado"));
    }

    public Produto criar (ProdutoDTO dto) {
        Produto p = new Produto();
        p.setNome(dto.nome());
        p.setPreco(dto.preco());
        p.setAtivo(dto.ativo());
        p.setCategoria(dto.categoria());
        return produtoRepository.save(p);
    }

    public Produto atualizar(Long id, ProdutoDTO dto) {
        Produto produto = buscar(id);
        produto.setNome(dto.nome());
        produto.setPreco(dto.preco());
        produto.setAtivo(dto.ativo());
        produto.setCategoria(dto.categoria());
        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        Produto produto = buscar(id);
        produtoRepository.delete(produto);
    }

    // Busca um produto pelo ID e retorna um DTO com seus dados.
    public ProdutoDTO buscarDTO(Long id) {
        Produto p = buscar(id);
        return new ProdutoDTO(
                p.getId(),
                p.getNome(),
                p.getCategoria(),
                p.getPreco(),
                p.isAtivo()
        );
    }

    @Transactional // Ativa ou desativa um produto com base no ID fornecido.
    public Produto atualizarAtivo(Long id, boolean ativo) {
        Produto produto = buscar(id);
        produto.setAtivo(ativo);
        return produtoRepository.save(produto);
    }
}