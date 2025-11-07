package com.sudare.ifsc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
public class ItemPedido {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Pedido pedido;

    @ManyToOne(optional = false)
    private Produto produto;

    @NotNull 
    @Min(1)
    private Integer quantidade;

    @NotNull 
    @DecimalMin("0.0")
    private BigDecimal precoUnitario;

    public BigDecimal getSubtotal() {
    if (precoUnitario == null || quantidade == null) {
        return BigDecimal.ZERO;
    }
    return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
}

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public Pedido getPedido() { 
        return pedido; 
    }
    public void setPedido(Pedido pedido) { 
        this.pedido = pedido; 
    }
    public Produto getProduto() { 
        return produto; 
    }
    public void setProduto(Produto produto) { 
        this.produto = produto; 
    }
    public Integer getQuantidade() { 
        return quantidade; 
    }
    public void setQuantidade(Integer quantidade) { 
        this.quantidade = quantidade; 
    }
    public BigDecimal getPrecoUnitario() { 
        return precoUnitario; 
    }
    public void setPrecoUnitario(BigDecimal precoUnitario) { 
        this.precoUnitario = precoUnitario; 
    }
}