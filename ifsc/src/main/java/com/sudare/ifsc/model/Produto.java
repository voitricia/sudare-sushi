package com.sudare.ifsc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;
    
    @NotNull @DecimalMin("0.0")
    private BigDecimal preco;

    private boolean ativo = true;

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
    public boolean isAtivo() { 
        return ativo;
    }
    public void setAtivo(boolean ativo) { 
        this.ativo = ativo;
    }

}