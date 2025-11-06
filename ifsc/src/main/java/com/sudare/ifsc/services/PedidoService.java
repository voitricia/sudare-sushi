package com.sudare.ifsc.services;

import com.sudare.ifsc.dtos.ItemPedidoDTO;
import com.sudare.ifsc.dtos.PedidoDTO;
import com.sudare.ifsc.exceptions.NotFoundException;
import com.sudare.ifsc.model.*;
import com.sudare.ifsc.repositories.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {
    // Note que não precisamos mais do ClienteService, só do Repositório
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository,
                         ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    // ... (métodos listar, buscar, criar(DTO), atualizarStatus, buscarFilaPreparo, buscarUltimosPedidos) ...
    // (Omitidos por brevidade, mas eles continuam aqui)
    public List<Pedido> listar(){ 
        return pedidoRepository.findAll(); 
    }
    public Pedido buscar(Long id){ 
        return pedidoRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado")); 
    }

    @Transactional(readOnly = true)
    public Pedido buscarCompletoParaEdicao(Long id) {
        return pedidoRepository.findByIdCompleto(id)
            .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));
    }

    @Transactional
    public Pedido criar(PedidoDTO dto){
        Cliente cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        for(ItemPedidoDTO itemDto : dto.itens()){
            Produto produto = produtoRepository.findById(itemDto.produtoId()).orElseThrow(() -> new NotFoundException("Produto não encontrado: ID " + itemDto.produtoId()));
            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDto.quantidade());
            item.setPrecoUnitario(itemDto.precoUnitario()); 
            pedido.adicionarItem(item);
        }
        return pedidoRepository.save(pedido);
    }
    @Transactional
    public Pedido atualizarStatus(Long id, StatusPedido status){
        Pedido p = buscar(id);
        p.setStatus(status);
        return pedidoRepository.save(p);
    }
    @Transactional(readOnly = true)
    public List<Pedido> buscarFilaPreparo() {
        return pedidoRepository.findAllByStatusWithCliente(StatusPedido.EM_PREPARO);
    }
    @Transactional(readOnly = true)
    public List<Pedido> buscarUltimosPedidos(int limite) {
        Pageable pageable = PageRequest.of(0, limite, Sort.by("criadoEm").descending());
        return pedidoRepository.findAllWithCliente(pageable);
    }
    
    
    // --- MÉTODO DE CRIAÇÃO ÚNICO ---
    
    /**
     * MÉTODO RENOMEADO (era criarPedidoBalcao)
     * Este é agora o ÚNICO método para criar um pedido.
     * Salva o nome/observação e associa ao cliente "Consumidor Final".
     */
    @Transactional
    public Pedido criarNovoPedido(String nomeObservacao) {
        // 1. Busca o cliente padrão.
        Cliente clientePadrao = clienteRepository.findByNome("Consumidor Final")
                .orElseThrow(() -> new RuntimeException("Cliente 'Consumidor Final' não encontrado."));

        // 2. Cria o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(clientePadrao); // Associa ao cliente padrão
        pedido.setNomeClienteObservacao(nomeObservacao); // Salva a observação
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setTotal(BigDecimal.ZERO);

        return pedidoRepository.save(pedido);
    }
    
    // --- (métodos adicionarItemAoPedido e removerItemDoPedido continuam aqui) ---
    @Transactional
    public Pedido adicionarItemAoPedido(Long pedidoId, Long produtoId, Integer quantidade) {
        Pedido pedido = buscar(pedidoId);
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));
        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        pedido.adicionarItem(item);
        return pedidoRepository.save(pedido);
    }
    @Transactional
    public Pedido removerItemDoPedido(Long pedidoId, Long itemPedidoId) {
        Pedido pedido = buscar(pedidoId);
        ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new NotFoundException("Item de pedido não encontrado"));
        pedido.removerItem(item);
        return pedidoRepository.save(pedido);
    }
}