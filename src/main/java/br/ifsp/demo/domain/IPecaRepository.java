package br.ifsp.demo.domain;

import java.util.UUID;

public interface IPecaRepository {

    boolean existe(UUID id);
}
