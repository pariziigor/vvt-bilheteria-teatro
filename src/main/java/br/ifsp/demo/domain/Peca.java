package br.ifsp.demo.domain;

import java.time.Duration;
import java.util.UUID;
import java.util.Objects;

public class Peca {

    private final UUID id;
    private String titulo;
    private String descricao;
    private Duration duracao;
    private String classificacao;

    public Peca(UUID id, String titulo, String descricao, Duration duracao, String classificacao) {
        Objects.requireNonNull(id, "O identificador da peça é obrigatório");
        if (titulo == null || titulo.isBlank() || descricao == null || descricao.isBlank()
                || classificacao == null || classificacao.isBlank()) {
            throw new IllegalArgumentException("Os dados textuais da peça são obrigatórios");
        }
        if (duracao == null || duracao.isZero() || duracao.isNegative()) {
            throw new IllegalArgumentException("A duração da peça deve ser positiva");
        }
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
