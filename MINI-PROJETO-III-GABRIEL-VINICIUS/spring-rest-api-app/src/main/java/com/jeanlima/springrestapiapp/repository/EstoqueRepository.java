package com.jeanlima.springrestapiapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jeanlima.springrestapiapp.model.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {
    // Método para buscar estoque pelo nome do produto
    Estoque findByProdutoDescricao(String descricao);
}
