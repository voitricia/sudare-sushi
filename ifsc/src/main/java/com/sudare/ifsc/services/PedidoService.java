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

    // ==========================================================
    // === MÉTODO QUE FALTAVA (PARA O PedidoController DA API) ===
    // ==========================================================
    @Transactional
    public Pedido criar(PedidoDTO dto){
        Cliente cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        
        // (Este DTO precisa ter uma lista de itens)
        if (dto.itens() != null) {
            for(ItemPedidoDTO itemDto : dto.itens()){
                Produto produto = produtoRepository.findById(itemDto.produtoId()).orElseThrow(() -> new NotFoundException("Produto não encontrado: ID " + itemDto.produtoId()));
                ItemPedido item = new ItemPedido();
                item.setProduto(produto);
                item.setQuantidade(itemDto.quantidade());
                item.setPrecoUnitario(itemDto.precoUnitario()); 
                pedido.adicionarItem(item);
            }
        }
        recalcularTotalPedido(pedido); // Garante que o total seja calculado
        return pedidoRepository.save(pedido);
    }
    // ==========================================================

    @Transactional
    public Pedido atualizarStatus(Long id, StatusPedido status){
        Pedido p = buscar(id);
        p.setStatus(status);
        return pedidoRepository.save(p);
    }
    @Transactional(readOnly = true)
    public List<Pedido> buscarFilaPreparo() {
        return pedidoRepository.findAllByStatusInWithCliente(
            List.of(StatusPedido.EM_PREPARO, StatusPedido.PRONTO)
        );
    }
    @Transactional(readOnly = true)
    public List<Pedido> buscarUltimosPedidos(int limite) {
        Pageable pageable = PageRequest.of(0, limite, Sort.by("criadoEm").descending());
        return pedidoRepository.findAllWithCliente(pageable);
    }

    /**
     * Este método é usado pelo PagesController (o formulário web)
     */
    @Transactional
    public Pedido criarNovoPedido(String nomeObservacao) {
        Cliente clientePadrao = clienteRepository.findByNome("Consumidor Final")
                .orElseThrow(() -> new RuntimeException("Cliente 'Consumidor Final' não encontrado."));

        Pedido pedido = new Pedido();
        pedido.setCliente(clientePadrao);
        pedido.setNomeClienteObservacao(nomeObservacao);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setTotal(BigDecimal.ZERO);

        return pedidoRepository.save(pedido);
    }
    
    @Transactional
    public Pedido adicionarItemAoPedido(Long pedidoId, Long produtoId, Integer quantidade) {
        Pedido pedido = buscarCompletoParaEdicao(pedidoId);
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));
        
        for (ItemPedido itemExistente : pedido.getItens()) {
            if (itemExistente.getProduto().getId().equals(produtoId)) {
                itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidade);
                recalcularTotalPedido(pedido);
                return pedido;
            }
        }

        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        pedido.adicionarItem(item);
        
        recalcularTotalPedido(pedido);
        return pedido; 
    }

    @Transactional
    public Pedido removerItemDoPedido(Long pedidoId, Long itemPedidoId) {
        Pedido pedido = buscarCompletoParaEdicao(pedidoId);
        ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new NotFoundException("Item de pedido não encontrado"));
        
        pedido.removerItem(item);
        itemPedidoRepository.delete(item); 
        
        recalcularTotalPedido(pedido);
        return pedido;
    }

    /**
     * Este método é usado pelo PagesController (edição de item na linha)
     */
    @Transactional
    public void atualizarItemQuantidade(Long itemPedidoId, Integer quantidade) {
        if (quantidade < 1) {
             ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new NotFoundException("Item de pedido não encontrado"));
             removerItemDoPedido(item.getPedido().getId(), itemPedidoId);
             return;
        }

        ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new NotFoundException("Item de pedido não encontrado"));

        item.setQuantidade(quantidade);
        itemPedidoRepository.save(item);

        recalcularTotalPedido(item.getPedido());
    }

    private void recalcularTotalPedido(Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;
        
        // Se o pedido foi salvo agora, pode não ter itens carregados
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
             // Se não tiver itens (ex: na criação do DTO), o total é zero
             if (pedido.getId() == null) {
                 pedido.setTotal(BigDecimal.ZERO);
                 return;
             }
             // Se já existe, recarrega
             Pedido pedidoComItens = buscarCompletoParaEdicao(pedido.getId());
             for (ItemPedido i : pedidoComItens.getItens()) {
                 total = total.add(i.getSubtotal());
             }
             pedidoComItens.setTotal(total);
             pedidoRepository.save(pedidoComItens);
        } else {
             // Se os itens já estão carregados
             for (ItemPedido i : pedido.getItens()) {
                 total = total.add(i.getSubtotal());
             }
             pedido.setTotal(total);
             pedidoRepository.save(pedido);
        }
    }
}