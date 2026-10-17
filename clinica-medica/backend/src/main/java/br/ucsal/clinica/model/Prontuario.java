package br.ucsal.clinica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Prontuario extends BaseEntity {
    // nao existe prontuario sem paciente
    @OneToOne(optional = false)
    @JoinColumn(unique = true)
    private Paciente paciente;

    // informacoes do convenio fazem parte do prontuario
    @ManyToOne
    private Convenio convenio;

    @Enumerated(EnumType.STRING)
    private Enums.Status status = Enums.Status.ATIVO;

    @Override
    public void inativar(String motivo) {
        status = Enums.Status.INATIVO;
    }
}
