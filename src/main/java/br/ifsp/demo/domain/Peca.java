package br.ifsp.demo.domain;

import java.time.Duration;
import java.util.UUID;

public class Peca {

    private final UUID id;
    private String titulo;
    private String descricao;
    private Duration duracao;
    private String classificacao;

    public Peca(UUID id, String titulo, String descricao, Duration duracao, String classificacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.duracao = duracao;
        this.classificacao = classificacao;
    }

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Duration getDuracao() {
        return duracao;
    }

    public String getClassificacao() {
        return classificacao;
    }
}
