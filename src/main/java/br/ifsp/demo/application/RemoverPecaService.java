package br.ifsp.demo.application;

import br.ifsp.demo.domain.IPecaRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class RemoverPecaService {

    private final IPecaRepository pecaRepository;

    public RemoverPecaService(IPecaRepository pecaRepository) {
        this.pecaRepository = pecaRepository;
    }

    public void remover(UUID id) {
        if (!pecaRepository.existe(id)) {
            throw new NoSuchElementException("Peça não encontrada");
        }
        if (pecaRepository.possuiSessoes(id)) {
            throw new IllegalStateException("Remova as sessões desta peça antes de removê-la.");
        }
        pecaRepository.remover(id);
    }
}
