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
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList; 
import java.util.List;

// Serviço para gerenciar operações relacionadas aos pedidos.
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

    // Lista todos os pedidos.
    public List<Pedido> listar(){ 
        return pedidoRepository.findAll(); 
    }
    
    // Busca um pedido pelo ID.
    public Pedido buscar(Long id){ 
        return pedidoRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado")); 
    }

    @Transactional(readOnly = true) // Busca um pedido completo com seus itens para edição.
    public Pedido buscarCompletoParaEdicao(Long id) {
        return pedidoRepository.findByIdCompleto(id)
            .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));
    }

    @Transactional // Atualiza o status de um pedido.
    public Pedido atualizarStatus(Long id, StatusPedido status){
        Pedido p = buscar(id);
        p.setStatus(status);
        return pedidoRepository.save(p);
    }

    @Transactional(readOnly = true) // Busca pedidos para a tela inicial com filtros de status.
    public List<Pedido> buscarPedidosHome(String statusFiltro) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("criadoEm").descending());
        
        if (statusFiltro == null || statusFiltro.isEmpty()) {
            statusFiltro = "ABERTO";
        }

        if (statusFiltro.equals("NENHUM")) {
            return new ArrayList<>(); 
        }

        if (statusFiltro.equals("TODOS")) {
            List<StatusPedido> statusesAtivos = List.of(StatusPedido.ABERTO, StatusPedido.EM_PREPARO, StatusPedido.PRONTO);
            return pedidoRepository.findHomeByStatusInWithItems(statusesAtivos, pageable);
        }

        if (statusFiltro.equals("TUDO")) {
            return pedidoRepository.findHomeAllWithItems(pageable);
        }

        try { // Converte a string de status para o enum correspondente.
            StatusPedido status = StatusPedido.valueOf(statusFiltro);
            return pedidoRepository.findHomeByStatusWithItems(status, pageable);
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    @Transactional // Cria um novo pedido com observação do cliente.
    public Pedido criarNovoPedido(String nomeObservacao) {
        Pedido pedido = new Pedido();
        pedido.setNomeClienteObservacao(nomeObservacao);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setTotal(BigDecimal.ZERO);
        return pedidoRepository.save(pedido);
    }
    
     @Transactional// Adiciona um item a um pedido existente.
    public Pedido adicionarItemAoPedido(Long pedidoId, Long produtoId, Integer quantidade) {
        Pedido pedido = buscarCompletoParaEdicao(pedidoId);
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        // Verifica se o item já existe no pedido e atualiza a quantidade se necessário.
        for (ItemPedido itemExistente : pedido.getItens()) {
            if (itemExistente.getProduto().getId().equals(produtoId)) {
                itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidade);
                recalcularTotalPedido(pedido);
                return pedido;
            }
        }

        // Adiciona um novo item ao pedido.
        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        pedido.adicionarItem(item);
        
        recalcularTotalPedido(pedido);
        return pedido; 
    }

    @Transactional // Remove um item de um pedido.
    public Pedido removerItemDoPedido(Long pedidoId, Long itemPedidoId) {
        Pedido pedido = buscarCompletoParaEdicao(pedidoId);
        ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new NotFoundException("Item de pedido não encontrado"));
        
        pedido.removerItem(item);
        itemPedidoRepository.delete(item); 
        recalcularTotalPedido(pedido);
        return pedido;
    }

    @Transactional // Atualiza a quantidade de um item em um pedido.
    public void atualizarItemQuantidade(Long itemPedidoId, Integer quantidade) {
        if (quantidade < 1) {
             ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new NotFoundException("Item de pedido não encontrado"));
             removerItemDoPedido(item.getPedido().getId(), itemPedidoId);
             return;
        }

        // Atualiza a quantidade do item e recalcula o total do pedido.
        ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new NotFoundException("Item de pedido não encontrado"));

        item.setQuantidade(quantidade);
        itemPedidoRepository.save(item);
        recalcularTotalPedido(item.getPedido());
    }
    
    @Transactional // Ativa ou desativa a taxa de serviço em um pedido.
    public void atualizarTaxaServico(Long pedidoId, boolean ativa) {
        Pedido pedido = buscarCompletoParaEdicao(pedidoId);
        pedido.setTaxaServico(ativa);
        recalcularTotalPedido(pedido); 
    }

    @Transactional // Atualiza a observação do cliente em um pedido.
    public Pedido atualizarObservacao(Long pedidoId, String observacao) {
        Pedido pedido = buscar(pedidoId); 
        pedido.setNomeClienteObservacao(observacao);
        return pedidoRepository.save(pedido);
    }

    // Recalcula o total do pedido considerando os itens e a taxa de serviço.
    private void recalcularTotalPedido(Pedido pedido) {
        Pedido pedidoAtualizado = pedido;
        if (pedido.getId() != null) {
             pedidoAtualizado = pedidoRepository.findByIdCompleto(pedido.getId()).orElse(pedido);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        if (pedidoAtualizado.getItens() != null) {
            for (ItemPedido i : pedidoAtualizado.getItens()) {
                subtotal = subtotal.add(i.getSubtotal());
            }
        }
        
        BigDecimal totalFinal = subtotal;
        if (pedidoAtualizado.isTaxaServico()) {
            BigDecimal valorTaxa = subtotal.multiply(new BigDecimal("0.10"));
            totalFinal = subtotal.add(valorTaxa);
        }
        
        pedidoAtualizado.setTotal(totalFinal.setScale(2, RoundingMode.HALF_EVEN));
        pedidoRepository.save(pedidoAtualizado);
    }

    @Transactional(readOnly = true)// Gera um relatório de pedidos finalizados em um intervalo de datas.
    public RelatorioDTO getRelatorio(LocalDate dataInicio, LocalDate dataFim) {
        ZoneOffset zonaDoServidor = OffsetDateTime.now().getOffset();
        OffsetDateTime inicio = dataInicio.atStartOfDay().atOffset(zonaDoServidor);
        OffsetDateTime fim = dataFim.atTime(LocalTime.MAX).atOffset(zonaDoServidor);
        
        List<Pedido> pedidos = pedidoRepository.findPedidosPorStatusEData(StatusPedido.FINALIZADO, inicio, fim);
        
        return calcularStats(pedidos);
    }
    
    @Transactional(readOnly = true) // Gera um relatório de pedidos finalizados com base em um período predefinido.
    public RelatorioDTO getRelatorio(String periodo) {
        OffsetDateTime fim = OffsetDateTime.now();
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

    // Calcula as estatísticas do relatório, como faturamento total e número de pedidos.
    private RelatorioDTO calcularStats(List<Pedido> pedidos) {
        BigDecimal faturamento = pedidos.stream()
                                        .map(Pedido::getTotal)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long numPedidos = (long) pedidos.size();

        return new RelatorioDTO(faturamento, numPedidos, pedidos);
    }
}