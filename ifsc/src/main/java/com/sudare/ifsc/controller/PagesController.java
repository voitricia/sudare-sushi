package com.sudare.ifsc.controller;

import com.sudare.ifsc.model.StatusPedido; // Importar
import com.sudare.ifsc.services.PedidoService;
import com.sudare.ifsc.services.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Importar
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PagesController {

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    public PagesController(ProdutoService produtoService, PedidoService pedidoService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // model.addAttribute("stats", ...); // (Lógica de stats pendente)
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
        // model.addAttribute("resumo", ...); // (Lógica de relatórios pendente)
        model.addAttribute("ini", ini);
        model.addAttribute("fim", fim);
        model.addAttribute("resumo", java.util.List.of());
        return "relatorios";
    }
    
    // --- MÉTODOS NOVOS (PARA OS FORMULÁRIOS) ---

    /**
     * Recebe o POST dos formulários de "Ativar" e "Desativar" produto.
     */
    @PostMapping("/cardapio/atualizar-ativo")
    public String atualizarProdutoAtivo(@RequestParam Long id, @RequestParam boolean ativo) {
        // Reutiliza o método de serviço (que criamos para a API, mas serve aqui também)
        produtoService.atualizarAtivo(id, ativo);
        
        // Redireciona (recarrega) a página do cardápio
        return "redirect:/cardapio";
    }

    /**
     * Recebe o POST dos formulários de "Finalizar" e "Cancelar" pedido.
     */
    @PostMapping("/pedidos/atualizar-status")
    public String atualizarPedidoStatus(@RequestParam Long id, @RequestParam StatusPedido status) {
        // Reutiliza o método de serviço da API
        // O Spring converte a String ("PRONTO") para o Enum (StatusPedido.PRONTO)
        pedidoService.atualizarStatus(id, status);
        
        // Redireciona (recarrega) a página de pedidos
        return "redirect:/pedidos";
    }
}