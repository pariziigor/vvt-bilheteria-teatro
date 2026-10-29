package br.ifsp.demo.application;

import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.Peca;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class CadastrarPecaService {

    private final IPecaRepository pecaRepository;

    public CadastrarPecaService(IPecaRepository pecaRepository) {
        this.pecaRepository = pecaRepository;
    }

    public Peca cadastrar(String titulo, String descricao, Duration duracao, String classificacao) {
        Peca peca = new Peca(UUID.randomUUID(), titulo, descricao, duracao, classificacao);
        pecaRepository.salvar(peca);
        return peca;
    }
}