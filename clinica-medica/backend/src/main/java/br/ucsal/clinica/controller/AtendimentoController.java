package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.*;
import br.ucsal.clinica.repository.AgendamentoRepository;
import br.ucsal.clinica.repository.AtendimentoRepository;
import br.ucsal.clinica.repository.ProntuarioRepository;
import br.ucsal.clinica.security.UsuarioLogado;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static br.ucsal.clinica.Regra.exigir;

@RestController
@RequestMapping("/api/atendimentos")
public class AtendimentoController {

    private final AtendimentoRepository repo;
    private final ProntuarioRepository prontuarios;
    private final AgendamentoRepository agendamentos;
    private final UsuarioLogado logado;

    public AtendimentoController(AtendimentoRepository repo, ProntuarioRepository prontuarios,
                                 AgendamentoRepository agendamentos, UsuarioLogado logado) {
        this.repo = repo;
        this.prontuarios = prontuarios;
        this.agendamentos = agendamentos;
        this.logado = logado;
    }

    private boolean admin() {
        return logado.temPerfil(Enums.Perfil.ADMINISTRADOR);
    }

    /** Admin ve todos; colaborador ve apenas os seus. */
    @GetMapping
    public List<Atendimento> listar() {
        if (admin()) return repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return repo.findByColaboradorIdOrderByIdDesc(logado.colaborador().getId());
    }

    @PostMapping
    public Atendimento criar(@RequestBody Atendimento a) {
        Colaborador medico = logado.colaborador(); // colaborador capturado pelo sistema
        exigir(medico.getFuncao() == Enums.Funcao.MEDICO, "Somente médicos registram atendimentos");
        exigir(a.getProntuario() != null && a.getProntuario().getId() != null, "Informe o prontuário");
        exigir(a.getTipo() != null, "Informe o tipo de atendimento");
        Prontuario p = prontuarios.findById(a.getProntuario().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prontuário não encontrado"));
        exigir(p.getStatus() == Enums.Status.ATIVO, "Prontuário inativo: paciente com restrição de atendimento");

        Agendamento ag = null;
        if (a.getAgendamento() != null && a.getAgendamento().getId() != null) {
            ag = agendamentos.findById(a.getAgendamento().getId()).orElse(null);
        }
        a.setId(null);
        a.setProntuario(p);
        a.setColaborador(medico);
        a.setAgendamento(ag);
        a.setDataHoraInicio(a.getDataHoraInicio() != null ? a.getDataHoraInicio() : LocalDateTime.now());
        a.setDataHoraEncerramento(null);
        a.setStatus(Enums.StatusAtendimento.EM_ANDAMENTO);
        return repo.save(a);
    }

    private Atendimento doColaborador(Long id) {
        Atendimento a = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atendimento não encontrado"));
        if (!admin()) {
            exigir(a.getColaborador().getId().equals(logado.colaborador().getId()), "Este atendimento pertence a outro colaborador");
        }
        return a;
    }

    @PutMapping("/{id}")
    public Atendimento editar(@PathVariable Long id, @RequestBody Atendimento n) {
        Atendimento a = doColaborador(id);
        exigir(a.getStatus() == Enums.StatusAtendimento.EM_ANDAMENTO, "Atendimento já encerrado");
        a.setTipo(n.getTipo() != null ? n.getTipo() : a.getTipo());
        a.setSintomas(n.getSintomas());
        a.setDiagnostico(n.getDiagnostico());
        a.setTratamento(n.getTratamento());
        a.setMedicacaoIndicada(n.getMedicacaoIndicada());
        a.setDosagem(n.getDosagem());
        a.setOrientacoesEnfermagem(n.getOrientacoesEnfermagem());
        return repo.save(a);
    }

    /** Encerra o atendimento (liberacao do paciente) e finaliza o agendamento vinculado. */
    @PatchMapping("/{id}/encerrar")
    public Atendimento encerrar(@PathVariable Long id) {
        Atendimento a = doColaborador(id);
        exigir(a.getStatus() == Enums.StatusAtendimento.EM_ANDAMENTO, "Atendimento já encerrado");
        a.setDataHoraEncerramento(LocalDateTime.now());
        a.setStatus(Enums.StatusAtendimento.ENCERRADO);
        if (a.getAgendamento() != null) {
            a.getAgendamento().setStatus(Enums.StatusAgendamento.FINALIZADO);
            agendamentos.save(a.getAgendamento());
        }
        return repo.save(a);
    }
}
