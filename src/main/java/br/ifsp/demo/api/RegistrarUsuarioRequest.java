package br.ifsp.demo.api;

public record RegistrarUsuarioRequest(String nome, String email, String senha) {
}