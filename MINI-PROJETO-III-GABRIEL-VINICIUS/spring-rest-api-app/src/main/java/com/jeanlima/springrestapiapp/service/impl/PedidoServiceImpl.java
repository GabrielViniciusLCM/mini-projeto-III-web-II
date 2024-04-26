package com.jeanlima.springrestapiapp.service.impl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jeanlima.springrestapiapp.enums.StatusPedido;
import com.jeanlima.springrestapiapp.exception.PedidoNaoEncontradoException;
import com.jeanlima.springrestapiapp.exception.RegraNegocioException;
import com.jeanlima.springrestapiapp.model.Cliente;
import com.jeanlima.springrestapiapp.model.ItemPedido;
import com.jeanlima.springrestapiapp.model.Pedido;
import com.jeanlima.springrestapiapp.model.Produto;
import com.jeanlima.springrestapiapp.repository.ClienteRepository;
import com.jeanlima.springrestapiapp.repository.ItemPedidoRepository;
import com.jeanlima.springrestapiapp.repository.PedidoRepository;
import com.jeanlima.springrestapiapp.repository.ProdutoRepository;
import com.jeanlima.springrestapiapp.rest.dto.ItemPedidoDTO;
import com.jeanlima.springrestapiapp.rest.dto.PedidoDTO;
import com.jeanlima.springrestapiapp.service.PedidoService;
import java.util.concurrent.atomic.AtomicReference;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    
    private final PedidoRepository repository;
    private final ClienteRepository clientesRepository;
    private final ProdutoRepository produtosRepository;
    private final ItemPedidoRepository itemsPedidoRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itemsPedido = converterItems(pedido, dto.getItems());
        repository.save(pedido);
        itemsPedidoRepository.saveAll(itemsPedido);
        pedido.setItens(itemsPedido);
        return pedido;
    }
    private List<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items) {
        if (items.isEmpty()) {
            throw new RegraNegocioException("Não é possível realizar um pedido sem items.");
        }
    
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
    
        List<ItemPedido> itemPedidos = items
                .stream()
                .map(dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(() -> new RegraNegocioException("Código de produto inválido: " + idProduto));
    
                    if (produto.getQuantidadeEstoque() < dto.getQuantidade()) {
                        throw new RegraNegocioException("Estoque insuficiente para o produto: " + produto.getId());
                    }
    
                    // Subtraindo do estoque
                    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - dto.getQuantidade());
                    produtosRepository.save(produto);
    
                    BigDecimal subtotal = produto.getPreco().multiply(new BigDecimal(dto.getQuantidade()));
                    total.updateAndGet(t -> t.add(subtotal));
    
                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
    
                    return itemPedido;
                })
                .collect(Collectors.toList());
    
        pedido.setTotal(total.get());
        return itemPedidos;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItens(id);
    }
    @Override
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        repository
        .findById(id)
        .map( pedido -> {
            pedido.setStatus(statusPedido);
            return repository.save(pedido);
        }).orElseThrow(() -> new PedidoNaoEncontradoException() );
    }

    @Override
    @Transactional
    public Pedido atualizarPedido(Integer idPedido, PedidoDTO pedidoDTO) {
        Pedido pedido = repository.findById(idPedido).orElseThrow(() -> new PedidoNaoEncontradoException());

        if (pedidoDTO.getCliente() != null) {
            Cliente cliente = clientesRepository.findById(pedidoDTO.getCliente()).orElseThrow(() -> new RegraNegocioException("Cliente inválido."));
            pedido.setCliente(cliente);
        }

        if (pedidoDTO.getItems() != null && !pedidoDTO.getItems().isEmpty()) {
            // Remover itens existentes
            itemsPedidoRepository.deleteByPedidoId(pedido.getId());
            // Adicionar novos itens
            List<ItemPedido> novosItens = converterItems(pedido, pedidoDTO.getItems());
            itemsPedidoRepository.saveAll(novosItens);
            pedido.setItens(novosItens);
        }

        return repository.save(pedido);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Pedido pedido = repository.findById(id)
            .orElseThrow();
        repository.delete(pedido);
    }

    
}
