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

    private String descricao;

    @NotNull @DecimalMin("0.0")
    private BigDecimal preco;

    @NotNull @Min(0)
    private Integer estoque;

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
    public String getDescricao() { 
        return descricao; 
    }
    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }
    public BigDecimal getPreco() { 
        return preco; 
    }
    public void setPreco(BigDecimal preco) { 
        this.preco = preco; 
    }
    public Integer getEstoque() { 
        return estoque; 
    }
    public void setEstoque(Integer estoque) { 
        this.estoque = estoque; 
    }
    public boolean isAtivo() { 
        return ativo;
    }
    public void setAtivo(boolean ativo) { 
        this.ativo = ativo;
    }

}
