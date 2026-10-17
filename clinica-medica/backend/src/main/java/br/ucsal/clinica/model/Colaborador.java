package br.ucsal.clinica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Colaborador extends BaseEntity {
    private String nome;
    @Column(unique = true)
    private String cpf;
    private String email;
    private String telefone;
    @Enumerated(EnumType.STRING)
    private Enums.Funcao funcao;
    private String localTrabalho;
    private String horarioTrabalho;
    private Integer cargaHorariaSemanal;
    // somente para medicos
    private String especialidade;
    private String diasTurnosAtendimento;
    private String conselhoRegional;
    private String registroConselho;
    @Enumerated(EnumType.STRING)
    private Enums.Status status = Enums.Status.ATIVO;

    @Override
    public void inativar(String motivo) {
        status = Enums.Status.INATIVO;
    }
}
