package br.ucsal.clinica.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Paciente extends BaseEntity {
    private String nome;
    @Column(unique = true)
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    @ManyToOne
    private Colaborador medico;
    private String especialidade;
    @Enumerated(EnumType.STRING)
    private Enums.Status status = Enums.Status.ATIVO;

    // todo paciente tem exatamente um prontuario (criado junto com o paciente)
    @JsonIgnore
    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL)
    private Prontuario prontuario;

    // o convenio fica guardado no prontuario; aqui serve so para receber/mostrar
    @Transient
    @Getter(AccessLevel.NONE)
    private Convenio convenio;

    public Convenio getConvenio() {
        if (convenio != null) return convenio;
        return prontuario != null ? prontuario.getConvenio() : null;
    }

    @JsonProperty("prontuarioId")
    public Long getProntuarioId() {
        return prontuario != null ? prontuario.getId() : null;
    }

    @Override
    public void inativar(String motivo) {
        status = Enums.Status.INATIVO;
        if (prontuario != null) prontuario.setStatus(Enums.Status.INATIVO);
    }
}
