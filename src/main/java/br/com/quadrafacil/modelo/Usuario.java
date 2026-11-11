package br.com.quadrafacil.modelo;

import java.util.UUID;

/** Modelo conceitual inicial; ainda não é uma entidade persistida. */
public record Usuario(UUID id, String nome, String email, Perfil perfil) {
    public enum Perfil { CLIENTE, PROPRIETARIO }
}
