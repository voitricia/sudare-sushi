package com.sudare.ifsc.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp; // Importe este
import org.hibernate.annotations.UpdateTimestamp;  // Importe este

import java.math.BigDecimal;
import java.time.OffsetDateTime; // Importe este
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Coluna para "Pedido Balcão" (observação)
    private String nomeClienteObservacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.ABERTO;

    @Column(nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    // cascade = CascadeType.ALL e orphanRemoval = true
    // garantem que os Itens sejam salvos/removidos junto com o Pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPedido> itens = new ArrayList<>();

    // ==========================================================
    // === CAMPOS QUE FALTAVAM (A CORREÇÃO ESTÁ AQUI) ===
    // ==========================================================
    
    @CreationTimestamp // Define automaticamente a data de criação
    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @UpdateTimestamp // Define automaticamente a data de atualização
    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;
    
    // ==========================================================

    // --- Métodos Auxiliares ---

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    public void removerItem(ItemPedido item) {
        itens.remove(item);
        item.setPedido(null);
    }

    // --- Getters e Setters ---
    // (Lembre-se de gerar os getters e setters para os novos campos)

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

    // Getters e Setters para os campos novos
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
}