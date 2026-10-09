package br.ifsp.demo.domain;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface ISessaoRepository {

    void salvar(Sessao sessao);

    Optional<Sessao> buscarPorId(UUID id);

    List<Sessao> listar();
}
