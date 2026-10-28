package br.ifsp.demo.domain;

import java.util.Objects;
import java.util.UUID;

public class Usuario {

    private final UUID id;
    private final String nome;
    private final String email;
    private final String senhaCodificada;

    public Usuario(UUID id, String nome, String email, String senhaCodificada) {
        Objects.requireNonNull(id, "O identificador do usuário é obrigatório");
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("O e-mail do usuário é inválido");
        }
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaCodificada = senhaCodificada;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaCodificada() {
        return senhaCodificada;
    }
}