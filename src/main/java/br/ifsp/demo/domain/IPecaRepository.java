package br.ifsp.demo.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPecaRepository {

    boolean existe(UUID id);

    Optional<Peca> buscarPorId(UUID id);

    boolean possuiSessoes(UUID id);

    void salvar(Peca peca);

    void atualizar(Peca peca);

    void remover(UUID id);

    List<Peca> listar();
}
