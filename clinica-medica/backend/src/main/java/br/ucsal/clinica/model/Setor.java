package br.ucsal.clinica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Setor extends BaseEntity {
    private String nome;
    private String email;
    private String telefone;
    // um unico responsavel por setor
    @ManyToOne(optional = false)
    private Colaborador responsavel;
    // todo setor pertence a uma unica clinica
    @ManyToOne(optional = false)
    private Clinica clinica;
    @Enumerated(EnumType.STRING)
    private Enums.Status status = Enums.Status.ATIVO;

    @Override
    public void inativar(String motivo) {
        status = Enums.Status.INATIVO;
    }
}
