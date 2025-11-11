package com.sudare.ifsc.services;

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
import java.time.ZoneOffset;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PedidoService {
    

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    // Método para o PedidoController (API)
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

    /* ==========================================================
    === CORREÇÃO: Método da API (quebrado) comentado ===
    ==========================================================
    @Transactional
    public Pedido criar(PedidoDTO dto){
        throw new UnsupportedOperationException("O método 'criar(PedidoDTO)' precisa ser refatorado após a remoção do Cliente.");
    }
    ==========================================================
    */

    @Transactional
    public Pedido atualizarStatus(Long id, StatusPedido status){
        Pedido p = buscar(id);
        p.setStatus(status);
        return pedidoRepository.save(p);
    }
    
    /**
     * Busca os pedidos para a Home, com filtro de status.
     */
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
    public List<Pedido> buscarPedidosHome(String statusFiltro) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("criadoEm").descending());
        
        if (statusFiltro != null && !statusFiltro.isEmpty() && !statusFiltro.equals("TODOS")) {
            try {
                StatusPedido status = StatusPedido.valueOf(statusFiltro);
                // Agora este método existe no repositório
                return pedidoRepository.findAllByStatusOrderByCriadoEmDesc(status, pageable);
            } catch (IllegalArgumentException e) {
                // Filtro inválido
            }
        }
        
        List<StatusPedido> statusesAtivos = List.of(StatusPedido.ABERTO, StatusPedido.EM_PREPARO, StatusPedido.PRONTO);
        // Agora este método existe no repositório
        return pedidoRepository.findAllByStatusInOrderByCriadoEmDesc(statusesAtivos, pageable);
    }

    @Transactional
    public Pedido criarNovoPedido(String nomeObservacao) {
        Pedido pedido = new Pedido();
        pedido.setNomeClienteObservacao(nomeObservacao);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setTotal(BigDecimal.ZERO);
        return pedidoRepository.save(pedido);
    }
    
    // ... (O resto do seu PedidoService.java: adicionarItem, removerItem, relatorios, etc.) ...
    
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
    
    // === NOVO MÉTODO PARA ALTERNAR A TAXA ===
    @Transactional
    public void atualizarTaxaServico(Long pedidoId, boolean ativa) {
        Pedido pedido = buscarCompletoParaEdicao(pedidoId);
        pedido.setTaxaServico(ativa);
        recalcularTotalPedido(pedido); // Recalcula e salva
    }

    // === MÉTODO RECALCULAR ATUALIZADO ===
    private void recalcularTotalPedido(Pedido pedido) {
        // Garante que temos os itens atualizados
        Pedido pedidoAtualizado = pedido;
        if (pedido.getId() != null) {
            // Recarrega para garantir que não estamos usando dados obsoletos da sessão
             pedidoAtualizado = pedidoRepository.findByIdCompleto(pedido.getId()).orElse(pedido);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        if (pedidoAtualizado.getItens() != null) {
            for (ItemPedido i : pedidoAtualizado.getItens()) {
                subtotal = subtotal.add(i.getSubtotal());
            }
        }
        BigDecimal total = BigDecimal.ZERO;
        
        // Aplica os 10% se a taxa estiver ativa
        BigDecimal totalFinal = subtotal;
        if (pedidoAtualizado.isTaxaServico()) {
            BigDecimal valorTaxa = subtotal.multiply(new BigDecimal("0.10"));
            totalFinal = subtotal.add(valorTaxa);
        }
        
        pedidoAtualizado.setTotal(totalFinal.setScale(2, RoundingMode.HALF_EVEN));
        pedidoRepository.save(pedidoAtualizado);
    }

    // ... (Métodos de relatório continuam iguais abaixo) ...
    
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
            case "semana": inicio = fim.minusDays(7).truncatedTo(ChronoUnit.DAYS); break;
            case "mes": inicio = fim.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS); break;
            case "tudo":
                pedidos = pedidoRepository.findAllPedidosPorStatus(StatusPedido.FINALIZADO);
                return calcularStats(pedidos);
            case "hoje": default: inicio = fim.truncatedTo(ChronoUnit.DAYS); break;
            case "hoje":
            default:
                inicio = fim.truncatedTo(ChronoUnit.DAYS);
                break;
        }
        pedidos = pedidoRepository.findPedidosPorStatusEData(StatusPedido.FINALIZADO, inicio, fim);
        return calcularStats(pedidos);
    }

    private RelatorioDTO calcularStats(List<Pedido> pedidos) {
        BigDecimal faturamento = pedidos.stream().map(Pedido::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal faturamento = pedidos.stream()
                                        .map(Pedido::getTotal)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        Long numPedidos = (long) pedidos.size();
        return new RelatorioDTO(faturamento, numPedidos, pedidos);
    }
}