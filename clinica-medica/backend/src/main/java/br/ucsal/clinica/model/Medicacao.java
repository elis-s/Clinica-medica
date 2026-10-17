package br.ucsal.clinica.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Medicacao extends BaseEntity {
    private String nome;
    @Column(length = 2000)
    private String descricao;
    private String fornecedor;
    @Enumerated(EnumType.STRING)
    private Enums.Armazenamento armazenamento;
    private int quantidadeEstoque;
    private int estoqueMinimo = 10;
    private LocalDate dataValidade;
    private LocalDate dataAquisicao;
    @Enumerated(EnumType.STRING)
    private Enums.Status status = Enums.Status.ATIVO;

    /** Alerta de reposicao: saldo atingiu o minimo. */
    @JsonProperty("precisaReposicao")
    public boolean precisaReposicao() {
        return quantidadeEstoque <= estoqueMinimo;
    }

    @Override
    public void inativar(String motivo) {
        status = Enums.Status.INATIVO;
    }
}
