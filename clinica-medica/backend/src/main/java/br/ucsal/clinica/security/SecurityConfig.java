package br.ucsal.clinica.security;

import br.ucsal.clinica.model.Usuario;
import br.ucsal.clinica.model.Enums;
import br.ucsal.clinica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Autenticacao via BANCO DE DADOS (tabela usuario) - nada em memoria. */
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository repo) {
        return login -> {
            Usuario u = repo.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException(login));
            return User.withUsername(u.getLogin())
                    .password(u.getSenha())
                    .disabled(u.getStatus() == Enums.Status.INATIVO)
                    .roles(u.getPerfis().stream().map(Enum::name).toArray(String[]::new))
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Value("${app.frontend-key}") String chave) throws Exception {
        final String ADM = "ADMINISTRADOR", COL = "COLABORADOR", USU = "USUARIO";
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(b -> b.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(new FrontendOnlyFilter(chave), BasicAuthenticationFilter.class)
            .authorizeHttpRequests(a -> a
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/auth/**").authenticated()
                // consultas liberadas para qualquer usuario logado (usadas nas telas de selecao)
                .requestMatchers(HttpMethod.GET, "/api/colaboradores/**", "/api/convenios/**",
                        "/api/medicacoes/**", "/api/solicitacoes/**").authenticated()
                // ADMINISTRADOR
                .requestMatchers("/api/clinicas/**", "/api/setores/**", "/api/convenios/**", "/api/colaboradores/**",
                        "/api/usuarios/**", "/api/auditoria/**", "/api/relatorios/admin").hasRole(ADM)
                // USUARIO (atendente) - leitura tambem para colaborador
                .requestMatchers(HttpMethod.GET, "/api/pacientes/**", "/api/agendamentos/**", "/api/prontuarios/**")
                        .hasAnyRole(ADM, USU, COL)
                .requestMatchers("/api/pacientes/**", "/api/agendamentos/**").hasAnyRole(ADM, USU)
                .requestMatchers("/api/medicacoes/**").hasAnyRole(ADM, USU)
                .requestMatchers(HttpMethod.PATCH, "/api/solicitacoes/**").hasAnyRole(ADM, USU)
                // COLABORADOR (medico)
                .requestMatchers("/api/atendimentos/**", "/api/relatorios/colaborador").hasAnyRole(ADM, COL)
                .requestMatchers(HttpMethod.POST, "/api/solicitacoes").hasAnyRole(ADM, COL)
                .anyRequest().denyAll());
        return http.build();
    }
}
