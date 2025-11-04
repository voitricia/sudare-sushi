package com.sudare.ifsc.controller;

import com.sudare.ifsc.services.PedidoService;
import com.sudare.ifsc.services.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        // TODO: Você ainda precisa criar a lógica de "stats" (estatísticas)
        // model.addAttribute("stats", ...); 
        
        // AGORA FUNCIONA: Busca os 5 últimos pedidos
        model.addAttribute("ultimosPedidos", pedidoService.buscarUltimosPedidos(5));
        return "index";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        // AGORA FUNCIONA: Busca a fila de pedidos EM_PREPARO
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
        // TODO: Você ainda precisa criar a lógica de "resumo"
        // model.addAttribute("resumo", ...);

        model.addAttribute("ini", ini);
        model.addAttribute("fim", fim);
        model.addAttribute("resumo", java.util.List.of()); // Lista vazia por enquanto
        return "relatorios";
    }
}