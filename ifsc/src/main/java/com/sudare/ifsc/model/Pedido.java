package com.sudare.ifsc.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.ABERTO;

    @Column(precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    public void recalcTotal() {
        this.total = itens.stream().map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public void adicionarItem(ItemPedido item) {
        if (item != null) {
            this.itens.add(item);
            item.setPedido(this); 
        }
        recalcTotal(); 
    }
    

    public void removerItem(ItemPedido item) {
        if (item != null) {
            this.itens.remove(item);
            item.setPedido(null); 
        }
        recalcTotal();
    }

    private String nomeClienteObservacao;

    public String getNomeClienteObservacao() {
        return nomeClienteObservacao;
    }

    public void setNomeClienteObservacao(String nomeClienteObservacao) {
        this.nomeClienteObservacao = nomeClienteObservacao;
    }

    public Long getId() { 
        return id;
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public Cliente getCliente() { 
        return cliente; 
    }
    public void setCliente(Cliente cliente) { 
        this.cliente = cliente; 
    }
    public List<ItemPedido> getItens() { 
        return itens; 
    }
    public void setItens(List<ItemPedido> itens) { 
        this.itens = itens; recalcTotal(); 
    }
    public OffsetDateTime getCriadoEm() { 
        return criadoEm; 
    }
    public void setCriadoEm(OffsetDateTime criadoEm) { 
        this.criadoEm = criadoEm; 
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
}