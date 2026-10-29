package br.ifsp.demo.api;

public record CadastrarPecaRequest(String titulo, String descricao, int duracaoMinutos, String classificacao) {
}