package br.ucsal.clinica;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Utilitario simples para validar regras de negocio (retorna 400 com mensagem). */
public final class Regra {
    private Regra() {}

    public static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }

    public static boolean preenchido(String s) {
        return s != null && !s.isBlank();
    }
}
