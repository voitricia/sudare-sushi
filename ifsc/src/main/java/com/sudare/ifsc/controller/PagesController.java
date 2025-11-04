package com.sudare.ifsc.controller;

import com.sudare.ifsc.model.StatusPedido;
import com.sudare.ifsc.services.DashboardService; // Importar
import com.sudare.ifsc.services.PedidoService;
import com.sudare.ifsc.services.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PagesController {

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;
    private final DashboardService dashboardService; // 1. Injetar o novo serviço

    public PagesController(ProdutoService produtoService, 
                           PedidoService pedidoService, 
                           DashboardService dashboardService) { // 2. Adicionar ao construtor
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // 3. Chamar o serviço e adicionar os stats ao model
        model.addAttribute("stats", dashboardService.getDashboardStats());
        model.addAttribute("ultimosPedidos", pedidoService.buscarUltimosPedidos(5));
        return "index";
    }

    // --- (O restante dos seus métodos permanece igual) ---

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
        // (Lógica de relatórios pendente)
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
}