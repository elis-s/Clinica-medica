package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Agendamento;
import br.ucsal.clinica.model.Enums;
import br.ucsal.clinica.model.Paciente;
import br.ucsal.clinica.model.Prontuario;
import br.ucsal.clinica.repository.AgendamentoRepository;
import br.ucsal.clinica.repository.PacienteRepository;
import br.ucsal.clinica.repository.ProntuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

import static br.ucsal.clinica.Regra.exigir;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController extends CrudController<Agendamento> {

    private final PacienteRepository pacientes;
    private final ProntuarioRepository prontuarios;

    public AgendamentoController(AgendamentoRepository repo, PacienteRepository pacientes, ProntuarioRepository prontuarios) {
        super(repo);
        this.pacientes = pacientes;
        this.prontuarios = prontuarios;
    }

    @Override
    protected void validar(Agendamento a) {
        exigir(a.getColaborador() != null && a.getColaborador().getId() != null, "Informe o colaborador (médico)");
        exigir(a.getPaciente() != null && a.getPaciente().getId() != null, "Informe o paciente");
        exigir(a.getDataConsulta() != null && a.getHoraConsulta() != null, "Informe data e hora da consulta");
    }

    @Override
    protected void antesDeCriar(Agendamento a) {
        Paciente p = pacientes.findById(a.getPaciente().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paciente não encontrado"));
        exigir(p.getStatus() == Enums.Status.ATIVO, "Paciente inativo não pode ser agendado");
        a.setStatus(Enums.StatusAgendamento.AGENDADO); // sempre inicia como "agendado"
    }

    /** Recepcao: usuario obtem os dados do agendamento e o prontuario do paciente. */
    @GetMapping("/{id}/recepcao")
    public Map<String, Object> recepcao(@PathVariable Long id) {
        Agendamento a = buscar(id);
        Prontuario pr = prontuarios.findByPacienteId(a.getPaciente().getId()).orElse(null);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("agendamento", a);
        r.put("prontuario", pr);
        return r;
    }
}
