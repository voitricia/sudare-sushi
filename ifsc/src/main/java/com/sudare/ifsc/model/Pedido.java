package com.sudare.ifsc.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // Representa um pedido no banco de dados.
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nomeClienteObservacao; // Nome do cliente ou observação do pedido

    // Status do pedido (ABERTO, FECHADO, CANCELADO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.ABERTO;

    // Total do pedido, calculado a partir dos itens
    @Column(nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    // Indica se o pedido inclui taxa de serviço
    @Column(nullable = false)
    private boolean taxaServico = false; 

    // Itens associados a este pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPedido> itens = new ArrayList<>();

    // Timestamp de criação do pedido
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    // Timestamp da última atualização do pedido
    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;

    // Métodos para adicionar e remover itens do pedido
    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    // Remove um item do pedido
    public void removerItem(ItemPedido item) {
        itens.remove(item);
        item.setPedido(null);
    }

    // Calcula o subtotal de todos os itens do pedido
    public BigDecimal getSubtotalItens() {
        if (itens == null) return BigDecimal.ZERO;
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Calcula o valor da taxa de serviço (10% do subtotal dos itens) se aplicável
    public BigDecimal getValorTaxaServico() {
        if (!taxaServico) return BigDecimal.ZERO;
        return getSubtotalItens().multiply(new BigDecimal("0.10"));
    }

    // Getters e setters 
    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public String getNomeClienteObservacao() { 
        return nomeClienteObservacao; 
    }

    public void setNomeClienteObservacao(String nomeClienteObservacao) { 
        this.nomeClienteObservacao = nomeClienteObservacao; 
    }

    public StatusPedido getStatus() { 
        return status; 
    }
    
    public void setStatus(StatusPedido status) { 
        this.status = status; 
    }

    public BigDecimal getTotal() { 
        return total; 
    }
    
    public void setTotal(BigDecimal total) { 
        this.total = total; 
    }

    public List<ItemPedido> getItens() { 
        return itens; 
    }

    public void setItens(List<ItemPedido> itens) { 
        this.itens = itens; 
    }

    public OffsetDateTime getCriadoEm() { 
        return criadoEm; 
    }

    public void setCriadoEm(OffsetDateTime criadoEm) { 
        this.criadoEm = criadoEm; 
    }

    public OffsetDateTime getAtualizadoEm() { 
        return atualizadoEm; 
    }

    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { 
        this.atualizadoEm = atualizadoEm; 
    }
    
    public boolean isTaxaServico() { 
        return taxaServico; 
    }

    public void setTaxaServico(boolean taxaServico) { 
        this.taxaServico = taxaServico; 
    }
}