package com.sudare.ifsc.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nomeClienteObservacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.ABERTO;

    @Column(nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean taxaServico = false; 

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPedido> itens = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;


    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    public void removerItem(ItemPedido item) {
        itens.remove(item);
        item.setPedido(null);
    }

    public BigDecimal getSubtotalItens() {
        if (itens == null) return BigDecimal.ZERO;
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTaxaServico() {
        if (!taxaServico) return BigDecimal.ZERO;
        return getSubtotalItens().multiply(new BigDecimal("0.10"));
    }

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