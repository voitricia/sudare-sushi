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

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarProdutos() {
        // Ordena por categoria e depois por nome
        return produtoRepository.findAll(Sort.by("categoria", "nome"));
    }

    @Transactional(readOnly = true)
    public Map<String, List<Produto>> listarProdutosAgrupados() {
        // 1. Ordem correta das categorias (COM BEBIDAS NO FINAL)
        List<String> ordemCategorias = List.of(
            "ENTRADAS", "SASHIMIS", "HOSSOMAKIS", "URAMAKIS", "FUTOMAKIS", 
            "NIGUIRIS", "GUNKANS", "COMBINADOS", "HOTS", "TEMAKIS", "BEBIDAS"
        );

        // 2. Busca produtos ordenados por nome
        Sort sort = Sort.by("nome");
        List<Produto> produtos = produtoRepository.findAll(sort);

        // 3. Agrupa por categoria
        Map<String, List<Produto>> grouped = produtos.stream()
            .collect(Collectors.groupingBy(Produto::getCategoria));

        // 4. Cria o mapa final na ordem correta
        Map<String, List<Produto>> produtosAgrupados = new LinkedHashMap<>();
        for (String categoria : ordemCategorias) {
            if (grouped.containsKey(categoria)) {
                produtosAgrupados.put(categoria, grouped.get(categoria));
            }
        }
        
        // Adiciona quaisquer outras categorias não listadas (caso existam)
        grouped.keySet().forEach(categoria -> {
            if (!produtosAgrupados.containsKey(categoria)) {
                produtosAgrupados.put(categoria, grouped.get(categoria));
            }
        });
        
        return produtosAgrupados;
    }

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

    @Transactional
    public Produto atualizarAtivo(Long id, boolean ativo) {
        Produto produto = buscar(id);
        produto.setAtivo(ativo);
        return produtoRepository.save(produto);
    }
}