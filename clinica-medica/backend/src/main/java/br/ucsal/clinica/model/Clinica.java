package br.ucsal.clinica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Clinica extends BaseEntity {
    private String razaoSocial;
    private String nomeFantasia;
    @Column(unique = true)
    private String cnpj;
    private String rua;
    private String bairro;
    private String cidade;
    private String estado;
    private String pais;
    private String cep;
    private String email;
    private String telefones;
    private String proprietarios;
    private String atividadePrincipal;
    @Column(length = 1000)
    private String missao;
    private String alvara;
    private String licencaSanitaria;
    private String registroCrm;
    private String diretorTecnico;
    @Enumerated(EnumType.STRING)
    private Enums.Status status = Enums.Status.ATIVO;
    private String motivoInativacao;

    @Override
    public void inativar(String motivo) {
        status = Enums.Status.INATIVO;
        motivoInativacao = motivo;
    }
}
