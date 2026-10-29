package br.ifsp.demo.domain;

import java.util.List;
import java.util.UUID;

public interface IPecaRepository {

    boolean existe(UUID id);

    void salvar(Peca peca);

    List<Peca> listar();
}