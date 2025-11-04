package com.sudare.ifsc.services;

import com.sudare.ifsc.dtos.ItemPedidoDTO;
import com.sudare.ifsc.dtos.PedidoDTO;
import com.sudare.ifsc.exceptions.NotFoundException;
import com.sudare.ifsc.model.*;
import com.sudare.ifsc.repositories.*;
import org.springframework.data.domain.PageRequest; // Importar
import org.springframework.data.domain.Pageable; // Importar
import org.springframework.data.domain.Sort; // Importar
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {
    // ... (injeções de dependência e construtor que você já tem)
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }


    // ... (métodos listar, buscar, criar, atualizarStatus que você já tem)
    public List<Pedido> listar(){
        return pedidoRepository.findAll();
    }

    public Pedido buscar(Long id){
        return pedidoRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado"));
    }

    @Transactional
    public Pedido criar(PedidoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        for (ItemPedidoDTO itemDto : dto.itens()) {
            Produto produto = produtoRepository.findById(itemDto.produtoId())
                    .orElseThrow(() -> new NotFoundException("Produto não encontrado: ID " + itemDto.produtoId()));
            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDto.quantidade());
            item.setPrecoUnitario(itemDto.precoUnitario());
            pedido.adicionarItem(item); // (Lembre-se de ter o método 'adicionarItem' no Pedido.java)
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarStatus(Long id, StatusPedido status){
        Pedido p = buscar(id);
        p.setStatus(status);
        return pedidoRepository.save(p);
    }

    // --- NOVOS MÉTODOS ---

    /**
     * Busca a fila de pedidos em preparo (para a página 'pedidos.html').
     */
    @Transactional(readOnly = true) // readOnly = true é uma boa prática para consultas
    public List<Pedido> buscarFilaPreparo() {
        // Você pode adicionar mais status se quiser, ex: ABERTO
        return pedidoRepository.findAllByStatusWithCliente(StatusPedido.EM_PREPARO);
    }

    /**
     * Busca os últimos N pedidos (para a página 'index.html').
     */
    @Transactional(readOnly = true)
    public List<Pedido> buscarUltimosPedidos(int limite) {
        // Cria um "Pageable" que busca a página 0, com 'limite' de itens, ordenado por 'criadoEm' descendente
        Pageable pageable = PageRequest.of(0, limite, Sort.by("criadoEm").descending());
        return pedidoRepository.findAllWithCliente(pageable);
    }
}