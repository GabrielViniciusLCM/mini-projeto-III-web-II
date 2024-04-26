package com.jeanlima.springrestapiapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jeanlima.springrestapiapp.model.ItemPedido;

import jakarta.transaction.Transactional;



public interface ItemPedidoRepository extends JpaRepository<ItemPedido,Integer>{
    @Transactional
    @Modifying
    @Query("DELETE FROM ItemPedido ip WHERE ip.pedido.id = :pedidoId")
    void deleteByPedidoId(Integer pedidoId);
}
