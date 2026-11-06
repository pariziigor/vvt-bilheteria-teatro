package br.ifsp.demo.api;

public record AtualizarPecaRequest(String titulo, String descricao, long duracaoMinutos, String classificacao) {
}
