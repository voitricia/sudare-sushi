package com.sudare.ifsc.controller;

import com.sudare.ifsc.dtos.ProdutoDTO; // Importar
import com.sudare.ifsc.model.Pedido;
import com.sudare.ifsc.model.StatusPedido;
import com.sudare.ifsc.services.ClienteService;
import com.sudare.ifsc.services.DashboardService;
import com.sudare.ifsc.services.PedidoService;
import com.sudare.ifsc.services.ProdutoService;
import jakarta.validation.Valid; // Importar
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Importar
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Importar
import org.springframework.web.bind.annotation.PathVariable; // Importar
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal; // Importar

@Controller
public class PagesController {

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;
    private final DashboardService dashboardService;
    private final ClienteService clienteService;

    public PagesController(ProdutoService produtoService,
            PedidoService pedidoService,
            DashboardService dashboardService,
            ClienteService clienteService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
        this.dashboardService = dashboardService;
        this.clienteService = clienteService;
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

    // --- NOVOS MÉTODOS PARA O FORMULÁRIO DE PRODUTO ---

    /**
     * Mostra o formulário VAZIO para criar um novo produto.
     */
    @GetMapping("/produtos/novo")
    public String mostrarFormNovoProduto(Model model) {
        // Cria um DTO com valores padrão
        ProdutoDTO dto = new ProdutoDTO(null, "", "", BigDecimal.ZERO, 0, true);
        model.addAttribute("produto", dto);
        return "form-produto";
    }

    /**
     * Mostra o formulário PREENCHIDO para editar um produto existente.
     */
    @GetMapping("/produtos/editar/{id}")
    public String mostrarFormEditarProduto(@PathVariable Long id, Model model) {
        // Busca o DTO do produto existente e o coloca no model
        model.addAttribute("produto", produtoService.buscarDTO(id));
        return "form-produto";
    }

    /**
     * Recebe o POST do formulário (tanto de novo quanto de edição).
     */
    @PostMapping("/produtos/salvar")
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoDTO dto,
            BindingResult bindingResult) {

        // 1. Verifica se há erros de validação (ex: nome em branco, preço <= 0)
        if (bindingResult.hasErrors()) {
            // Se houver erros, re-exibe a página do formulário
            // O Thymeleaf usará o 'bindingResult' para mostrar os erros
            return "form-produto";
        }

        // 2. Se não houver erros, salva
        if (dto.id() != null) {
            // Se tem ID, é uma ATUALIZAÇÃO
            produtoService.atualizar(dto.id(), dto);
        } else {
            // Se não tem ID, é uma CRIAÇÃO
            produtoService.criar(dto);
        }

        // 3. Redireciona de volta para a listagem
        return "redirect:/cardapio";
    }

    // --- NOVOS MÉTODOS PARA O FORMULÁRIO DE PEDIDO ---
    @GetMapping("/pedidos/novo")
    public String mostrarFormNovoPedido(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "form-pedido";
    }

    @PostMapping("/pedidos/criar")
    public String criarPedidoHeader(@RequestParam Long clienteId) {
        Pedido pedidoSalvo = pedidoService.criarPedidoHeader(clienteId);
        return "redirect:/pedidos/editar/" + pedidoSalvo.getId();
    }

    /**
     * ETAPA 2: GET (AGORA COMPLETO)
     * Mostra a página de edição de itens.
     */
    @GetMapping("/pedidos/editar/{id}")
    public String mostrarFormEditarPedido(@PathVariable Long id, Model model) {
        // 1. Busca o pedido (para a tabela de itens)
        model.addAttribute("pedido", pedidoService.buscar(id));

        // 2. Busca os produtos (para o <select> de "Adicionar Item")
        model.addAttribute("produtos", produtoService.listarProdutos());

        return "form-editar-pedido";
    }

    /**
     * ETAPA 2: POST (ADICIONAR ITEM)
     */
    @PostMapping("/pedidos/editar/adicionar-item")
    public String adicionarItemAoPedido(@RequestParam Long pedidoId,
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade) {

        pedidoService.adicionarItemAoPedido(pedidoId, produtoId, quantidade);

        // Recarrega a página de edição
        return "redirect:/pedidos/editar/" + pedidoId;
    }

    /**
     * ETAPA 2: POST (REMOVER ITEM)
     */
    @PostMapping("/pedidos/editar/remover-item")
    public String removerItemDoPedido(@RequestParam Long pedidoId,
            @RequestParam Long itemPedidoId) {

        pedidoService.removerItemDoPedido(pedidoId, itemPedidoId);

        // Recarrega a página de edição
        return "redirect:/pedidos/editar/" + pedidoId;
    }
}