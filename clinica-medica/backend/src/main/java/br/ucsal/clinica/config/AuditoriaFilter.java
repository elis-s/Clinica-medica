package br.ucsal.clinica.config;

import br.ucsal.clinica.model.Auditoria;
import br.ucsal.clinica.repository.AuditoriaRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/** Registra no banco todo login e toda operacao de escrita (POST/PUT/PATCH) feita por usuario autenticado. */
@Component
public class AuditoriaFilter extends OncePerRequestFilter {

    private final AuditoriaRepository repo;

    public AuditoriaFilter(AuditoriaRepository repo) {
        this.repo = repo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        chain.doFilter(req, res);

        String metodo = req.getMethod();
        String caminho = req.getRequestURI();
        boolean login = "GET".equals(metodo) && caminho.equals("/api/auth/me");
        boolean escrita = !"GET".equals(metodo) && !"OPTIONS".equals(metodo) && !"HEAD".equals(metodo);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if ((login || escrita) && auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Auditoria a = new Auditoria();
            a.setUsuario(auth.getName());
            a.setAcao(login ? "LOGIN" : metodo);
            a.setCaminho(caminho);
            a.setStatusHttp(res.getStatus());
            a.setDataHora(LocalDateTime.now());
            repo.save(a);
        }
    }
}
