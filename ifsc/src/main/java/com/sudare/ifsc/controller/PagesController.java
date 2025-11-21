package com.sudare.ifsc.controller;

import com.sudare.ifsc.dtos.ProdutoDTO;
import com.sudare.ifsc.dtos.RelatorioDTO;
import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import com.sudare.ifsc.services.PedidoService;
import com.sudare.ifsc.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List; 

@Controller
public class PagesController {

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;
    
    private final List<String> CATEGORIAS = List.of(
        "ENTRADAS", "SASHIMIS", "HOSSOMAKIS", "URAMAKIS", "FUTOMAKIS", 
        "NIGUIRIS", "GUNKANS", "COMBINADOS", "HOTS", "TEMAKIS", "BEBIDAS"
    );

    public PagesController(ProdutoService produtoService,
                           PedidoService pedidoService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }
    
    @GetMapping({"/", "/index"})
    public String home(Model model,
                       @RequestParam(name = "statusEditId", required = false) Long statusEditId,
                       @RequestParam(name = "statusFiltro", required = false) String statusFiltro) { 
        
        model.addAttribute("ultimosPedidos", pedidoService.buscarPedidosHome(statusFiltro));
        model.addAttribute("statusEditId", statusEditId); 
        
        String filtroAtivo = (statusFiltro == null || statusFiltro.isEmpty()) ? "ABERTO" : statusFiltro;
        model.addAttribute("statusFiltroAtivo", filtroAtivo);
        
        return "index";
    }

    @PostMapping("/pedidos/atualizar-status")
    public String atualizarPedidoStatus(@RequestParam Long id, @RequestParam StatusPedido status,
                                        @RequestParam(name = "statusFiltro", required = false) String statusFiltro) {
        pedidoService.atualizarStatus(id, status);
        
        String filtro = (statusFiltro == null || statusFiltro.isEmpty()) ? "ABERTO" : statusFiltro;
        return "redirect:/?statusFiltro=" + filtro;
    }

    @GetMapping("/relatorios")
    public String relatorios(Model model,
                             @RequestParam(name = "periodo", required = false) String periodo,
                             @RequestParam(name = "dataInicio", required = false) String dataInicioStr,
                             @RequestParam(name = "dataFim", required = false) String dataFimStr) {
        RelatorioDTO relatorio;
        String periodoAtivo = "hoje";
        LocalDate dataInicio = null;
        LocalDate dataFim = null;
        if (periodo != null && !periodo.isEmpty()) {
            periodoAtivo = periodo;
            relatorio = pedidoService.getRelatorio(periodo);
        } else if (dataInicioStr != null && !dataInicioStr.isEmpty() && dataFimStr != null && !dataFimStr.isEmpty()) {
            try {
                dataInicio = LocalDate.parse(dataInicioStr);
                dataFim = LocalDate.parse(dataFimStr);
                relatorio = pedidoService.getRelatorio(dataInicio, dataFim);
                periodoAtivo = "custom";
            } catch (Exception e) {
                relatorio = pedidoService.getRelatorio("hoje");
            }
        } else {
            relatorio = pedidoService.getRelatorio("hoje");
        }
        model.addAttribute("relatorio", relatorio);
        model.addAttribute("periodoAtivo", periodoAtivo);
        model.addAttribute("dataInicio", dataInicio); 
        model.addAttribute("dataFim", dataFim);
        return "relatorios";
    }

    @GetMapping("/cardapio")
    public String cardapio(Model model) {
        model.addAttribute("produtosAgrupados", produtoService.listarProdutosAgrupados());
        return "cardapio";
    }

    @PostMapping("/cardapio/atualizar-ativo")
    public String atualizarProdutoAtivo(@RequestParam Long id, @RequestParam boolean ativo) {
        produtoService.atualizarAtivo(id, ativo);
        return "redirect:/cardapio";
    }

    @GetMapping("/produtos/novo")
    public String mostrarFormNovoProduto(Model model) {
        model.addAttribute("categorias", CATEGORIAS);
        ProdutoDTO dto = new ProdutoDTO(null, "", null, BigDecimal.ZERO, true);
        model.addAttribute("produto", dto);
        return "form-produto";
    }
    
    @GetMapping("/produtos/editar/{id}")
    public String mostrarFormEditarProduto(@PathVariable Long id, Model model) {
        model.addAttribute("categorias", CATEGORIAS);
        model.addAttribute("produto", produtoService.buscarDTO(id));
        return "form-produto";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoDTO dto, 
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", CATEGORIAS);
            return "form-produto";
        }
        if (dto.id() != null) {
            produtoService.atualizar(dto.id(), dto);
        } else {
            produtoService.criar(dto);
        }
        return "redirect:/cardapio";
    }

    @GetMapping("/pedidos/novo")
    public String mostrarFormNovoPedido() {
        return "form-pedido"; 
    }

    @PostMapping("/pedidos/criar")
    public String criarPedido(@RequestParam String local,
                            @RequestParam(name = "observacao", required = false) String observacao) {
        String observacaoFinal;
        if (observacao != null && !observacao.trim().isEmpty()) {
            observacaoFinal = local + " (" + observacao + ")";
        } else {
            observacaoFinal = local;
        }
        Pedido pedidoSalvo = pedidoService.criarNovoPedido(observacaoFinal);
        return "redirect:/pedidos/editar/" + pedidoSalvo.getId();
    }
    
    @GetMapping("/pedidos/editar/{id}")
    public String mostrarFormEditarPedido(@PathVariable Long id, Model model,
                                          @RequestParam(name = "editItemId", required = false) Long editItemId) {
        model.addAttribute("pedido", pedidoService.buscarCompletoParaEdicao(id));
        model.addAttribute("produtosAgrupados", produtoService.listarProdutosAgrupados());
        model.addAttribute("editItemId", editItemId); 
        return "form-editar-pedido"; 
    }
    
    @PostMapping("/pedidos/editar/adicionar-item")
    public String adicionarItemAoPedido(@RequestParam Long pedidoId,
                                        @RequestParam Long produtoId,
                                        @RequestParam Integer quantidade) {
        pedidoService.adicionarItemAoPedido(pedidoId, produtoId, quantidade);
        return "redirect:/pedidos/editar/" + pedidoId;
    }
    
    @PostMapping("/pedidos/editar/remover-item")
    public String removerItemDoPedido(@RequestParam Long pedidoId,
                                      @RequestParam Long itemPedidoId) {
        pedidoService.removerItemDoPedido(pedidoId, itemPedidoId);
        return "redirect:/pedidos/editar/" + pedidoId;
    }

    @PostMapping("/pedidos/editar/atualizar-item")
    public String atualizarItemDoPedido(@RequestParam Long pedidoId,
                                        @RequestParam Long itemPedidoId,
                                        @RequestParam Integer quantidade) {
        pedidoService.atualizarItemQuantidade(itemPedidoId, quantidade);
        return "redirect:/pedidos/editar/" + pedidoId;
    }
    
    @PostMapping("/pedidos/editar/taxa-servico")
    public String atualizarTaxaServico(@RequestParam Long pedidoId,
                                       @RequestParam(defaultValue = "false") boolean taxaAtiva) {
        pedidoService.atualizarTaxaServico(pedidoId, taxaAtiva);
        return "redirect:/pedidos/editar/" + pedidoId;
    }

    @PostMapping("/pedidos/editar/observacao")
    public String atualizarObservacao(@RequestParam Long pedidoId,
                                    @RequestParam String nomeClienteObservacao) {
        pedidoService.atualizarObservacao(pedidoId, nomeClienteObservacao);
        return "redirect:/pedidos/editar/" + pedidoId;
    }
}