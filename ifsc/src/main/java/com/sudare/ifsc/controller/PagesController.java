package com.sudare.ifsc.controller;

import com.sudare.ifsc.dtos.ProdutoDTO;
import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import com.sudare.ifsc.services.DashboardService;
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

@Controller
public class PagesController {

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;
    private final DashboardService dashboardService;

    public PagesController(ProdutoService produtoService,
                           PedidoService pedidoService,
                           DashboardService dashboardService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("stats", dashboardService.getDashboardStats());
        model.addAttribute("ultimosPedidos", pedidoService.buscarUltimosPedidos(5));
        return "index";
    }
    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("fila", pedidoService.buscarFilaPreparo());
        return "pedidos";
    }
    @GetMapping("/cardapio")
    public String cardapio(Model model) {
        model.addAttribute("produtos", produtoService.listarProdutos());
        return "cardapio";
    }
    @GetMapping("/relatorios")
    public String relatorios(Model model,
                             @RequestParam(required = false) String ini,
                             @RequestParam(required = false) String fim) {
        model.addAttribute("ini", ini);
        model.addAttribute("fim", fim);
        model.addAttribute("resumo", java.util.List.of());
        return "relatorios";
    }
    @PostMapping("/cardapio/atualizar-ativo")
    public String atualizarProdutoAtivo(@RequestParam Long id, @RequestParam boolean ativo) {
        produtoService.atualizarAtivo(id, ativo);
        return "redirect:/cardapio";
    }
    @PostMapping("/pedidos/atualizar-status")
    public String atualizarPedidoStatus(@RequestParam Long id, @RequestParam StatusPedido status) {
        pedidoService.atualizarStatus(id, status);
        return "redirect:/pedidos";
    }

    @GetMapping("/produtos/novo")
    public String mostrarFormNovoProduto(Model model) {
        ProdutoDTO dto = new ProdutoDTO(null, "", "", BigDecimal.ZERO, true);
        model.addAttribute("produto", dto);
        return "form-produto";
    }
    
    @GetMapping("/produtos/editar/{id}")
    public String mostrarFormEditarProduto(@PathVariable Long id, Model model) {
        model.addAttribute("produto", produtoService.buscarDTO(id));
        return "form-produto";
    }
    @PostMapping("/produtos/salvar")
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoDTO dto, 
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form-produto";
        }
        if (dto.id() != null) {
            produtoService.atualizar(dto.id(), dto);
        } else {
            produtoService.criar(dto);
        }
        return "redirect:/cardapio";
    }


    // --- FLUXO ÚNICO DE CRIAÇÃO DE PEDIDO ---
    
    @GetMapping("/pedidos/novo")
    public String mostrarFormNovoPedido() {
        return "form-pedido"; 
    }

    @PostMapping("/pedidos/criar")
    public String criarPedido(@RequestParam String nomeObservacao) {
        if(nomeObservacao == null || nomeObservacao.trim().isEmpty()) {
            nomeObservacao = "Pedido Balcão"; // Valor padrão
        }
        Pedido pedidoSalvo = pedidoService.criarNovoPedido(nomeObservacao);
        return "redirect:/pedidos/editar/" + pedidoSalvo.getId();
    }
    
    @GetMapping("/pedidos/editar/{id}")
    public String mostrarFormEditarPedido(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoService.buscarCompletoParaEdicao(id));
        model.addAttribute("produtos", produtoService.listarProdutos());
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
}