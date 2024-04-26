package com.jeanlima.springrestapiapp.rest.controllers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.jeanlima.springrestapiapp.model.Cliente;
import com.jeanlima.springrestapiapp.repository.ClienteRepository;



@RequestMapping("/api/clientes")
@RestController //anotação especializadas de controller - todos já anotados com response body!
public class ClienteController {

    @Autowired
    private ClienteRepository clientes;

    @GetMapping("{id}")
    public Cliente getClienteById( @PathVariable Integer id ){
        return clientes
                .findById(id)
                .orElseThrow(() -> //se nao achar lança o erro!
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Cliente não encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente save( @RequestBody Cliente cliente ){
        return clientes.save(cliente);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable Integer id ){
        clientes.findById(id)
                .map( cliente -> {
                    clientes.delete(cliente );
                    return cliente;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado") );

    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update( @PathVariable Integer id,
                        @RequestBody Cliente cliente ){
        clientes
                .findById(id)
                .map( clienteExistente -> {
                    cliente.setId(clienteExistente.getId());
                    clientes.save(cliente);
                    return clienteExistente;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Cliente não encontrado") );
    }

    @GetMapping
    public List<Cliente> find( Cliente filtro ){
        ExampleMatcher matcher = ExampleMatcher
                                    .matching()
                                    .withIgnoreCase()
                                    .withStringMatcher(
                                            ExampleMatcher.StringMatcher.CONTAINING );

        Example example = Example.of(filtro, matcher);
        return clientes.findAll(example);
    }

    @PatchMapping("{id}")
    public Cliente patchCliente(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        Cliente cliente = clientes.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        updates.forEach((k, v) -> {
            Field field = ReflectionUtils.findRequiredField(Cliente.class, k);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, cliente, v);
            }
        });
        return clientes.save(cliente);
    }

    @GetMapping("{id}/pedidos")
    public ResponseEntity<?> getClientePedidos(@PathVariable Integer id) {
        Cliente cliente = clientes.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        List<Map<String, Object>> pedidosInfo = cliente.getPedidos().stream().map(pedido -> {
            Map<String, Object> pedidoMap = new HashMap<>();
            pedidoMap.put("id", pedido.getId());
            pedidoMap.put("itens", pedido.getItens().stream().map(item -> Map.of(
                "descricaoProduto", item.getProduto().getDescricao(),
                "quantidade", item.getQuantidade(),
                "precoUnitario", item.getProduto().getPreco()
            )).collect(Collectors.toList()));
            pedidoMap.put("total", pedido.getTotal());
            return pedidoMap;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(pedidosInfo);
    }



}
