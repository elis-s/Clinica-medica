package br.ucsal.clinica.security;

import br.ucsal.clinica.model.Colaborador;
import br.ucsal.clinica.model.Enums;
import br.ucsal.clinica.model.Usuario;
import br.ucsal.clinica.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Acesso ao usuario autenticado (o sistema captura quem fez cada operacao a partir do login). */
@Component
public class UsuarioLogado {

    private final UsuarioRepository repo;

    public UsuarioLogado(UsuarioRepository repo) {
        this.repo = repo;
    }

    public String login() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Usuario usuario() {
        return repo.findByLogin(login()).orElseThrow();
    }

    public Colaborador colaborador() {
        Colaborador c = usuario().getColaborador();
        if (c == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seu usuário não está vinculado a um colaborador");
        }
        return c;
    }

    public boolean temPerfil(Enums.Perfil perfil) {
        return usuario().getPerfis().contains(perfil);
    }
}
