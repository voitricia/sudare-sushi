package com.sudare.ifsc.services;

import com.sudare.ifsc.dtos.ItemPedidoDTO;
import com.sudare.ifsc.dtos.PedidoDTO;
import com.sudare.ifsc.dtos.RelatorioDTO;
import com.sudare.ifsc.exceptions.NotFoundException;
import com.sudare.ifsc.model.*;
import com.sudare.ifsc.repositories.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    // private final ClienteRepository clienteRepository; // REMOVIDO
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    // ATUALIZADO: Construtor sem ClienteRepository
    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
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

    /**
     * ATENÇÃO: Este método da API (PedidoController) está quebrado
     * pois o PedidoDTO espera um 'clienteId' que não existe mais.
     * Precisamos refatorar o PedidoDTO e o PedidoController (API) se você ainda usa eles.
     */
    @Transactional
    public Pedido criar(PedidoDTO dto){
        // A lógica antiga que usava 'dto.clienteId()' está quebrada.
        // Vamos lançar um erro para evitar confusão.
        throw new UnsupportedOperationException("O método 'criar(PedidoDTO)' precisa ser refatorado após a remoção do Cliente.");
    }

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
        Pageable pageable = Pageable.unpaged();
        if (limite > 0) {
            pageable = PageRequest.of(0, limite, Sort.by("criadoEm").descending());
        }
        return pedidoRepository.findAllWithCliente(pageable);
    }

    /**
     * ATUALIZADO: Método muito mais simples!
     * Não precisa mais buscar "Consumidor Final".
     */
    @Transactional
    public Pedido criarNovoPedido(String nomeObservacao) {
        Pedido pedido = new Pedido();
        pedido.setNomeClienteObservacao(nomeObservacao); // Salva a observação
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setTotal(BigDecimal.ZERO);

        return pedidoRepository.save(pedido);
    }
    
    // ... (adicionarItemAoPedido, removerItemDoPedido, atualizarItemQuantidade, recalcularTotalPedido) ...
    // ... (Estes métodos não mudam) ...
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
        
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
             if (pedido.getId() == null) {
                 pedido.setTotal(BigDecimal.ZERO);
                 return;
             }
             Pedido pedidoComItens = buscarCompletoParaEdicao(pedido.getId());
             for (ItemPedido i : pedidoComItens.getItens()) {
                 total = total.add(i.getSubtotal());
             }
             pedidoComItens.setTotal(total);
             pedidoRepository.save(pedidoComItens);
        } else {
             for (ItemPedido i : pedido.getItens()) {
                 total = total.add(i.getSubtotal());
             }
             pedido.setTotal(total);
             pedidoRepository.save(pedido);
        }
    }
    
    // --- Métodos de Relatório (Não mudam) ---

    @Transactional(readOnly = true)
    public RelatorioDTO getRelatorio(LocalDate dataInicio, LocalDate dataFim) {
        OffsetDateTime inicio = dataInicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime fim = dataFim.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);
        
        List<Pedido> pedidos = pedidoRepository.findPedidosPorStatusEData(StatusPedido.FINALIZADO, inicio, fim);
        
        return calcularStats(pedidos);
    }
    
    @Transactional(readOnly = true)
    public RelatorioDTO getRelatorio(String periodo) {
        OffsetDateTime fim = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime inicio;
        
        List<Pedido> pedidos;

        switch (periodo) {
            case "semana":
                inicio = fim.minusDays(7).truncatedTo(ChronoUnit.DAYS);
                break;
            case "mes":
                inicio = fim.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                break;
            case "tudo":
                pedidos = pedidoRepository.findAllPedidosPorStatus(StatusPedido.FINALIZADO);
                return calcularStats(pedidos);
            
            case "hoje":
            default:
                inicio = fim.truncatedTo(ChronoUnit.DAYS);
                break;
        }

        pedidos = pedidoRepository.findPedidosPorStatusEData(StatusPedido.FINALIZADO, inicio, fim);
        return calcularStats(pedidos);
    }

    private RelatorioDTO calcularStats(List<Pedido> pedidos) {
        BigDecimal faturamento = pedidos.stream()
                                        .map(Pedido::getTotal)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long numPedidos = (long) pedidos.size();
        
        // Removido Ticket Médio
        return new RelatorioDTO(faturamento, numPedidos, pedidos);
    }
}