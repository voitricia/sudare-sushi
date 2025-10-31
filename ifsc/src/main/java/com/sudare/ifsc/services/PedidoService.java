package com.sudare.ifsc.services;

import com.sudare.ifsc.dtos.ItemPedidoDTO;
import com.sudare.ifsc.dtos.PedidoDTO;
import com.sudare.ifsc.exceptions.NotFoundException;
import com.sudare.ifsc.model.*;
import com.sudare.ifsc.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemRepository;

    public PedidoService(PedidoRepository p, ClienteRepository c, ProdutoRepository pr, ItemPedidoRepository i){
        this.pedidoRepository = p; 
        this.clienteRepository = c; 
        this.produtoRepository = pr; 
        this.itemRepository = i;
    }

    public List<Pedido> listar(){ 
        return pedidoRepository.findAll(); 
    }

    public Pedido buscar(Long id){ 
        return pedidoRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado")); 
    }

    @Transactional
    public Pedido criar(PedidoDTO dto){
        Cliente cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido = pedidoRepository.save(pedido);
        for(ItemPedidoDTO i : dto.itens()){
            Produto produto = produtoRepository.findById(i.produtoId()).orElseThrow(() -> new NotFoundException("Produto não encontrado"));
            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(i.quantidade());
            item.setPrecoUnitario(i.precoUnitario());
            itemRepository.save(item);
            pedido.getItens().add(item);
        }
        pedido.recalcTotal();
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarStatus(Long id, StatusPedido status){
        Pedido p = buscar(id);
        p.setStatus(status);
        return pedidoRepository.save(p);
    }
}
