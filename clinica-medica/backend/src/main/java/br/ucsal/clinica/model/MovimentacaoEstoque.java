package br.ucsal.clinica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class MovimentacaoEstoque extends BaseEntity {
    @ManyToOne(optional = false)
    private Medicacao medicacao;
    @Enumerated(EnumType.STRING)
    private Enums.TipoMovimentacao tipo;
    private int quantidade;
    private LocalDateTime dataHora;
    // colaborador que solicitou (apenas nas saidas)
    @ManyToOne
    private Colaborador colaboradorSolicitante;
    // login de quem entregou/registrou (capturado do usuario autenticado)
    private String responsavel;
}
