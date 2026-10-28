package br.ifsp.demo.domain;

import java.util.Optional;

public interface IUsuarioRepository {

    void salvar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);
}
