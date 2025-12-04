package com.sudare.ifsc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity // Entidade que representa um produto do cardápio
public class Produto {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank // Nome não pode ser vazio
    private String nome;
    
    @NotNull 
    @DecimalMin("0.0") // Preço deve ser igual ou maior que zero
    private BigDecimal preco;

    @NotBlank // Categoria obrigatória
    private String categoria;

    // Indica se o produto está ativo no cardápio
    private boolean ativo = true;

    // Getters e setters
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getNome() { 
        return nome; 
    }
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    public BigDecimal getPreco() { 
        return preco; 
    }
    public void setPreco(BigDecimal preco) { 
        this.preco = preco; 
    }
    
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isAtivo() { 
        return ativo;
    }
    public void setAtivo(boolean ativo) { 
        this.ativo = ativo;
    }
}