package br.ucsal.clinica.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Garante que o BACK-END so responda ao FRONT-END:
 * qualquer requisicao sem a chave do front (header X-Frontend-Key) e barrada e descartada.
 */
public class FrontendOnlyFilter extends OncePerRequestFilter {

    private final String chave;

    public FrontendOnlyFilter(String chave) {
        this.chave = chave;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (chave.equals(req.getHeader("X-Frontend-Key"))) {
            chain.doFilter(req, res);
            return;
        }
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"message\":\"Acesso permitido somente pelo front-end\"}");
    }
}
