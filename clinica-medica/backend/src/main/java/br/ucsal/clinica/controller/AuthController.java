package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Usuario;
import br.ucsal.clinica.security.UsuarioLogado;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioLogado logado;

    public AuthController(UsuarioLogado logado) {
        this.logado = logado;
    }

    /** Usado pelo front para validar o login e descobrir os perfis do usuario. */
    @GetMapping("/me")
    public Map<String, Object> me() {
        Usuario u = logado.usuario();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("login", u.getLogin());
        r.put("perfis", u.getPerfis());
        r.put("colaboradorId", u.getColaborador() != null ? u.getColaborador().getId() : null);
        r.put("nome", u.getColaborador() != null ? u.getColaborador().getNome() : u.getLogin());
        return r;
    }
}
