package br.ifsp.demo.application;

import br.ifsp.demo.domain.ISessaoRepository;
import br.ifsp.demo.domain.Sessao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarSessoesService {

    private final ISessaoRepository sessaoRepository;

    public ListarSessoesService(ISessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public List<Sessao> listar() {
        return List.copyOf(sessaoRepository.listar());
    }
}
