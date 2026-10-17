package br.ucsal.clinica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Convenio extends BaseEntity {
    private String razaoSocial;
    private String nomeFantasia;
    private String registroAns;
    @Column(unique = true)
    private String cnpj;
    private String inscricaoMunicipal;
    private String codigoPrestador;
    private String email;
    private String telefone;
    @Enumerated(EnumType.STRING)
    private Enums.Status status = Enums.Status.ATIVO;

    @Override
    public void inativar(String motivo) {
        status = Enums.Status.INATIVO;
    }
}
