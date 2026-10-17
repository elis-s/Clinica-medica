package br.ucsal.clinica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Agendamento extends BaseEntity {
    @ManyToOne(optional = false)
    private Colaborador colaborador;
    @ManyToOne(optional = false)
    private Paciente paciente;
    private LocalDate dataConsulta;
    private LocalTime horaConsulta;
    @Enumerated(EnumType.STRING)
    private Enums.StatusAgendamento status = Enums.StatusAgendamento.AGENDADO;

    @Override
    public void inativar(String motivo) {
        status = Enums.StatusAgendamento.INATIVO;
    }
}
