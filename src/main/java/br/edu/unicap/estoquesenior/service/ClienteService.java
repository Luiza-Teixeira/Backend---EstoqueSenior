package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.model.Cliente;
import br.edu.unicap.estoquesenior.repository.ClienteRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado: " + id));
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente dadosAtualizados) {
        Cliente clienteExistente = buscarPorId(id);

        clienteExistente.setNome(dadosAtualizados.getNome());
        clienteExistente.setTelefone(dadosAtualizados.getTelefone());
        clienteExistente.setEndereco(dadosAtualizados.getEndereco());

        return clienteRepository.save(clienteExistente);
    }

    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }
}