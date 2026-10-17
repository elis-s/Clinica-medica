package br.ucsal.clinica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class SolicitacaoMedicacao extends BaseEntity {
    @ManyToOne(optional = false)
    private Medicacao medicacao;
    private int quantidade;
    @Column(length = 1000)
    private String justificativa;
    @ManyToOne(optional = false)
    private Colaborador solicitante;
    @Enumerated(EnumType.STRING)
    private Enums.StatusSolicitacao status = Enums.StatusSolicitacao.PENDENTE;
    private LocalDateTime dataHoraEntrega;
    private String entreguePor;
}
