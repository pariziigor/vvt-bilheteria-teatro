package br.ifsp.demo.application;

import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import java.util.NoSuchElementException;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BuscarSessaoService {

    private final ISessaoRepository repository;

    public BuscarSessaoService(ISessaoRepository repository) {
        this.repository = repository;
    }

    public Sessao buscar(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador da sessão é obrigatório");
        }
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Sessão não encontrada"));
    }
}