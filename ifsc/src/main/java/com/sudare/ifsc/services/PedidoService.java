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
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository; // 1. INJEÇÃO ADICIONADA

    // 2. CONSTRUTOR ATUALIZADO
    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository,
                         ItemPedidoRepository itemPedidoRepository) { // Adicionado
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository; // Adicionado
    }

    // ... (métodos listar, buscar, criar(PedidoDTO), atualizarStatus, buscarFilaPreparo, buscarUltimosPedidos...)
    public List<Pedido> listar(){ 
        return pedidoRepository.findAll(); 
    }
    public Pedido buscar(Long id){ 
        return pedidoRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado")); 
    }
    // (Omitido por brevidade, mas seus métodos antigos continuam aqui)
    @Transactional
    public Pedido criar(PedidoDTO dto){
        // ... (seu método 'criar' completo)
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
    @Transactional
    public Pedido criarPedidoHeader(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setTotal(BigDecimal.ZERO);
        return pedidoRepository.save(pedido);
    }

    // --- 3. NOVOS MÉTODOS (LÓGICA DA ETAPA 2) ---

    /**
     * Adiciona um item a um pedido existente.
     * O preço unitário é pego do cadastro atual do produto.
     */
    @Transactional
    public Pedido adicionarItemAoPedido(Long pedidoId, Long produtoId, Integer quantidade) {
        // 1. Busca os objetos
        Pedido pedido = buscar(pedidoId);
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        // 2. Cria o novo item
        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco()); // Pega o preço atual do produto

        // 3. Adiciona via helper (que já recalcula o total)
        pedido.adicionarItem(item);

        // 4. Salva (Cascade.ALL salva o novo ItemPedido junto)
        return pedidoRepository.save(pedido);
    }

    /**
     * Remove um item de um pedido existente.
     */
    @Transactional
    public Pedido removerItemDoPedido(Long pedidoId, Long itemPedidoId) {
        // 1. Busca os objetos
        Pedido pedido = buscar(pedidoId);
        ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new NotFoundException("Item de pedido não encontrado"));

        // 2. Remove via helper (que já recalcula o total)
        pedido.removerItem(item);

        // 3. Salva (orphanRemoval=true deleta o ItemPedido do banco)
        return pedidoRepository.save(pedido);
    }
}