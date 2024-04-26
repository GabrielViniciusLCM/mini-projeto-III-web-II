package com.jeanlima.springrestapiapp.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column
    private String descricao;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade_estoque")
    private Integer quantidadeEstoque; // Nova propriedade para armazenar a quantidade em estoque

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
    
    public Produto() {
    }
    
    public Produto(String descricao, BigDecimal preco, Integer quantidadeEstoque) {
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((descricao == null) ? 0 : descricao.hashCode());
        result = prime * result + ((preco == null) ? 0 : preco.hashCode());
        result = prime * result + ((quantidadeEstoque == null) ? 0 : quantidadeEstoque.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Produto other = (Produto) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (descricao == null) {
            if (other.descricao != null)
                return false;
        } else if (!descricao.equals(other.descricao))
            return false;
        if (preco == null) {
            if (other.preco != null)
                return false;
        } else if (!preco.equals(other.preco))
            return false;
        if (quantidadeEstoque == null) {
            if (other.quantidadeEstoque != null)
                return false;
        } else if (!quantidadeEstoque.equals(other.quantidadeEstoque))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Produto [id=" + id + ", descricao=" + descricao + ", preco=" + preco + ", quantidadeEstoque=" + quantidadeEstoque + "]";
    }
}
