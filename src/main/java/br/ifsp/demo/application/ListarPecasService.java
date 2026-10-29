package br.ifsp.demo.application;

import br.ifsp.demo.domain.IPecaRepository;
import br.ifsp.demo.domain.Peca;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarPecasService {

    private final IPecaRepository pecaRepository;

    public ListarPecasService(IPecaRepository pecaRepository) {
        this.pecaRepository = pecaRepository;
    }

    public List<Peca> listar() {
        return List.copyOf(pecaRepository.listar());
    }
}