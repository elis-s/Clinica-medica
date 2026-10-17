package br.ucsal.clinica.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Usuario extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String login;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha; // sempre guardada com BCrypt

    // um usuario pode acumular mais de um perfil
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Enums.Perfil> perfis = new HashSet<>();

    // vinculo opcional com o colaborador (necessario para medicos/atendimentos)
    @ManyToOne
    private Colaborador colaborador;

    @Enumerated(EnumType.STRING)
    private Enums.Status status = Enums.Status.ATIVO;

    @Override
    public void inativar(String motivo) {
        status = Enums.Status.INATIVO;
    }
}
