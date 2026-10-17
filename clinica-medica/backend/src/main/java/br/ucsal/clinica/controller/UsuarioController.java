package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Usuario;
import br.ucsal.clinica.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.ucsal.clinica.Regra.exigir;
import static br.ucsal.clinica.Regra.preenchido;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController extends CrudController<Usuario> {

    private final PasswordEncoder encoder;

    public UsuarioController(UsuarioRepository repo, PasswordEncoder encoder) {
        super(repo);
        this.encoder = encoder;
    }

    @Override
    protected void validar(Usuario u) {
        exigir(preenchido(u.getLogin()), "Login é obrigatório");
        exigir(u.getPerfis() != null && !u.getPerfis().isEmpty(), "Informe ao menos um perfil");
    }

    @Override
    protected void antesDeCriar(Usuario u) {
        exigir(preenchido(u.getSenha()), "Senha é obrigatória");
        u.setSenha(encoder.encode(u.getSenha()));
    }

    @Override
    protected void antesDeEditar(Usuario novo, Usuario atual) {
        // senha em branco = mantem a atual
        novo.setSenha(preenchido(novo.getSenha()) ? encoder.encode(novo.getSenha()) : atual.getSenha());
    }
}
