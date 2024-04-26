package com.jeanlima.springrestapiapp.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.jeanlima.springrestapiapp.model.Estoque;
import com.jeanlima.springrestapiapp.repository.EstoqueRepository;

import java.util.List;

@RestController
@RequestMapping("/api/estoques")
public class EstoqueController {

    @Autowired
    private EstoqueRepository estoqueRepository;

    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estoque createEstoque(@RequestBody Estoque estoque) {
        return estoqueRepository.save(estoque);
    }

    @GetMapping
    public List<Estoque> getAllEstoques() {
        return estoqueRepository.findAll();
    }

    @GetMapping("/{id}")
    public Estoque getEstoqueById(@PathVariable Integer id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado"));
    }

    @PutMapping("/{id}")
    public Estoque updateEstoque(@PathVariable Integer id, @RequestBody Estoque estoque) {
        return estoqueRepository.findById(id)
                .map(existingEstoque -> {
                    existingEstoque.setProduto(estoque.getProduto());
                    existingEstoque.setQuantidade(estoque.getQuantidade());
                    return estoqueRepository.save(existingEstoque);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEstoque(@PathVariable Integer id) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado"));
        estoqueRepository.delete(estoque);
    }

    
    @GetMapping("/filter")
    public Estoque getEstoqueByProductName(@RequestParam String productName) {
        return estoqueRepository.findByProdutoDescricao(productName);
    }

   
    @PatchMapping("/{id}")
    public Estoque patchEstoqueQuantity(@PathVariable Integer id, @RequestParam Integer newQuantity) {
        return estoqueRepository.findById(id)
                .map(existingEstoque -> {
                    existingEstoque.setQuantidade(newQuantity);
                    return estoqueRepository.save(existingEstoque);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado"));
    }
}
