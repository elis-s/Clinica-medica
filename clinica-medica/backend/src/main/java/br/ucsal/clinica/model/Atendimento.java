package br.ucsal.clinica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Atendimento extends BaseEntity {
    @ManyToOne(optional = false)
    private Prontuario prontuario;
    // capturado pelo sistema a partir do login
    @ManyToOne(optional = false)
    private Colaborador colaborador;
    @ManyToOne
    private Agendamento agendamento;
    @Enumerated(EnumType.STRING)
    private Enums.TipoAtendimento tipo;
    private LocalDateTime dataHoraInicio;
    @Column(length = 2000)
    private String sintomas;
    @Column(length = 2000)
    private String diagnostico;
    @Column(length = 2000)
    private String tratamento;
    private String medicacaoIndicada;
    private String dosagem;
    private LocalDateTime dataHoraEncerramento;
    @Column(length = 2000)
    private String orientacoesEnfermagem;
    @Enumerated(EnumType.STRING)
    private Enums.StatusAtendimento status = Enums.StatusAtendimento.EM_ANDAMENTO;
}
