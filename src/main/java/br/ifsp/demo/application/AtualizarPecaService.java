package br.ifsp.demo.application;

import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.Peca;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AtualizarPecaService {

    private final IPecaRepository pecaRepository;

    public AtualizarPecaService(IPecaRepository pecaRepository) {
        this.pecaRepository = pecaRepository;
    }

    public Peca atualizar(UUID id, String titulo, String descricao, Duration duracao, String classificacao) {
        Peca peca = pecaRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Peça não encontrada"));
        peca.atualizar(titulo, descricao, duracao, classificacao);
        pecaRepository.atualizar(peca);
        return peca;
    }
}
